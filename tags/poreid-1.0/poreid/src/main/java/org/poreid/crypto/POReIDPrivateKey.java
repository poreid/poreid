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

package org.poreid.crypto;

import java.math.BigInteger;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import org.poreid.POReIDException;
import org.poreid.POReIDSmartCard;
import org.poreid.PkAlias;
import org.poreid.RSAPaddingSchemes;
import org.poreid.config.POReIDConfig;
import org.poreid.dialogs.pindialogs.PinBlockedException;
import org.poreid.dialogs.pindialogs.PinEntryCancelledException;
import org.poreid.dialogs.pindialogs.PinTimeoutException;


public final class POReIDPrivateKey implements RSAPrivateKey {
    private final POReIDSmartCard eIDCard;
    private final PkAlias pkAlias;
    private final byte[] pin;
    
    public POReIDPrivateKey(final POReIDSmartCard eIDCard, PkAlias alias, byte[] pin) {
        this.eIDCard = eIDCard;
        this.pkAlias = alias;
        this.pin = (POReIDConfig.isExternalPinCachePermitted()) ? pin : null;
    }
    
    protected POReIDPrivateKey(final POReIDSmartCard eIDCard, PkAlias alias, byte[] pin, boolean ssl) {
        this.eIDCard = eIDCard;
        this.pkAlias = alias;
        this.pin = pin;
    }
    
    @Override
    public String getAlgorithm() {
        return POReIDConfig.RSA;
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public byte[] getEncoded() {
        return null;
    }

    @Override
    public BigInteger getPrivateExponent() {
        throw new UnsupportedOperationException("Operação não suportada");
    }

    @Override
    public BigInteger getModulus() {
        throw new UnsupportedOperationException("Operação não suportada");
    }
    
    byte[] sign(final byte[] digestValue, final String digestAlgo, RSAPaddingSchemes paddingScheme) throws SignatureException {
        byte[] signatureValue = null;
        
        try {
            signatureValue = this.eIDCard.sign(digestValue, pin, digestAlgo, pkAlias, paddingScheme);
        } catch (final PinTimeoutException | PinEntryCancelledException | PinBlockedException | POReIDException ex) {
            throw new SignatureException("Erro não foi possivel gerar assinatura.", ex);
        }
        return signatureValue;
    }
}
