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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;


public class POReIDKeyManagerFactory extends KeyManagerFactorySpi {
    private ManagerFactoryParameters mfp;

    
    @Override
    protected KeyManager[] engineGetKeyManagers() {
        KeyManager eidKeyManager;
        
        try {
            eidKeyManager = new POReIDX509KeyManager(this.mfp);
        } catch (final KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new IllegalStateException(e);
        }
        final KeyManager[] keyManagers = new KeyManager[]{eidKeyManager};
        return keyManagers;
    }

    
    @Override
    protected void engineInit(final ManagerFactoryParameters mfp) throws InvalidAlgorithmParameterException {
        
        if (null == mfp) {
            return;
        }
        
        if (mfp instanceof POReIDsslManagerFactoryParameters || mfp instanceof POReIDManagerFactoryParameters){
            this.mfp = mfp;
        } else {
            throw new InvalidAlgorithmParameterException();
        }
    }

    
    @Override
    protected void engineInit(final KeyStore keyStore, final char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        throw new UnsupportedOperationException("Operação não suportada");
    }
}
