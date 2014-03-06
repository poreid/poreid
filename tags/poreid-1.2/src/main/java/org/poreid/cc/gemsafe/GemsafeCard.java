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

package org.poreid.cc.gemsafe;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import org.poreid.cc.CitizenCard;
import org.poreid.DigestPrefixes;
import org.poreid.Pin;
import org.poreid.PkAlias;
import org.poreid.RSAPaddingSchemes;
import org.poreid.cc.CardSpecificReferences;
import org.poreid.POReIDException;
import org.poreid.common.Util;
import org.poreid.config.POReIDConfig;
import org.poreid.dialogs.pindialogs.PinBlockedException;
import org.poreid.dialogs.pindialogs.PinEntryCancelledException;
import org.poreid.dialogs.pindialogs.PinTimeoutException;

/**
 *
 * @author POReID
 */
public final class GemsafeCard extends CitizenCard {
    private final Card card;
    private final CardChannel channel;
    
    
    public GemsafeCard(Card card, String readerName, Locale locale, boolean cachePreferences) {
        super(new GemsafeSpecificReferences(card, readerName, locale, cachePreferences));
        this.card = card;
        this.channel = card.getBasicChannel();
    }

    
    @Override
    protected int selectFile(String fileId) throws POReIDException{
        try {
            ResponseAPDU responseApdu;

            responseApdu = channel.transmit(new CommandAPDU(0x00, 0xA4, 0x00, 0x00, Util.hexToBytes(fileId.substring(8))));
            if (0x9000 != responseApdu.getSW()) {
                throw new POReIDException("Código de estado não esperado: " + Integer.toHexString(responseApdu.getSW()));
            }

            return parseFCI(responseApdu.getData());
        } catch (CardException ex) {
            throw new POReIDException(ex);
        }
    }

    
    private int parseFCI(byte[] fci) throws POReIDException {
        int size = 0;
        int index = 0;
        if (fci != null && fci[index] == 0x6F && fci.length == fci[++index] + 2) {
    search:
            while (++index < fci.length) {
                switch (fci[index]) {
                    case (byte) 0x81:
                        if (fci[++index]==2){
                            size = ((fci[++index] & 0xFF) << 8) | (fci[++index] & 0xFF);     
                        } else {
                            throw new POReIDException("Formato do FCI (file control information) não esperado");
                        }
                    break search;
                    default:
                        index = fci[++index] + index;
                    break;
                }
            }
        }
        
        return size;
    }
    
    
    @Override
    public byte[] sign(byte hash[],  byte[] pinCode, String digestAlgo, PkAlias pkAlias, RSAPaddingSchemes... sch) throws PinTimeoutException, PinEntryCancelledException, PinBlockedException, POReIDException {
        ResponseAPDU responseApdu = null;
        CommandAPDU cmd;
        
        try {
            RSAPaddingSchemes scheme = sch.length > 0 && null != sch[0] ? sch[0] : RSAPaddingSchemes.PKCS1;
            CardSpecificReferences csr = getCardSpecificReferences();
            Pin gemPin = csr.getCryptoReferences(pkAlias);

            DigestPrefixes digestPrefixes = csr.getDigestPrefix(digestAlgo);
            if (null == digestPrefixes) {
                throw new POReIDException("Algoritmo de resumo desconhecido - " + digestAlgo);
            }

            try {
                card.beginExclusive();
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(0x90);
                if (0 == digestPrefixes.compareTo(DigestPrefixes.SHA_1)) {
                    baos.write(digestPrefixes.getPrefix().length + hash.length);
                    baos.write(digestPrefixes.getPrefix(), 0, digestPrefixes.getPrefix().length);
                    baos.write(hash, 0, hash.length);
                } else {
                    baos.write(hash.length);
                    baos.write(hash, 0, hash.length);
                }
                  
                if (!POReIDConfig.isExternalPinCachePermitted() && !isOTPPinChanging()) {
                    pinCode = null;
                }
                  
                verifyPin(gemPin, pinCode); // pin introduzido através do dialogo.
                
                setSecurityEnvironment(csr.getAlgorithmID(digestAlgo, scheme), gemPin.getKeyReference());

                cmd = new CommandAPDU(0x00, 0x2A, 0x90, 0xA0, baos.toByteArray());
                responseApdu = channel.transmit(cmd);
                if (0x9000 != responseApdu.getSW()) {
                    throw new POReIDException("Código de estado não esperado: " + Integer.toHexString(responseApdu.getSW()));
                }

                cmd = new CommandAPDU(0x00, 0x2A, 0x9E, 0x9A, 0x80);
                responseApdu = channel.transmit(cmd);
                if (0x9000 != responseApdu.getSW()) {
                    throw new POReIDException("Código de estado não esperado: " + Integer.toHexString(responseApdu.getSW()));
                }

            } finally {
                card.endExclusive();
            }

            return responseApdu != null ? responseApdu.getData() : new byte[0];
        } catch (CardException ex) {
            throw new POReIDException(ex);
        }
    }

    private void setSecurityEnvironment(Byte algorithm, byte keyReference) throws POReIDException, CardException {
        ResponseAPDU responseApdu;

        if (null == algorithm){
            throw new POReIDException("Algoritmo não suportado");
        }
        
        responseApdu = channel.transmit(new CommandAPDU(0x00, 0x22, 0x41, 0xB6, new byte[]{(byte) 0x80, (byte) 0x01, algorithm, (byte) 0x84, (byte) 0x01, keyReference}));
        if (0x9000 != responseApdu.getSW()) {
            throw new POReIDException("Código de estado não esperado: " + Integer.toHexString(responseApdu.getSW()));
        }
    }

    
    @Override
    protected byte[] getNFillModifyPinAPDU(Pin pin, byte[][] pins) {
        byte[] apdu = getModifyPinAPDU(pin);
        System.arraycopy(pins[0], 0, apdu, 5, pins[0].length);
        System.arraycopy(pins[1], 0, apdu, 13, pins[1].length);
        
        return apdu;
    }

    
    @Override
    protected byte[] getModifyPinAPDU(Pin pin) {
        byte pad = pin.getPadChar();
        return new byte[]{0x00, 0x24, 0x00, pin.getReference(), 0x10, pad, pad, pad, pad, pad, pad, pad, pad, pad, pad, pad, pad, pad, pad, pad, pad};
    }

    @Override
    protected boolean verifyToModify() {
        return false;
    }
}
