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
package org.poreid;

import org.poreid.pcscforjava.Card;
import org.poreid.pcscforjava.CardException;
import org.poreid.pcscforjava.ResponseAPDU;
import org.poreid.pinpad.ReaderWithPinPad;
import org.poreid.pinpad.ReaderWithPinPadData;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.poreid.config.POReIDConfig;

/**
 *
 * @author POReID
 */
public final class TerminalFeatures {
    private final Card card;
    private final String className;
    private final String readerName;
    private static final byte FEATURE_VERIFY_PIN_DIRECT = 0x06;
    private static final byte FEATURE_MODIFY_PIN_DIRECT = 0x07;              
    private static final byte MICROSOFT_DEVICE_TYPE__SMARTCARD = 0x31;  // http://research.microsoft.com/en-us/um/redmond/projects/invisible/src/drivers/net/simnic/devioctl.h.htm
    private static final int SCARD_CTL_BASE_CODE = 0x42000000;          // reader.h pcsc - *nix code 
    private static final int GET_FEATURE_REQUEST = 3400;                // http://www.pcscworkgroup.com/specifications/files/pcsc10_v2.02.09.pdf
    private final int CM_IOCTL_GET_FEATURE_REQUEST = SCARD_CTL_CODE(GET_FEATURE_REQUEST);
    private Integer cachedFeatureVerify;
    private Integer cachedFeatureModify;
    
    
    private TerminalFeatures(Card card, String readerName) {
        this.card = card;        
        this.readerName = readerName.replaceAll("( \\d+)*$", "");
        this.className = POReIDConfig.getSmartCardReaderImplementingClassName(this.readerName);
    }
    
    
    /**
     * Devolve uma instância da classe
     * @param card Instância de um cartão (pertence ao smartcardio)
     * @param readerName Nome do leitor de cartões
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
        return null != (cachedFeatureVerify = getFeature(FEATURE_VERIFY_PIN_DIRECT));    
    }
    
    
    /**
     * Indica se o leitor apesar de dispor de pinpad a funcionalidade de verificação do pin através do pinpad é suportada
     * @param scClass Nome da classe que implementa o suporte ao cartão
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
        return null != (cachedFeatureModify = getFeature(FEATURE_MODIFY_PIN_DIRECT));
    }
    
    
    /**
     * Indica se o leitor apesar de dispor de pinpad a funcionalidade de modificação do pin através do pinpad é suportada
     * @param scClass Nome da classe que implementa o suporte ao cartão
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
     * @param timeOut Tempo limite
     * @param minPinSize Tamanho minimo do pin
     * @param maxPinSize Tamanho máximo do pin
     * @param apdu Instrução de verificação do pin a enviar para o cartão
     * @return status word
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
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
                    
            return card.transmitControlCommand(cachedFeatureVerify, pData.getFeaturePinDirect());
        } catch (CardException | InvocationTargetException | IllegalArgumentException | SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException | IOException ex) {
            throw new POReIDException("Não foi possivel efetuar a verificação do PIN através do leitor de cartões", ex);
        }
    }
    
    
    /**
     * Transmite as instruções necessárias à modificação de PIN
     * @param timeOut Tempo limite
     * @param minPinSize Tamanho minimo do pin
     * @param maxPinSize Tamanho máximo do pin
     * @param modifyApdu Instrução de modificação do pin a enviar para o cartão
     * @return status word
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    public byte[] transmitModifyPinDirect(byte timeOut, byte minPinSize, byte maxPinSize, byte[] modifyApdu) throws POReIDException {        
        return transmitModifyPinDirect(timeOut, minPinSize, maxPinSize, null, modifyApdu);
    }
    
    
    /**
     * Transmite as instruções necessárias à modificação de PIN
     * @param timeOut tempo de expiração
      * @param minPinSize Tamanho minimo do pin
     * @param maxPinSize Tamanho máximo do pin
     * @param verifyApdu Instrução de verificação do pin a enviar para o cartão
     * @param modifyApdu Instrução de modificação do pin a enviar para o cartão
     * @return status word
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
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
            
            return card.transmitControlCommand(cachedFeatureModify, pData.getFeaturePinDirect());

        } catch (CardException | InvocationTargetException | IllegalArgumentException | SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException | IOException ex) {
            throw new POReIDException("Não foi possivel efetuar a alteração do PIN através do leitor de cartões", ex);
        }
    }
}
