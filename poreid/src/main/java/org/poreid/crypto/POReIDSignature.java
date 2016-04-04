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

package org.poreid.crypto;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.util.HashMap;
import java.util.Map;
import org.poreid.RSAPaddingSchemes;
import org.poreid.config.POReIDConfig;

/**
 *
 * @author POReID
 */
public class POReIDSignature extends SignatureSpi {

    private final static Map<String, String> digestAlgos;
    private final RSAPaddingSchemes paddingScheme;
    private final MessageDigest messageDigest;
    private POReIDPrivateKey privateKey;
    private Signature verifySignature;
    private final String signatureAlgorithm;
    private final ByteArrayOutputStream precomputedDigestOutputStream;

    static {
        digestAlgos = new HashMap<>();
        digestAlgos.put("SHA1withRSA", "SHA-1");
        digestAlgos.put("SHA1withRSA/ISO9796-2", "SHA-1");
        digestAlgos.put("SHA1withRSA/PKCS#1", "SHA-1");
        digestAlgos.put("SHA1withRSA/RFC2409", "SHA-1");
        digestAlgos.put("SHA224withRSA", "SHA-224"); 
        digestAlgos.put("SHA256withRSA", "SHA-256"); 
        digestAlgos.put("SHA384withRSA", "SHA-384"); 
        digestAlgos.put("SHA512withRSA", "SHA-512"); 
        digestAlgos.put("NONEwithRSA", null);
    }

    
    POReIDSignature(final String signatureAlgorithm) throws NoSuchAlgorithmException {
        this.signatureAlgorithm = signatureAlgorithm;
        if (false == digestAlgos.containsKey(signatureAlgorithm)) {
            throw new NoSuchAlgorithmException(signatureAlgorithm);
        }
        
        int index = signatureAlgorithm.lastIndexOf('/');
        if (index < 0){
            paddingScheme = RSAPaddingSchemes.PKCS1;
        } else {
            paddingScheme = RSAPaddingSchemes.contains(signatureAlgorithm.substring(index, signatureAlgorithm.length()));
            if (null == paddingScheme){
                throw new NoSuchAlgorithmException(signatureAlgorithm.substring(index, signatureAlgorithm.length()));
            }
        } 
        
        final String digestAlgo = digestAlgos.get(signatureAlgorithm);
        if (null != digestAlgo) {
            this.messageDigest = MessageDigest.getInstance(digestAlgo);
            this.precomputedDigestOutputStream = null;
        } else {
            this.messageDigest = null;
            this.precomputedDigestOutputStream = new ByteArrayOutputStream();
        }
    }

    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (null == this.verifySignature) {
            try {
                this.verifySignature = Signature.getInstance(this.signatureAlgorithm);
            } catch (final NoSuchAlgorithmException ex) {
                throw new InvalidKeyException("Algoritmo não encontrado: " + ex.getMessage(), ex);
            }
        }
        this.verifySignature.initVerify(publicKey);
    }

    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        if (null == privateKey){
            throw new InvalidKeyException("Chave nula");
        }
        
        if (!(privateKey instanceof POReIDPrivateKey)) {
            throw new InvalidKeyException("Chave fornecida não é do tipo esperado "+privateKey.getClass().getName()+" != "+POReIDPrivateKey.class.getName());
        }
        
        this.privateKey = (POReIDPrivateKey) privateKey;
        if (null != this.messageDigest) {
            this.messageDigest.reset();
        }
    }

    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.messageDigest.update(b);
        if (null != this.verifySignature) {
            this.verifySignature.update(b);
        }
    }

    
    @Override
    protected void engineUpdate(final byte[] b, final int off, final int len) throws SignatureException {
        if (null != this.messageDigest) {
            this.messageDigest.update(b, off, len);
        }
        if (null != this.precomputedDigestOutputStream) {
            this.precomputedDigestOutputStream.write(b, off, len);
        }
        if (null != this.verifySignature) {
            this.verifySignature.update(b, off, len);
        }
    }

    
    @Override
    protected byte[] engineSign() throws SignatureException {
        final byte[] digestValue;
        String digestAlgo;
        if (null != this.messageDigest) {
            digestValue = this.messageDigest.digest();
            digestAlgo = this.messageDigest.getAlgorithm();
        } else if (null != this.precomputedDigestOutputStream) {
            digestValue = this.precomputedDigestOutputStream.toByteArray();
            digestAlgo = POReIDConfig.NONE;
        } else {
            throw new SignatureException();
        }
        
        return this.privateKey.sign(digestValue, digestAlgo, paddingScheme);
    }

    
    @Override
    protected boolean engineVerify(final byte[] sigBytes) throws SignatureException {
        if (null == this.verifySignature) {
            throw new SignatureException("Necessário efetuar initVerify primeiro");
        }
        final boolean result = this.verifySignature.verify(sigBytes);
        return result;
    }

    
    @Override
    @Deprecated
    protected void engineSetParameter(final String param, final Object value) throws InvalidParameterException {
    }

    
    @Override
    @Deprecated
    protected Object engineGetParameter(final String param) throws InvalidParameterException {
        return null;
    }
}
