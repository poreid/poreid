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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import org.poreid.CacheStatus;
import org.poreid.POReIDException;
import org.poreid.SmartCardFileException;
import org.poreid.dialogs.pindialogs.PinBlockedException;
import org.poreid.dialogs.pindialogs.PinEntryCancelledException;
import org.poreid.dialogs.pindialogs.PinTimeoutException;

/**
 * Fornece a implementação das funcionalidades definidas na interface CitizenData
 * @author POReID
 */
public abstract class CitizenCard extends POReIDCard implements CitizenData{
    private String visibleInfo;
    private String tooltip;
    private String description;
    private final ResourceBundle bundle;
    private CitizenCardIdAttributes ccia = null;
    private CitizenCardPhotoAttributes ccpa = null;
    private CitizenCardAddressAttributes ccaa =  null;
    private byte[] notes = null;
    private byte[] sod = null;
      
    
    protected CitizenCard(CardSpecificReferences csr, CacheStatus cacheStatus) {
        super(csr, cacheStatus);
        bundle = CCConfig.getBundle(CitizenCard.class.getSimpleName(),csr.getLocale());
    }
   
    
    @Override
    public final CitizenCardPhotoAttributes getPhotoData() throws SmartCardFileException {
        try {
            if (null != ccpa){
                return ccpa;
            }
            return ccpa = new CitizenCardPhotoAttributes(readFile(getFileDescription().PHOTO));
        } catch (PinTimeoutException | PinEntryCancelledException | PinBlockedException | POReIDException ex) {
            throw new SmartCardFileException("Erro durante a leitura da fotografia. Não foi possivel ler os dados.", ex);
        }
    }

    
    @Override
    public final CitizenCardIdAttributes getID() throws SmartCardFileException {
        try {
            if (null != ccia){
                return ccia;
            }
            return ccia = new CitizenCardIdAttributes(readFile(getFileDescription().ID));
        } catch (PinTimeoutException | PinEntryCancelledException | PinBlockedException | POReIDException ex) {
            throw new SmartCardFileException("Erro durante a leitura da identificação. Não foi possivel ler os dados.", ex);
        }
    }

    
    @Override
    public final CitizenCardAddressAttributes getAddress() throws PinTimeoutException, PinEntryCancelledException, PinBlockedException, POReIDException {
        if (null != ccaa) {
            return ccaa;
        }
        
        return ccaa = new CitizenCardAddressAttributes(readFile(getFileDescription().ADDRESS));
    }

    
    @Override
    public final byte[] readPersonalNotes() throws SmartCardFileException {
        try {
            if (null != notes){
                return notes;
            }
            return notes = readFile(getFileDescription().NOTES);
        } catch (PinTimeoutException | PinEntryCancelledException | PinBlockedException | POReIDException ex) {
            throw new SmartCardFileException("Erro durante a leitura das notas pessoais. Não foi possivel ler os dados.", ex);
        }
    }
    
    
    @Override
    public final byte[] getSOD() throws SmartCardFileException {
        try {
            if (null != sod){
                return sod;
            }
            return sod = readFile(getFileDescription().SOD);
        } catch (PinTimeoutException | PinEntryCancelledException | PinBlockedException | POReIDException ex) {
            throw new SmartCardFileException("Erro durante a leitura do SOD. Não foi possivel ler os dados.", ex);
        }
    }
    
    
    @Override
    public final String getUIVisibleInfo(){
        if (visibleInfo == null){
            try {
                getID();
            } catch (SmartCardFileException ex) {
                throw new RuntimeException("Erro não é possivel exibir os dados do cartão.", ex);
            }
            visibleInfo = ccia.getCitizenFullName();
        }
        
        return visibleInfo;
    }
    
    
    @Override 
    public final String getTooltip(){
        if (tooltip == null){
            try {
                getID();
            } catch (SmartCardFileException ex) {
                return bundle.getString("tooltip.error");
            }
            tooltip = bundle.getString("name")+": " + ccia.getCitizenFullName()+" * "+bundle.getString("nic")+": "+ccia.getDocumentNumber();
        }
        
        return tooltip;
    }
    
    
    @Override
    public final String getDescription() {
        if (null == description) {
            try {
                getID();
            } catch (SmartCardFileException ex) {
                return bundle.getString("description.error");
            }
            description = MessageFormat.format(bundle.getString("description"), ccia.getCitizenFullName(), ccia.getDocumentNumber());
        }
        
        return description;
    }

    
    @Override
    public final void savePersonalNotes(String notes) throws SmartCardFileException, PinTimeoutException, POReIDException, PinEntryCancelledException, PinBlockedException {
        byte[] tmp = notes.getBytes(StandardCharsets.UTF_8);
        byte[] notesBytes;
        
        if (tmp.length+1 > getFileDescription().NOTES.getMaximumSize()){
            throw new SmartCardFileException("O tamanho dos dados excede a capacidade das notas pessoais. Tamanho máximo = "+getFileDescription().NOTES.getMaximumSize()+" bytes");
        }
        
        notesBytes = new byte[tmp.length+1];
        System.arraycopy(tmp, 0, notesBytes, 0, tmp.length);
        notesBytes[tmp.length] = '\0';
        
        writeFile(getFileDescription().NOTES, notesBytes);        
    }
    
    
    @Override
    public PublicKey getPublicKey() throws SmartCardFileException, InvalidKeySpecException, NoSuchAlgorithmException {
        try {
            PublicKey pubKey;
            byte[] pk;

            pk = readFile(getFileDescription().PUBLIC_KEY);

            BigInteger modulus = new BigInteger(Arrays.copyOf(pk, 128));
            BigInteger exponent = new BigInteger(Arrays.copyOfRange(pk, 128, pk.length));
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance(CCConfig.RSA);
            pubKey = factory.generatePublic(spec);
            return pubKey;
        } catch (PinEntryCancelledException | PinBlockedException | POReIDException | PinTimeoutException ex) { 
            throw new SmartCardFileException("Erro durante a leitura da chave pública. Não foi possivel ler os dados.", ex);
        }
    }
}
