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

import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import org.poreid.config.POReIDConfig;


/**
 *
 * @author POReID
 */
public class POReIDX509KeyManager extends X509ExtendedKeyManager {
    private KeyStore keyStore;

    
    public POReIDX509KeyManager() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        this(null);
    }

    
    public POReIDX509KeyManager(final ManagerFactoryParameters mfp) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        POReIDKeyStoreParameter pteIDKeyStoreParameter;
        POReIDsslKeyStoreParameter pteIDsslKeyStoreParameter;
        
        this.keyStore = KeyStore.getInstance(POReIDConfig.POREID);
        
        if (null != mfp) {
            if (mfp instanceof POReIDsslManagerFactoryParameters){
                pteIDsslKeyStoreParameter = new POReIDsslKeyStoreParameter();
                pteIDsslKeyStoreParameter.setP(((POReIDsslManagerFactoryParameters)mfp).getP());
                pteIDsslKeyStoreParameter.setCard(((POReIDsslManagerFactoryParameters)mfp).getCard());
                this.keyStore.load(pteIDsslKeyStoreParameter);
            } else {
               if (mfp instanceof POReIDManagerFactoryParameters){
                   pteIDKeyStoreParameter = new POReIDKeyStoreParameter();
                   pteIDKeyStoreParameter.setLocale(((POReIDManagerFactoryParameters)mfp).getLocale());
                   pteIDKeyStoreParameter.setCard(((POReIDManagerFactoryParameters)mfp).getCard());
                   this.keyStore.load(pteIDKeyStoreParameter);
                }          
            }
        } else {
            this.keyStore.load(null);
        }
    }

    
    @Override
    public String chooseClientAlias(final String[] keyTypes, final Principal[] issuers, final Socket socket) {
        for (String keyType : keyTypes) {
            if (POReIDConfig.RSA.equalsIgnoreCase(keyType)) {
                return POReIDConfig.POREID;
            }
        }
        
        return null;
    }

    
    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        return null;
    }

    
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        if (POReIDConfig.POREID.equalsIgnoreCase(alias)) {
            Certificate[] certificateChain;
            try {
                certificateChain = this.keyStore.getCertificateChain(POReIDConfig.AUTENTICACAO);
            } catch (final KeyStoreException e) {
                return null;
            }
            final X509Certificate[] x509CertificateChain = new X509Certificate[certificateChain.length];
            for (int idx = 0; idx < certificateChain.length; idx++) {
                x509CertificateChain[idx] = (X509Certificate) certificateChain[idx];
            }
            return x509CertificateChain;
        }
        return null;
    }

    
    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return null;
    }

    
    @Override
    public PrivateKey getPrivateKey(final String alias) {
        if (POReIDConfig.POREID.equalsIgnoreCase(alias)) {
            PrivateKey privateKey;
            try {
                privateKey = (PrivateKey) this.keyStore.getKey(POReIDConfig.AUTENTICACAO, null);
            } catch (final KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
                return null;
            }
            return privateKey;
        }
        return null;
    }

    
    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return null;
    }

    
    @Override
    public String chooseEngineClientAlias(final String[] keyType, final Principal[] issuers, final SSLEngine engine) {
        return super.chooseEngineClientAlias(keyType, issuers, engine);
    }

    
    @Override
    public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
        return super.chooseEngineServerAlias(keyType, issuers, engine);
    }
}
