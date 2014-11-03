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
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.poreid.POReIDException;
import org.poreid.POReIDSmartCard;
import org.poreid.config.POReIDConfig;


/**
 *
 * @author POReID
 */
public final class POReIDSocketFactory {


    public static SSLSocketFactory getSSLSocketFactory(TrustManager[] tms) throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(POReIDConfig.POREID);
        sslContext.init(keyManagerFactory.getKeyManagers(), tms, new SecureRandom());
        
        return sslContext.getSocketFactory();
    }
    
    
    public static SSLSocketFactory getSSLSocketFactory(String trustStorePath, String trustStorePassword) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException, CertificateException {    
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(org.poreid.crypto.POReIDSocketFactory.class.getResourceAsStream(trustStorePath), trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(KeyManagerFactory.getInstance(POReIDConfig.POREID).getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        
        return sslContext.getSocketFactory();
    }
    
    
    public static SSLSocketFactory getSSLSocketFactory(POReIDSmartCard card, String trustStorePath, String trustStorePassword) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException, CertificateException, InvalidAlgorithmParameterException {    
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(org.poreid.crypto.POReIDSocketFactory.class.getResourceAsStream(trustStorePath), trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS"); 
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(POReIDConfig.POREID);
        POReIDManagerFactoryParameters spec = new POReIDManagerFactoryParameters();
        spec.setCard(card);
        keyManagerFactory.init(spec);    
        sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        
        return sslContext.getSocketFactory();
    }
    
    
    public static SSLSocketFactory getSSLSocketFactoryOTP(CanContinue can, POReIDSmartCard card,  String trustStorePath, String trustStorePassword, byte[] p) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException, CertificateException, InvalidAlgorithmParameterException, POReIDException {    
        if (!can.proceed()){
            throw new POReIDException("o método getSSLSocketFactory(5) não pode ser invocado fora do contexto OTP");
        }
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(org.poreid.crypto.POReIDSocketFactory.class.getResourceAsStream(trustStorePath), trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(POReIDConfig.POREID);
        POReIDsslManagerFactoryParameters spec = new POReIDsslManagerFactoryParameters();
        spec.setP(p);
        spec.setCard(card);
        keyManagerFactory.init(spec);
        sslContext.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        
        return sslContext.getSocketFactory();
    }            
}
