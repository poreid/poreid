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
package org.poreid.verify.sod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.icao.LDSSecurityObject;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.poreid.verify.util.Util;

/**
 *
 * @author POReID
 */
public class SOD {

    private final CMSSignedData cms;
    private final LDSSecurityObject lds;
    private final KeyStore keystore;

    protected SOD(byte[] sod, KeyStore keystore) throws SODException {
        try {
            cms = new CMSSignedData(sod);
            lds = LDSSecurityObject.getInstance(cms.getSignedContent().getContent()); 
            this.keystore = keystore;
        } catch (CMSException ex) {
            throw new SODException("não foi possivel instanciar o SOD", ex);
        }
    }
    
    
    private boolean isCertificateSelfSigned(X509Certificate certificate) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {  
        try {
            PublicKey key = certificate.getPublicKey();
            certificate.verify(key);
            return true;
        } catch (SignatureException | InvalidKeyException ex) {
            return false;
        }
    }
    

    protected boolean verify() throws SODException {
        try {
            /* verificar caminho de certificação sem ocsp/crl, aqui não é local para essas considerações */
            X509CertificateHolder holder = (X509CertificateHolder) cms.getCertificates().getMatches(null).iterator().next(); // apenas o primeiro certificado (só tem 1)
            X509Certificate cert = (X509Certificate) get(holder.getEncoded());

            SignerInformationStore signerInformationStore = cms.getSignerInfos();
            SignerInformation signerInformation = (SignerInformation) signerInformationStore.getSigners().iterator().next(); // apenas 1 assinatura (só tem 1)

            if (!Util.isLeafCertificateValid(keystore, cert)){
                return false;
            }
            
            /* verificar assinatura do cms */
            ContentVerifierProvider contentVerifierProvider = new JcaContentVerifierProviderBuilder().setProvider(new BouncyCastleProvider()).build(cert);
            DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider(new BouncyCastleProvider()).build();
            SignatureAlgorithmIdentifierFinder signatureAlgorithmIdentifierFinder = new DefaultSignatureAlgorithmIdentifierFinder();
            CMSSignatureAlgorithmNameGenerator signatureAlgorithmNameGenerator = new DefaultCMSSignatureAlgorithmNameGenerator();
            SignerInformationVerifier signerInformationVerifier = new SignerInformationVerifier(signatureAlgorithmNameGenerator, signatureAlgorithmIdentifierFinder, contentVerifierProvider, digestCalculatorProvider);

            return signerInformation.verify(signerInformationVerifier);

        } catch (LeafCertificateValidationException | IOException | CertificateException | OperatorCreationException | CMSException ex) {
            throw new SODException("Não foi possivel verificar o SOD ("+ex.getMessage()+")", ex);
        }
    }
    

    private Certificate get(final byte[] bytes) throws CertificateException {
        return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bytes));
    }
    
    
    protected byte[] getCitizenIdentificationHash(){
        return lds.getDatagroupHash()[0].getDataGroupHashValue().getOctets();
    }
    
    
    protected byte[] getCitizenAddressHash(){
        return lds.getDatagroupHash()[1].getDataGroupHashValue().getOctets();
    }
    
    
    protected byte[] getCitizenPhoto(){
        return lds.getDatagroupHash()[2].getDataGroupHashValue().getOctets();
    }
    
    
    protected byte[] getCitizenPublicKeyHash(){
        return lds.getDatagroupHash()[3].getDataGroupHashValue().getOctets();
    }
}
