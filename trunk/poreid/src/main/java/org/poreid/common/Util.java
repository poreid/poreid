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
package org.poreid.common;

import org.poreid.CertificateChainNotFound;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author POReID
 */
public class Util {
    
    
    public static byte[] hexToBytes(String hexString) {
        return DatatypeConverter.parseHexBinary(hexString);
    }

    
    public static String bytesToHex(byte[] b) {
        return DatatypeConverter.printHexBinary(b);
    }

    
    public static String prettyBytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte _b : b) {
            sb.append(String.format("%02x", _b));
            sb.append(" ");
        }

        return sb.toString().toUpperCase().trim();
    }

    
    public static void prettyPrintBytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte _b : b) {
            sb.append(String.format("%02x", _b));
            sb.append(" ");
        }

        System.out.println(sb.toString().toUpperCase());
    }

    
    public static void prettyPrintBytesToHex(String msg, byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte _b : b) {
            sb.append(String.format("%02x", _b));
            sb.append(" ");
        }

        System.out.println(msg + sb.toString().toUpperCase().trim());
    }

    
    public static void prettyPrintBytesToHex(String startMsg, byte[] b, String endMsg) {
        StringBuilder sb = new StringBuilder();
        for (byte _b : b) {
            sb.append(String.format("%02x", _b));
            sb.append(" ");
        }

        System.out.println(startMsg + sb.toString().toUpperCase().trim() + endMsg);
    }

    
    public static X509Certificate readCertificate(File f) throws CertificateException, FileNotFoundException, IOException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = null;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(f))) {
            if (in.available() > 0) {
                cert = (X509Certificate) cf.generateCertificate(in);
            }
        }
        return cert;
    }

    
    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();

        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        return baos.toByteArray();
    }

    
    public static boolean isCertificateSelfSigned(X509Certificate cert) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        PublicKey key = cert.getPublicKey();
        
        try {
            cert.verify(key);
            return true;
        } catch (SignatureException | InvalidKeyException ex) {
            return false;
        }
    }
    
    
    public static List<X509Certificate> getCertificateChain(X509Certificate client, KeyStore ks) throws CertificateChainNotFound{ 
        try {
            List<X509Certificate> certChain = new ArrayList<>();
            
            
            X509Certificate[] certs = new X509Certificate[ks.size()];
            int i = 0;
            Enumeration<String> alias = ks.aliases();
            while (alias.hasMoreElements()) {
                certs[i++] = (X509Certificate) ks.getCertificate(alias.nextElement());
            }
            
            certChain.add(client);
            if (checkNBuildCertificateChain(client, certChain, certs)) {
                return certChain;
            } else {
                throw new CertificateChainNotFound("no chain found for "+client.getSubjectDN().getName()+" certificate");
            }
        } catch (KeyStoreException | CertificateException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException ex) {
            throw new CertificateChainNotFound("cannot obtain certificate chain", ex);
        }
    }
    
    
    public static boolean checkNBuildCertificateChain(X509Certificate client, List<X509Certificate> certChain, X509Certificate... trustedCerts) throws CertificateException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        int numCerts = trustedCerts.length;
        boolean found = false;
        PKIXParameters params;
        CertPath path;
        
        while (!found && numCerts > 0) { 
            path = CertificateFactory.getInstance("X.509").generateCertPath(Arrays.asList(new Certificate[]{client}));
            params = new PKIXParameters(Collections.singleton(new TrustAnchor(trustedCerts[--numCerts], null)));
            params.setRevocationEnabled(false);
            if (client.getIssuerDN().equals(trustedCerts[numCerts].getSubjectDN())) {
                try {
                    CertPathValidator.getInstance("PKIX").validate(path, params);
                    if (isCertificateSelfSigned(trustedCerts[numCerts])) {
                        if (null != certChain) {
                            certChain.add(trustedCerts[numCerts]);
                        }
                        found = true;
                    } else if (!client.equals(trustedCerts[numCerts])) {
                        if (null != certChain) {
                            certChain.add(trustedCerts[numCerts]);
                        }
                        found = checkNBuildCertificateChain(trustedCerts[numCerts], certChain, trustedCerts);
                    }
                } catch (CertPathValidatorException ignore) { }
            }
        }
        return found;
    }
    
    
    /* martelada para não fazer parse de asn1 e incluir um porradão de tralha */     
    public static String extractFromASN1(byte[] asn1, int offset, int length) {
        StringBuilder sb = new StringBuilder(length * 2);
        for (int i = offset; i < offset + length; i++) {
            sb.append(String.format("%02x", asn1[i]));
        }
        return sb.toString();
    }
}
