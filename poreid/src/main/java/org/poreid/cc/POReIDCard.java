/*
 * The MIT License
 *
 * Copyright 2014, 2015, 2016 Rui Martinho (rmartinho@gmail.com), António Braz (antoniocbraz@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.poreid.cc;


import org.poreid.pcscforjava.Card;
import org.poreid.pcscforjava.CardChannel;
import org.poreid.pcscforjava.CardException;
import org.poreid.pcscforjava.CommandAPDU;
import static org.poreid.pcscforjava.PCSCDefines.SCARD_RESET_CARD;
import org.poreid.pcscforjava.ResponseAPDU;
import org.poreid.Pin;
import org.poreid.TerminalFeatures;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.poreid.POReIDSmartCard;
import org.poreid.SmartCardFile;
import org.poreid.SmartCardFileCache;
import org.poreid.CertificateChainNotFound;
import org.poreid.CertificateNotFound;
import org.poreid.KeepAlive;
import org.poreid.POReIDException;
import org.poreid.PinStatus;
import org.poreid.SecurityStatusNotSatisfiedException;
import org.poreid.common.Util;
import org.poreid.dialogs.dialog.DialogController;
import org.poreid.dialogs.pindialogs.blockedpin.BlockedPinDialogController;
import org.poreid.dialogs.pindialogs.PinBlockedException;
import org.poreid.dialogs.pindialogs.PinEntryCancelledException;
import org.poreid.dialogs.pindialogs.PinTimeoutException;
import org.poreid.dialogs.pindialogs.modifypin.ModifyPinDialogController;
import org.poreid.dialogs.pindialogs.usepinpad.PinOperation;
import org.poreid.dialogs.pindialogs.verifypin.VerifyPinDialogController;
import org.poreid.dialogs.pindialogs.usepinpad.UsePinPadDialogController;
import org.poreid.dialogs.pindialogs.wrongpin.WrongPinDialogController;
import org.poreid.pcscforjava.PCSCDefines;
import org.poreid.pcscforjava.PCSCErrorValues;

/**
 *
 * @author POReID
 */
public abstract class POReIDCard implements POReIDSmartCard {
    private final String escudoPortugues = "/org/poreid/images/escudo.png"; // icone especifico para o cc
    private final String poreidKeystore = "/org/poreid/cc/keystores/poreid.cc.ks";
    
    private final CardSpecificReferences csr;
    private final org.poreid.CacheStatus cacheStatus;
    private final Card card;
    private final CardChannel channel;
    private final String aid;
    private String cardPan = null;
    private CertificateFactory certificateFactory = null;
    private final int BLOCK_SIZE_READ = 0x100;
    private final int BLOCK_SIZE_WRITE = 0xF8;
    private final int RETRY_COUNT = 3;
	protected final int NO_FCI = -1;
    private SmartCardFileCache fileCache;
    private final TerminalFeatures terminalFeatures;
    private final Locale locale;
    private final Files files;
    private final ResourceBundle bundle;
    private boolean otpPinChanging;
    private boolean locked;
    
    protected POReIDCard(CardSpecificReferences csr, org.poreid.CacheStatus cacheStatus) {
        this.csr = csr;
        this.cacheStatus = cacheStatus;
        this.card = csr.getCard();
        this.aid = csr.getAID();
        this.locale = csr.getLocale();
        this.files = new Files(csr);
        this.channel = this.card.getBasicChannel();        
        this.terminalFeatures = TerminalFeatures.getInstance(card, csr.getCardReaderName());
        this.bundle = CCConfig.getBundle(POReIDCard.class.getSimpleName(),locale);
    }
    
    
    protected abstract int selectFile(String fileId) throws POReIDException;
    protected abstract byte[] getModifyPinAPDU(Pin pin);
    protected abstract byte[] getNFillModifyPinAPDU(Pin pin, byte[][] pins);
    protected abstract boolean verifyToModify();

    
    private byte[] readBinary(int offset, int size) throws IOException, SecurityStatusNotSatisfiedException, POReIDException {
        boolean availableFCI = (size != NO_FCI);        
        int blockSize = (!availableFCI || size > BLOCK_SIZE_READ ? BLOCK_SIZE_READ : size);
        
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            byte[] chunk = new byte[0];
            int retry = 0;
            
            do {                
                CommandAPDU readBinaryApdu = new CommandAPDU(0x00, 0xB0, offset >> 8, offset & 0xFF, (availableFCI ? (BLOCK_SIZE_READ > size ? size : BLOCK_SIZE_READ) : blockSize));
                ResponseAPDU responseApdu = channel.transmit(readBinaryApdu, true, true);
                int sw = responseApdu.getSW();

                if (0x6B00 == sw) {
                    break;
                }
                
                if (0x6982 == sw) {
                    throw new SecurityStatusNotSatisfiedException("Necessário fornecer pin para utilizar recurso.");
                }
                
                if (0x9000 != sw) {
                    if (!card.isValid() || retry > RETRY_COUNT) {
                        byte[] apdu = responseApdu.getBytes();                                
                        throw new IOException("Código de estado não esperado: [" + Integer.toHexString(responseApdu.getSW())+"] - ["+responseApdu.getSW()+"], responseAPDU size: "+ apdu.length + ", sw1 = "+(apdu[apdu.length - 2] & 0xff)+", sw2 = "+(apdu[apdu.length - 1] & 0xff)+", retry = "+retry+", data size = "+data.size());
                    } else {
                        retry++;
                        continue;
                    }
                }
                
                chunk = responseApdu.getData();
                data.write(chunk);
                offset += chunk.length;
                size -= chunk.length;                
            } while (BLOCK_SIZE_READ == chunk.length || retry != 0);
            
            return data.toByteArray();
        } catch (CardException ex) {
            throw new POReIDException(ex);
        }
    }
    
    
    @Override
    public final boolean verifyPin(Pin pin, byte[] pinCode) throws PinTimeoutException, PinEntryCancelledException, PinBlockedException, POReIDException {       
        if (!CCConfig.isExternalPinCachePermitted() && !otpPinChanging){
            pinCode = null;
        }
        
        return internalVerifyPin(pin, pinCode);
    }
    
    
    private boolean internalVerifyPin(Pin pin, byte[] pinCode) throws PinTimeoutException, PinEntryCancelledException, PinBlockedException, POReIDException {
        ResponseAPDU responseApdu;
        boolean pinOk = false;
        int pinTriesLeft=-1;
        
        checkPinTries(pin, pinTriesLeft);
        
        do {
            responseApdu = resolveReaderPinpadSupportVerifyPin(pin, pinCode);

            switch (responseApdu.getSW()) {
                case 0x9000:
                    pinOk = true;                    
                    break;
                case 0x6400:
                    throw new PinTimeoutException(pin.getLabel()+ " não foi inserido dentro do tempo regular definido pelo leitor");
                case 0x6401:
                    throw new PinEntryCancelledException("Introdução do " + pin.getLabel() + " cancelada pelo utilizador.");
                case 0x6983:    // Referenced PIN not successfully verified AND no subsequent tries are allowed (remaining tries counter reached 0)
                case 0x6984:    // Referenced PIN remaining tries counter or usage counter reached 0
                case 0x6402:
                    BlockedPinDialogController.getInstance(pin.getLabel(), locale).displayBlockedPinDialog(csr.getStartTime());
                    throw new PinBlockedException("O " + pin.getLabel() + "está bloqueado.");
                case 0x6403:
                    DialogController.getInstance(bundle.getString("pin.issue.title"), MessageFormat.format(bundle.getString("pin.issue.message"), pin.getLabel()), locale, true).displayDialog(csr.getStartTime());
                    throw new POReIDException("O leitor de cartões indica que o " + pin.getLabel() + "introduzido não tem o tamanho esperado.");
                default:
                    if ((responseApdu.getSW() & (int) 0xfffffff0) == 0x63C0) {
                        pinTriesLeft = responseApdu.getSW2() & 0x0f;
                    } else {
                        throw new POReIDException("Código de estado não esperado: " + Integer.toHexString(responseApdu.getSW()));
                    }
                    if (WrongPinDialogController.getInstance(pin.getLabel(), pinTriesLeft, locale).displayWrongPinDialog(csr.getStartTime())) {
                        throw new PinEntryCancelledException("Introdução do " + pin.getLabel() + " cancelada pelo utilizador.");
                    }
            }
        } while (true != pinOk && null == pinCode);
        
        return pinOk;
    }
    
    
    private void goToPinKeyPath(Pin pin) throws POReIDException {
        boolean recover = true;
        boolean opComplete = false;

        do {
            try {
                if (null != pin.getKeyPath()) {
                    if (channel.transmit(new CommandAPDU(Util.hexToBytes(pin.getKeyPath())), true, true).getSW() != 0x9000) {
                        throw new POReIDException("Erro " + pin.getLabel() + " Key Path " + pin.getKeyPath());
                    }
                }
                opComplete = true;
            } catch (CardException ex) {
                if (recover) {
                    reconnect(ex);
                    recover = false;
                } else {
                    throw new POReIDException("Erro " + pin.getLabel() + " Key Path " + pin.getKeyPath(), ex);
                }
            }
        } while (!opComplete);
    }
    
    
    private void checkPinTries(Pin pin, int pinTriesLeft) throws POReIDException, PinBlockedException {
        PinStatus pinStatus;
        
        if (-1 == pinTriesLeft) {
            pinStatus = getPinStatus(pin);
            if (pinStatus.isPinStatusAvailable()){
                pinTriesLeft = pinStatus.availableTries();
            }
        }

        if (0 == pinTriesLeft) {
            BlockedPinDialogController.getInstance(pin.getLabel(), locale).displayBlockedPinDialog(csr.getStartTime());
            throw new PinBlockedException("O " + pin.getLabel() + " encontra-se bloqueado");
        }        
    }
    
    
    private ResponseAPDU resolveReaderPinpadSupportVerifyPin(Pin pin, byte[] pinCode) throws PinEntryCancelledException, PinTimeoutException, POReIDException {
        ResponseAPDU responseApdu;

        if (null != pinCode && terminalFeatures.canBypassPinpad()) {
            responseApdu = verifyPinWithoutPinPad(pin, pinCode);
        } else {
            if (terminalFeatures.isVerifyPinThroughPinpadAvailable()) {
                if (terminalFeatures.isVerifyPinThroughPinpadSupported(this.getClass().getName())) {
                    responseApdu = verifyPinWithPinPad(pin);
                } else {
                    if (terminalFeatures.canBypassPinpad()) {                        
                        responseApdu = verifyPinWithoutPinPad(pin, null);
                    } else {
                        DialogController.getInstance(bundle.getString("incompatible.title"), MessageFormat.format(bundle.getString("incompatible.message.verify.error"), pin.getLabel()), locale, true).displayDialog(csr.getStartTime());
                        throw new POReIDException("O leitor de cartões não permite a realização da operação de verificação do " + pin.getLabel());
                    }                    
                }
            } else {
                responseApdu = verifyPinWithoutPinPad(pin, null);
            }
        }
        
        return responseApdu;
    }
    
    
    private ResponseAPDU verifyPinWithPinPad(Pin pin) throws POReIDException {
        UsePinPadDialogController dialogCtl = UsePinPadDialogController.getInstance(PinOperation.VERIFICACAO, pin, locale);

        dialogCtl.displayVerifyPinPinPadDialog();
        try {
            return new ResponseAPDU(terminalFeatures.transmitVerifyPinDirect(CCConfig.TIMEOUT, (byte) pin.getMinLength(), (byte) pin.getMaxLength(), getVerifyPinAPDU(pin)));
        } finally {
            dialogCtl.disposeVerifyPinPinPadDialog();
        }
    }
    
    
    private ResponseAPDU verifyPinWithoutPinPad(Pin pin, byte[] pinCode) throws PinEntryCancelledException, PinTimeoutException, POReIDException {
        byte[] pcode = new byte[8];
        byte[] internal = null;
        ResponseAPDU responseApdu;        
        ScheduledExecutorService scheduledExecutorService;
        
        try {            
            Arrays.fill(pcode, pin.getPadChar());
            if (null == pinCode || 0 == pinCode.length) {
                
                scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                scheduledExecutorService.scheduleAtFixedRate(new KeepAlive(card), 0, 500, TimeUnit.MILLISECONDS);              
                
                if (terminalFeatures.isVerifyPinThroughPinpadAvailable() && terminalFeatures.canBypassPinpad()) {
                    DialogController.getInstance(bundle.getString("incompatible.title"), MessageFormat.format(bundle.getString("incompatible.message.verify.ok"), pin.getLabel()), locale, false).displayDialog(csr.getStartTime());
                }
                    
                try {
                    internal = VerifyPinDialogController.getInstance(CCConfig.TIMEOUT, pin, locale).askForPin();
                    System.arraycopy(internal, 0, pcode, 0, internal.length);
                } finally {                    
                        scheduledExecutorService.shutdown();
                        try {
                            scheduledExecutorService.awaitTermination(250, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException ignore) {
                        }                    
                }                                                                
            } else {
                System.arraycopy(pinCode, 0, pcode, 0, pinCode.length);
            }
                                    
            responseApdu = channel.transmit(new CommandAPDU(0x00, 0x20, 0x00, (byte) pin.getReference(), pcode), true, true);   
        } catch (CardException | IllegalStateException ex) {
            throw new POReIDException(ex);
        } finally {
            if (null != internal) {
                Arrays.fill(internal, pin.getPadChar());
            }    
            Arrays.fill(pcode, pin.getPadChar());
        }

        return responseApdu;
    }
 
    
    public final CardSpecificReferences getCardSpecificReferences(){
        return csr;
    }
    
    
    protected final void writeFile(SmartCardFile file, byte[] data) throws POReIDException, PinEntryCancelledException, PinBlockedException, PinTimeoutException {
        boolean writeComplete = false;

        try {
            loadData();
            selectFile(file.getFileId());
            do {
                try {
                    updateBinary(0, data);
                    writeComplete = true;
                } catch (SecurityStatusNotSatisfiedException ex) {
                    if (null != file.getPin()) {
                        verifyPin(file.getPin(), null);
                        selectFile(file.getFileId());
                    } else {
                        throw new POReIDException("Erro não esperado", ex);
                    }
                }
            } while (!writeComplete);

        } catch (CardException | IllegalStateException ex) {
            throw new POReIDException(ex);
        }
    }
    
    
    private void updateBinary(int offset_, byte[] data) throws CardException, SecurityStatusNotSatisfiedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int offset = offset_;
        int writeSize;
        int totalSize = data.length; 
        byte b[] = new byte[240];
        do {
            Arrays.fill(b, (byte)0);

            writeSize = (BLOCK_SIZE_WRITE > totalSize ? totalSize : BLOCK_SIZE_WRITE) & 0xFF;
            baos.write(data, offset, writeSize);
            CommandAPDU updateBinaryApdu = new CommandAPDU(0x00, 0xD6, offset >> 8, offset & 0xFF, baos.toByteArray());
            baos.reset();
            Util.prettyPrintBytesToHex(updateBinaryApdu.getBytes());
            ResponseAPDU responseApdu = channel.transmit(updateBinaryApdu, true, true);
            int sw = responseApdu.getSW();
            
            if (0x6982 == sw) {
                throw new SecurityStatusNotSatisfiedException("Necessário fornecer pin para utilizar recurso.");
            }
            
            if (0x9000 != sw) {
                throw new CardException("Código de estado não esperado: " + Integer.toHexString(responseApdu.getSW()));
            }

            offset += writeSize;
            totalSize -= writeSize;
        } while (BLOCK_SIZE_WRITE == writeSize);
    }
    
    
    final Files getFileDescription(){
        return files;
    }

    protected final byte[] readFile(SmartCardFile file) throws PinEntryCancelledException, PinBlockedException, POReIDException, PinTimeoutException {
        return readFile(file, null);
    }

    protected final byte[] readFile(SmartCardFile file, byte[] pinCode) throws PinEntryCancelledException, PinBlockedException, POReIDException, PinTimeoutException {
        String idFileid;
        byte[] contents = null;
        int lenght_sel;
        int offset, lenght;
        boolean readComplete = false;
        boolean recover = true;

        loadData();
        idFileid = this.fileCache.getSCFileCacheFileName(file);
        try {
            if (file.isCacheable() && this.fileCache.isCached(idFileid)) {
                if (file.isUpdateable()) {
                    selectFile(file.getFileId());
                    try {
                        contents = this.fileCache.readNCheckCacheFile(file, readBinary(file.getDiffOffset(), file.getDiffLenght()));
                    } catch (SecurityStatusNotSatisfiedException ignore) {
                    }
                } else {
                    contents = this.fileCache.readCachedFile(idFileid);
                }

                if (null != contents) {
                    return contents;
                }
            }
                        
            do {                
                try {                    
                    lenght_sel = selectFile(file.getFileId());
                    if (-1 != file.getOffset()) {
                        if (-1 != file.getLenght()) {
                            lenght = file.getLenght();
                        } else {
                            lenght = (lenght_sel != NO_FCI ? lenght_sel - file.getOffset() : lenght_sel);
                        }
                        offset = file.getOffset();
                    } else {
                        offset = 0;
                        lenght = lenght_sel;
                    }
                    
                    contents = readBinary(offset, lenght);
                    readComplete = true;
                } catch (POReIDException ex){
                    if (recover) {
                        reconnect(ex.getCause());
                        recover = false;                        
                    } else {
                        throw ex;
                    }                    
                } catch (SecurityStatusNotSatisfiedException ex) {
                    if (null != file.getPin()) {
                        verifyPin(file.getPin(), pinCode);
                    } else {
                        throw new POReIDException(ex);
                    }
                }
            } while (!readComplete);

            if (file.isCacheable()) {
                this.fileCache.writeCacheFile(idFileid, contents);
            }
        } catch (IOException ex) {
            throw new POReIDException(ex.getMessage(), ex);
        }

        return contents;
    }
    
    
    private void reconnect(Throwable throwable) throws POReIDException {                
        
        if (throwable.getCause() != null) {
            int error = PCSCErrorValues.getPcscExceptionValue(throwable.getCause().getMessage());

            try {
                switch (error) {
                    case PCSCErrorValues.SCARD_W_RESET_CARD:
                    case PCSCErrorValues.SCARD_E_COMM_DATA_LOST:
                    case PCSCErrorValues.SCARD_E_NOT_TRANSACTED:
                    case PCSCErrorValues.ERROR_IO_DEVICE: // duvido que resolva... mas vamos lá
                        card.reconnect(PCSCDefines.SCARD_SHARE_SHARED, PCSCDefines.SCARD_RESET_CARD);
                        selectAID(this.aid, false);
                        break;
                    default:
                        throw new POReIDException("Não foi possível recuperar (D) error= {" + PCSCErrorValues.toErrorString(error) + "} ex.error= {" + throwable.getCause().getMessage() + "}", throwable);
                }
            } catch (CardException ex) {
                throw new POReIDException("Não foi possível recuperar", ex);
            }
        }
    }

    
    private void selectAID(String aid, boolean recover) throws POReIDException{
        try {
            if (channel.transmit(new CommandAPDU(Util.hexToBytes(aid)), true, true).getSW() != 0x9000) {
                throw new POReIDException("AID " + aid + " não foi selecionado");
            }
        } catch (CardException ex) {
            if (recover) {
                reconnect(ex);
            } else {
                throw new POReIDException("(no recover) AID " + aid + " não foi selecionado",ex);
            }
        }
    }
    
    private void LoadPanAndCache() throws POReIDException, IOException, SecurityStatusNotSatisfiedException {
        boolean recover = true;
        boolean opComplete = false;

        do {
            try {
                this.cardPan = Util.extractFromASN1(readBinary(0, selectFile(files.EF_5032.getFileId())), 7, 8);
                selectFile(files.SOD.getFileId());
                this.fileCache = new SmartCardFileCache(this.cardPan, cacheStatus, readBinary(files.SOD.getDiffOffset(), files.SOD.getDiffLenght()));                
                opComplete = true;
            } catch (POReIDException ex) {
                if (recover) {
                    reconnect(ex);
                    recover = false;
                } else {
                    throw ex;
                }
            }
        } while (!opComplete);
    }
    
    private void loadData() throws POReIDException {                
        try {
            beginExclusive();
            if (null == this.cardPan) {
                selectAID(this.aid, true);
                LoadPanAndCache();
            }
        } catch (IOException | CardException ex) {
            throw new POReIDException(ex.getMessage(), ex);
        } catch (SecurityStatusNotSatisfiedException ignore) {
        }
    }

    
    @Override
    public final X509Certificate getCertificate(SmartCardFile file) throws CertificateNotFound {
        try {
            if (null == this.certificateFactory){
                this.certificateFactory = CertificateFactory.getInstance("X.509");
            }
            return (X509Certificate) this.certificateFactory.generateCertificate(new ByteArrayInputStream(readFile(file)));
        } catch (PinTimeoutException | CertificateException | PinEntryCancelledException | PinBlockedException | POReIDException ex){
            throw new CertificateNotFound("Certificado não encontrado",ex);
        }
    }
    
    
    @Override
    public final X509Certificate getAuthenticationCertificate() throws CertificateNotFound {
        return this.getCertificate(files.AuthenticationCertificate);
    }

    
    @Override
    public final X509Certificate getQualifiedSignatureCertificate() throws CertificateNotFound {
        return this.getCertificate(files.QualifiedSignatureCertificate);
    }

    
    
    @Override
    public final List<X509Certificate> getQualifiedSignatureCertificateChain() throws CertificateChainNotFound{
        try {
            List<X509Certificate> l;
            KeyStore ks = KeyStore.getInstance("JKS");
            try (InputStream input = POReIDCard.class.getResourceAsStream(poreidKeystore)){
                ks.load(input, null);
            }
            l = (List<X509Certificate>) Util.getCertificateChain(getCertificate(files.QualifiedSignatureSubCACertificate), ks);
            l.add(0,getQualifiedSignatureCertificate());
            return l;  
        } catch (CertificateNotFound | KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException ex) {
            throw new CertificateChainNotFound("Não foi possivel obter cadeia de certificados",ex);
        }
    }

    
    @Override
    public final List<X509Certificate> getAuthenticationCertificateChain() throws CertificateChainNotFound {
        try {
            List<X509Certificate> l;
            KeyStore ks = KeyStore.getInstance("JKS");
            try (InputStream input = POReIDCard.class.getResourceAsStream(poreidKeystore)){
                ks.load(input, null);
            }            
            l = (List<X509Certificate>) Util.getCertificateChain(getCertificate(files.AuthenticationSubCACertificate), ks);
            l.add(0,getAuthenticationCertificate());
            return l;
        } catch (CertificateNotFound | KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException ex) {
            throw new CertificateChainNotFound("Não foi possivel obter cadeia de certificados",ex);
        }
    }
    
    
    @Override
     public final List<X509Certificate> getCertificateChain(SmartCardFile file) throws CertificateChainNotFound {
         throw new RuntimeException("Não implementado");
     }

    
    @Override
    public final byte[] getIcon(){
        try (InputStream input = POReIDCard.class.getResourceAsStream(escudoPortugues)) {
            return Util.toByteArray(input);
        } catch (IOException ex) {
            return new byte[0];
        }
    }
    
    
    @Override
    public PinStatus getPinStatus(Pin pin) throws POReIDException{
        PinStatus pinStatus;
        int triesLeft;
        
        try {
            goToPinKeyPath(pin);
			
            ResponseAPDU responseApdu = channel.transmit(new CommandAPDU(0x00, 0x20, 0x00, pin.getReference()),true, true);
            switch (responseApdu.getSW()) {
                case 0x6404: // Firewalled pinpad
                case 0x6982: // Security condition not satisfied
                case 0x6985: // Conditions of use not satisfied
                case 0x6a81: // Function not supported                
                case 0x6d00: // Instruction code not supported or invalid
                    pinStatus = new PinStatus(false); 
                    break;                
                case 0x9000:
                    pinStatus = new PinStatus(false);           
                    break;
                default:
                    if ((responseApdu.getSW() & (int) 0xfffffff0) == 0x63C0) {
                        triesLeft = responseApdu.getSW2() & 0x0f;
                        pinStatus = new PinStatus(triesLeft);
                    } else {
                        pinStatus = new PinStatus(false); // não é necessário bloquear a execução só porque surgiu um valor não esperado                    
                    }
            }   
        } catch (CardException ex) {
            throw new POReIDException(ex);
        }
        return pinStatus;
    }
    
    
    @Override
    public void ModifyPin(Pin pin) throws PinBlockedException, PinEntryCancelledException, POReIDException {
        ResponseAPDU responseApdu;
        
        checkPinTries(pin, -1);
        
        try {  
            responseApdu = resolveReaderPinpadSupportModifyPin(pin);
            if (0x9000 != responseApdu.getSW()) {
                throw new POReIDException("Não foi possível modificar o" + pin.getLabel() + ". Código de estado: " + Integer.toHexString(responseApdu.getSW()));
            }
        } catch (CardException ex) {
            throw new POReIDException("Não foi possivel modificar o "+pin.getLabel(), ex);
        } catch (PinTimeoutException ignore) { } 
    }
    
    
    private ResponseAPDU resolveReaderPinpadSupportModifyPin(Pin pin) throws PinBlockedException, PinEntryCancelledException, PinTimeoutException, CardException, POReIDException {
        ResponseAPDU responseApdu;

        if (!getCardSpecificReferences().isEMVCAPPin(pin)) {
            if (terminalFeatures.isModifyPinThroughPinpadAvailable()) {
                if (terminalFeatures.isModifyPinThroughPinpadSupported(this.getClass().getName())) {
                    responseApdu = modifyPinWithPinPad(pin);
                } else {
                    if (terminalFeatures.canBypassPinpad()) { //TODO: verificar se um keep alive aqui ajuda no caso de windows 8 
                        DialogController.getInstance(bundle.getString("incompatible.title"), MessageFormat.format(bundle.getString("incompatible.message.modify.ok"), pin.getLabel()), locale, false).displayDialog();
                        responseApdu = modifyPinWithoutPinPad(pin);
                    } else {
                        DialogController.getInstance(bundle.getString("incompatible.title"), MessageFormat.format(bundle.getString("incompatible.message.modify.error"), pin.getLabel()), locale, true).displayDialog();
                        throw new POReIDException("O leitor de cartões não permite a realização da operação de modificação do " + pin.getLabel());
                    }
                }
            } else {
                responseApdu = modifyPinWithoutPinPad(pin);
            }
        } else {
            if (terminalFeatures.canBypassPinpad()) {
                DialogController.getInstance(bundle.getString("emvpin.dialog.title"), MessageFormat.format(bundle.getString("emvpin.dialog.message.ok"), pin.getLabel()), locale, false).displayDialog();
                responseApdu = modifyPinWithoutPinPad(pin);
            } else {
                DialogController.getInstance(bundle.getString("emvpin.dialog.title"), MessageFormat.format(bundle.getString("emvpin.dialog.message.error"), pin.getLabel()), locale, true).displayDialog();
                throw new POReIDException("O leitor de cartões não permite a realização da operação de modificação do " + pin.getLabel());
            }
        }

        return responseApdu;
    }
    
    
    private ResponseAPDU modifyPinWithPinPad(Pin pin) throws POReIDException, PinTimeoutException, PinEntryCancelledException, PinBlockedException {
        ResponseAPDU responseApdu;
        UsePinPadDialogController modifyDialogCtl = null;
        byte[] verifyApdu = null;
        boolean pinOk = false;
        int pinTriesLeft;

        try {
            do {
                modifyDialogCtl = UsePinPadDialogController.getInstance(PinOperation.MODIFICACAO, pin, locale);
                modifyDialogCtl.displayVerifyPinPinPadDialog();

                if (verifyToModify()) {
                    verifyApdu = getVerifyPinAPDU(pin);
                }

                responseApdu = new ResponseAPDU(terminalFeatures.transmitModifyPinDirect(CCConfig.TIMEOUT, (byte) pin.getMinLength(), (byte) pin.getMaxLength(), verifyApdu, getModifyPinAPDU(pin)));
                
                switch (responseApdu.getSW()) {
                    case 0x9000:
                        pinOk = true;
                        break;
                    case 0x6401:
                        modifyDialogCtl.disposeVerifyPinPinPadDialog();
                        throw new PinEntryCancelledException("Introdução do " + pin.getLabel() + " cancelada pelo utilizador.");
                    case 0x6983:    
                    case 0x6402:
                    case 0x6984:
                        modifyDialogCtl.disposeVerifyPinPinPadDialog();
                        BlockedPinDialogController.getInstance(pin.getLabel(), locale).displayBlockedPinDialog(csr.getStartTime());
                        throw new PinBlockedException("O " + pin.getLabel() + "está bloqueado.");
                    default:
                        modifyDialogCtl.disposeVerifyPinPinPadDialog();
                        if ((responseApdu.getSW() & (int) 0xfffffff0) == 0x63C0) {
                            pinTriesLeft = responseApdu.getSW2() & 0x0f;
                        } else {
                            throw new POReIDException("Código de estado não esperado: " + Integer.toHexString(responseApdu.getSW()));
                        }   
                        if (WrongPinDialogController.getInstance(pin.getLabel(), pinTriesLeft, locale).displayWrongPinDialog(csr.getStartTime())) {
                            throw new PinEntryCancelledException("Introdução do " + pin.getLabel() + " cancelada pelo utilizador.");
                        }
                }
            } while (true != pinOk);

        } finally {
            if (null != modifyDialogCtl){
                modifyDialogCtl.disposeVerifyPinPinPadDialog();
            }
        }

        return responseApdu;
    }
    
    
    private ResponseAPDU modifyPinWithoutPinPad(Pin pin) throws PinBlockedException, PinEntryCancelledException, PinTimeoutException, CardException, POReIDException {
        CommandAPDU cApdu;
        ResponseAPDU response;
        boolean pinOk;
        ByteBuffer pins[];
        OTP otp=null;

        do {        
            pins = ModifyPinDialogController.getInstance(pin.getLabel(), pin.getMinLength(), pin.getMaxLength(), locale).modifyPin();
            pinOk = internalVerifyPin(pin, pins[0].array());
        } while (true != pinOk);

        if (getCardSpecificReferences().isEMVCAPPin(pin)) {          
            try {
                otpPinChanging = true;
                otp = new OTP(this, pin, pins);
                otp.doOTPPinModify();
            } finally {
                otpPinChanging = false;
            }
        }
        
        cApdu = new CommandAPDU(getNFillModifyPinAPDU(pin, new byte[][]{pins[0].array(), pins[1].array()}));
        Arrays.fill(pins[0].array(), pin.getPadChar());
        Arrays.fill(pins[1].array(), pin.getPadChar()); 
        response = channel.transmit(cApdu, true, true);
        
        if (null != otp && 0x9000 == response.getSW()) {
            otp.finish();
        }
        
        return response;
    }
    
    
    protected final boolean isOTPPinChanging(){
        return otpPinChanging;
    }
    
    
    /*private boolean isOSWindows8Plus() {
        String osName = System.getProperty("os.name");
        return (osName.contains("Windows 8") || osName.contains("Windows 10"));
    }*/
     
     
    private byte[] getVerifyPinAPDU(Pin pin) {
        byte pad = pin.getPadChar();
        return new byte[]{0x00, 0x20, 0x00, (byte) pin.getReference(), 0x08, pad, pad, pad, pad, pad, pad, pad, pad};
    }
    
    
    protected void beginExclusive() throws CardException{
        if (!locked){
            locked = true;
            card.beginExclusive();            
        }
    }
    
    
    protected boolean endExclusive() throws CardException{
        boolean unlock = true;
        if (locked){
            locked = false;            
            card.endExclusive();
        } else {
            unlock = false;
        }
        
        return unlock;
    }
    
    @Override
    public boolean isPOReIDSmartcardPresent() throws POReIDException{
        try {
            return csr.getTerminal().isCardPresent();
        } catch (CardException ex) {
            throw new POReIDException("Ocorreu um erro durante a verificação do leitor no cartão", ex);
        }
    }
    
    
    @Override
    public void close() throws POReIDException{
        if (fileCache != null) {
            fileCache.enforceCacheThreshold();
        }
        try {
            endExclusive();
            this.card.disconnect(SCARD_RESET_CARD);
        } catch (CardException ex) {
            throw new POReIDException("Ocorreu um erro durante a terminação da ligação com cartão", ex);
        }
    }
}
