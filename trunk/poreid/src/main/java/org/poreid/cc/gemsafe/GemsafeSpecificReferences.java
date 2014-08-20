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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.smartcardio.Card;
import org.poreid.DigestPrefixes;
import org.poreid.Pin;
import org.poreid.PkAlias;
import org.poreid.RSAPaddingSchemes;
import org.poreid.cc.CardSpecificReferences;
import org.poreid.cc.CitizenCard;
import org.poreid.config.POReIDConfig;

/**
 *
 * @author POReID
 */
public final class GemsafeSpecificReferences implements CardSpecificReferences{
    private final String SELECT_AID = "00A4040C07604632FF000002";
    private final String SELECT_EMV_AID = "00A4040007604632FF000001"; 
    private final String CDOL1 = "0000000000000000000000000000800000000000000000000000000000";
    private final byte TAMANHO_MIN_PIN = 4;
    private final byte TAMANHO_MAX_PIN = 8;
    private final ResourceBundle bundle;
    private final Card card;
    private final String cardReaderName;
    private final Locale locale;
    private final Map<RSAPaddingSchemes, Byte> algorithmID;
    private final Map<PkAlias, Pin> pinInfo;
    protected final Map<String, DigestPrefixes> digestsMap;   
    private final Map<String, Byte> fix;
    private final boolean cachePreferences;
    

    public GemsafeSpecificReferences(Card card, String cardReaderName, Locale locale, boolean cachePreferences){
        this.card = card;
        this.cardReaderName = cardReaderName;
        this.locale = locale;
        this.cachePreferences = cachePreferences;
        bundle = POReIDConfig.getBundle(CitizenCard.class.getSimpleName(), locale);
        
        algorithmID = new HashMap<>();
        //algorithmID.put(RSAPaddingSchemes.ISO9796_2, (byte)0x01); 
        algorithmID.put(RSAPaddingSchemes.PKCS1, (byte)0x02);
        //algorithmID.put(RSAPaddingSchemes.RFC2409, (byte)0x03); 
        
        pinInfo = new HashMap<>();
        pinInfo.put(PkAlias.AUTENTICACAO, new Pin(bundle.getString("authentication.pin"), TAMANHO_MIN_PIN, TAMANHO_MIN_PIN, POReIDConfig.IMAGE_AUTHENTICATION_LOCATION, (byte)0x81, (byte)0x02, SELECT_AID, (byte)0xFF));
        pinInfo.put(PkAlias.ASSINATURA, new Pin(bundle.getString("signature.pin"), TAMANHO_MIN_PIN, TAMANHO_MAX_PIN, POReIDConfig.IMAGE_SIGNATURE_LOCATION, (byte)0x82, (byte)0x01, SELECT_AID, (byte)0xFF));
        
        digestsMap = new HashMap<>();
        digestsMap.put("SHA-1", DigestPrefixes.SHA_1);
        digestsMap.put("SHA-256", DigestPrefixes.SHA_256);  
        digestsMap.put("NONE", DigestPrefixes.NONE);
        
        fix =  new HashMap<>();
        fix.put("SHA-256", (byte)0x40);
    }
    
    
    @Override
    public Pin getCryptoReferences(PkAlias pkAlias) {
        return pinInfo.get(pkAlias);
    }

    
    @Override
    public Pin getAddressPin() {
        return new Pin(bundle.getString("address.pin"), TAMANHO_MIN_PIN, TAMANHO_MAX_PIN, POReIDConfig.IMAGE_ADDRESS_LOCATION, (byte)0x83, SELECT_AID, (byte)0xFF);
    }

    
    @Override
    public String getAID() {
        return this.SELECT_AID;
    }
    
    
    @Override
    public String getEmvAID() {
        return this.SELECT_EMV_AID;
    }
    
    
    @Override
    public String getCDOL1() {
        return CDOL1;
    }

    
    @Override
    public Card getCard() {
        return this.card;
    }

    
    @Override
    public Byte getAlgorithmID(String digest, RSAPaddingSchemes scheme) { 
        return (byte) ((fix.containsKey(digest) ? fix.get(digest) : 0) | algorithmID.get(scheme));
    }
    
    
    @Override
    public String getCardReaderName() {
        return cardReaderName;
    }

    
    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public DigestPrefixes getDigestPrefix(String prefix) {
        return digestsMap.get(prefix);
    }    


    @Override
    public boolean isEMVCAPPin(Pin pin) {
        return pinInfo.get(PkAlias.AUTENTICACAO).equals(pin);
    }

    @Override
    public boolean getCachePreference() {
        return cachePreferences;
    }
}
