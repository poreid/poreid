/*
 * The MIT License
 *
 * Copyright 2014 Rui Martinho (rmartinho@gmail.com), António Braz (antoniocbraz@gmail.com)
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
package org.poreid;

import org.poreid.pinpad.ReaderWithPinPad;
import org.poreid.pinpad.ReaderWithPinPadData;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import org.poreid.config.POReIDConfig;

/**
 *
 * @author POReID
 */
public final class TerminalFeatures {
    private final Card card;
    private final String className;
    private final String readerName;
    private final byte FEATURE_VERIFY_PIN_DIRECT = 0x06;
    private final byte FEATURE_MODIFY_PIN_DIRECT = 0x07;              
    private final byte MICROSOFT_DEVICE_TYPE__SMARTCARD = 0x31;  // http://research.microsoft.com/en-us/um/redmond/projects/invisible/src/drivers/net/simnic/devioctl.h.htm
    private final int SCARD_CTL_BASE_CODE = 0x42000000;          // reader.h pcsc - *nix code 
    private final int GET_FEATURE_REQUEST = 3400;                // http://www.pcscworkgroup.com/specifications/files/pcsc10_v2.02.09.pdf
    private final int CM_IOCTL_GET_FEATURE_REQUEST = SCARD_CTL_CODE(GET_FEATURE_REQUEST);
    
    
    private TerminalFeatures(Card card, String readerName) {
        this.card = card;
        Matcher matcher = Pattern.compile("^(.*) \\d+ \\d+$|^(.*) \\d+$").matcher(readerName);
        matcher.matches();
        this.readerName = null != matcher.group(1) ? matcher.group(1): matcher.group(2);   
        this.className = POReIDConfig.getSmartCardReaderImplementingClassName(this.readerName);
    }
    
    
    /**
     * Devolve uma instância da classe
     * @param card
     * @param readerName
     * @return Instância da classe TerminalFeatures
     */
    public static TerminalFeatures getInstance(Card card, String readerName) {   
        return new TerminalFeatures(card, readerName);
    }
    
    
    /**
     * Indica se o leitor disponibiliza a funcionalidade de verificação do pin através de pinpad (caso exista)
     * @return true se disponibiliza, false se não
     */
    public boolean isVerifyPinThroughPinpadAvailable() {
        return null != getFeature(FEATURE_VERIFY_PIN_DIRECT);    
    }
    
    
    /**
     * Indica se o leitor apesar de dispor de pinpad a funcionalidade de verificação do pin através do pinpad é suportada
     * @param scClass
     * @return true se suporta, false se não.
     */
    public boolean isVerifyPinThroughPinpadSupported(String scClass) {
        return POReIDConfig.getVerifyPinSupport(readerName, scClass);
    }
    
    
    /**
     * Indica se o leitor disponibiliza a funcionalidade de modificação do pin através de pinpad (caso exista)
     * @return true se disponibiliza, false se não
     */
    public boolean isModifyPinThroughPinpadAvailable() {   
        return null != getFeature(FEATURE_MODIFY_PIN_DIRECT);
    }
    
    
    /**
     * Indica se o leitor apesar de dispor de pinpad a funcionalidade de modificação do pin através do pinpad é suportada
     * @param scClass
     * @return true se suporta, false se não.
     */
    public boolean isModifyPinThroughPinpadSupported(String scClass) {
        return POReIDConfig.getModifyPinSupport(readerName, scClass);
    }
    
    
    /**
     * Indica se é possivel utilizar um leitor com pinpad como se fosse um leitor sem pinpad
     * @return true se for possivel, false se não for.
     */
    public boolean canBypassPinpad(){
        return POReIDConfig.getOSInjectPinSupport(readerName);
    }
    
    
    private Integer getFeature(byte featureTag) {
        byte[] features;

        try {
            features = card.transmitControlCommand(CM_IOCTL_GET_FEATURE_REQUEST, new byte[0]);
            if (0 == features.length) {
                return null;
            }
        } catch (CardException e) {
            return null;
        }

        return findFeature(featureTag, features);
    }

    
    private Integer findFeature(byte featureTag, byte[] features) {
        int idx = 0;
        while (idx < features.length) {
            if (featureTag == features[idx]) { //http://www.pcscworkgroup.com/specifications/files/pcsc10_v2.02.09.pdf -- 2.6.14
                return java.nio.ByteBuffer.wrap(features, idx+2, 4).order(java.nio.ByteOrder.BIG_ENDIAN).getInt();
            }
            idx += 6;
        }
        return null;
    }
    
    
    private int SCARD_CTL_CODE(int code) {
        int ioctl;

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            ioctl = MICROSOFT_DEVICE_TYPE__SMARTCARD << 16 | (code) << 2;
        } else {
            ioctl = SCARD_CTL_BASE_CODE + code;
        }
        return ioctl;
    }
    
    
    /**
     * Transmite as instruções necessárias à verificação de PIN
     * @param timeOut
     * @param minPinSize
     * @param maxPinSize
     * @param apdu
     * @return status word
     * @throws POReIDException
     */
    public byte[] transmitVerifyPinDirect(byte timeOut, byte minPinSize, byte maxPinSize, byte[] apdu) throws POReIDException {        
        try {
            Constructor<? extends ReaderWithPinPad> ctor = Class.forName(className).asSubclass(ReaderWithPinPad.class).getConstructor();
            ReaderWithPinPadData pData = ctor.newInstance().getVerifyPinDirect(timeOut, minPinSize, maxPinSize, apdu);

            try {
                if (pData.getIoCtlSmartcardLcdMessages() != 0) {
                    card.transmitControlCommand(pData.getIoCtlSmartcardLcdMessages(), pData.getPinPadString());
                }
            } catch (CardException ignore) { }
                    
            return card.transmitControlCommand(getFeature(FEATURE_VERIFY_PIN_DIRECT), pData.getFeaturePinDirect());
        } catch (CardException | InvocationTargetException | IllegalArgumentException | SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException | IOException ex) {
            throw new POReIDException("Não foi possivel efetuar a verificação do PIN através do leitor de cartões", ex);
        }
    }
    
    
    /**
     * Transmite as instruções necessárias à modificação de PIN
     * @param timeOut
     * @param minPinSize
     * @param maxPinSize
     * @param modifyApdu
     * @return status word
     * @throws POReIDException
     */
    public byte[] transmitModifyPinDirect(byte timeOut, byte minPinSize, byte maxPinSize, byte[] modifyApdu) throws POReIDException {        
        return transmitModifyPinDirect(timeOut, minPinSize, maxPinSize, null, modifyApdu);
    }
    
    
    /**
     * Transmite as instruções necessárias à modificação de PIN
     * @param timeOut
     * @param minPinSize
     * @param maxPinSize
     * @param verifyApdu
     * @param modifyApdu
     * @return status word
     * @throws POReIDException
     */
    public byte[] transmitModifyPinDirect(byte timeOut, byte minPinSize, byte maxPinSize, byte[] verifyApdu, byte[] modifyApdu) throws POReIDException {
        ResponseAPDU responseApdu;

        try {
            if (null != verifyApdu) {
                responseApdu = new ResponseAPDU(transmitVerifyPinDirect(timeOut, minPinSize, maxPinSize, verifyApdu));
                if (0x9000 != responseApdu.getSW()){
                    return responseApdu.getBytes(); // trata do erro mais acima, poderemos ter de voltar novamente.
                }   
            }

            Constructor<? extends ReaderWithPinPad> ctor = Class.forName(className).asSubclass(ReaderWithPinPad.class).getConstructor();
            ReaderWithPinPadData pData = ctor.newInstance().getModifyPinDirect((verifyApdu!=null), timeOut, minPinSize, maxPinSize, modifyApdu);
            
            try {
                if (pData.getIoCtlSmartcardLcdMessages() != 0) {
                    card.transmitControlCommand(pData.getIoCtlSmartcardLcdMessages(), pData.getPinPadString());
                }
            } catch (CardException ignore) { }
            
            return card.transmitControlCommand(getFeature(FEATURE_MODIFY_PIN_DIRECT), pData.getFeaturePinDirect());

        } catch (CardException | InvocationTargetException | IllegalArgumentException | SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException | IOException ex) {
            throw new POReIDException("Não foi possivel efetuar a alteração do PIN através do leitor de cartões", ex);
        }
    }
}
