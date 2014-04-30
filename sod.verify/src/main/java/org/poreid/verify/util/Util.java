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
package org.poreid.verify.util;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.poreid.verify.sod.LeafCertificateValidationException;
import org.poreid.verify.ocsp.OCSPClient;
import org.poreid.verify.ocsp.OCSPValidationException;

/**
 *
 * @author POReID
 */
public class Util {

    
    private static boolean isCertificateSelfSigned(X509Certificate certificate) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {  
        try {
            PublicKey key = certificate.getPublicKey();
            certificate.verify(key);
            return true;
        } catch (SignatureException | InvalidKeyException ex) {
            return false;
        }
    }
    
    public static boolean isLeafCertificateValid(KeyStore kstore, X509Certificate cert) throws LeafCertificateValidationException {
        try {
            CertPathBuilder pathBuilder = CertPathBuilder.getInstance("PKIX");
            X509CertSelector select = new X509CertSelector();
            select.setSubject(cert.getSubjectX500Principal().getEncoded());

            Set trustanchors = new HashSet();
            List<Certificate> certList = new ArrayList<>();
            certList.add(cert);
            Enumeration<String> enumeration = kstore.aliases();
            while (enumeration.hasMoreElements()) {
                X509Certificate certificate = (X509Certificate) kstore.getCertificate(enumeration.nextElement());
                if (certificate.getIssuerX500Principal().equals(certificate.getSubjectX500Principal())) {
                    if (isCertificateSelfSigned(certificate)) {
                        trustanchors.add(new TrustAnchor((X509Certificate) certificate, null));
                    }
                } else {
                    certList.add(certificate);
                }

            }

            PKIXBuilderParameters params = new PKIXBuilderParameters(trustanchors, select);
            CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList));
            params.addCertStore(certStore);
            params.setRevocationEnabled(false);
            CertPathBuilderResult cpbr = pathBuilder.build(params);
            List<X509Certificate> path = (List<X509Certificate>) cpbr.getCertPath().getCertificates();            
            X509Certificate issuer = (path.size()< 2 ? ((TrustAnchor)trustanchors.iterator().next()).getTrustedCert() : path.get(1));            
            OCSPClient client = new OCSPClient(issuer, path.get(0));
            
            return client.checkOCSP();
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException | OCSPValidationException | NoSuchProviderException | InvalidAlgorithmParameterException | CertPathBuilderException ex) {
            throw new LeafCertificateValidationException("Não foi possivel validar os dados enviados (" + ex.getMessage() + ")",ex);
        }
    }
}
