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
package org.poreid.verify.ocsp;

/**
 *
 * @author POReID
 */
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.cert.ocsp.jcajce.JcaCertificateID;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

public class OCSPClient {

    private static byte[] sentNonce;
    private final X509Certificate issuer;
    private final X509Certificate certificate;
    private URL url;

    public OCSPClient(X509Certificate issuer, X509Certificate certificate) {
        this.issuer = issuer;
        this.certificate = certificate;
        this.url = getOcspUrlFromCertificate(certificate);
    }

    private OCSPReq generateOCSPRequest(X509Certificate issuerCert, BigInteger serialNumber) throws CertificateEncodingException, OperatorCreationException, OCSPException, IOException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        OCSPReqBuilder gen = new OCSPReqBuilder();
        gen.addRequest(new JcaCertificateID(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build().get(CertificateID.HASH_SHA1), issuerCert, serialNumber));

        BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
        Extension ext = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, true, new DEROctetString(nonce.toByteArray()));
        gen.setRequestExtensions(new Extensions(new Extension[]{ext}));
        sentNonce = ext.getExtnId().getEncoded();

        return gen.build();
    }

    /* não é obrigatório que tenha, mas os certificados do CC têm */
    private URL getOcspUrlFromCertificate(X509Certificate certificate) {
        byte[] octetBytes = certificate.getExtensionValue(org.bouncycastle.asn1.x509.Extension.authorityInfoAccess.getId());
        
        if (null != octetBytes) {
            try {
                byte[] encoded = X509ExtensionUtil.fromExtensionValue(octetBytes).getEncoded();
                ASN1Sequence seq = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(encoded));
                AuthorityInformationAccess access = AuthorityInformationAccess.getInstance(seq);
                for (AccessDescription accessDescription : access.getAccessDescriptions()){
                    if (accessDescription.getAccessMethod().equals(AccessDescription.id_ad_ocsp)){
                        url = new URL(accessDescription.getAccessLocation().getName().toString());
                        break;
                    }
                }                
            } catch (IOException ignore) {
            }
        }

        return url;
    }

    public boolean haveOCSP() {
        return (null != url);
    }

    public boolean checkOCSP() throws OCSPValidationException {
        boolean retval = false;
        
        try {

            if (null == url) {
                throw new OCSPValidationException("Certificado não tem validação por OCSP");
            }

            byte[] encodedOcspRequest = generateOCSPRequest(issuer, certificate.getSerialNumber()).getEncoded();

            HttpURLConnection httpConnection;
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("Content-Type", "application/ocsp-request");
            httpConnection.setRequestProperty("Accept", "application/ocsp-response");
            httpConnection.setDoOutput(true);

            try (DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(httpConnection.getOutputStream()))) {
                dataOut.write(encodedOcspRequest);
                dataOut.flush();
            }

            InputStream in = (InputStream) httpConnection.getContent();

            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
            
            OCSPResp ocspResponse = new OCSPResp(in);
            BasicOCSPResp basicResponse = (BasicOCSPResp) ocspResponse.getResponseObject();            
            X509CertificateHolder certHolder = basicResponse.getCerts()[0];
            
            if (!basicResponse.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(issuer))){            
                // Certificado tem de ter uma Key Purpose ID for authorized responders
                if (!ExtendedKeyUsage.fromExtensions(certHolder.getExtensions()).hasKeyPurposeId(KeyPurposeId.id_kp_OCSPSigning)){
                    return false;
                }
                // Certificado tem de ser emitido pela mesma CA do certificado que estamos a verificar
                if (!certHolder.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(issuer))){
                    return false;
                }
                // Validar assinatura na resposta ocsp
                if (!basicResponse.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(certHolder))){
                    return false;
                }                
            }
                
            // Politica de Certificados do SCEE
            if (null == certHolder.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck).getExtnId()) {
                throw new OCSPValidationException("Extensão id_pkix_ocsp_nocheck não encontrada no certificado (Politica de Certificados do SCEE)");
            }
            
            SingleResp[] responses = basicResponse.getResponses();
            byte[] receivedNonce = basicResponse.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce).getExtnId().getEncoded();
            if (Arrays.equals(receivedNonce, sentNonce) && responses[0].getCertID().getSerialNumber().equals(certificate.getSerialNumber())) {
                retval = responses[0].getCertStatus() == CertificateStatus.GOOD;
            }
            
            return retval;
        } catch (CertificateEncodingException | OperatorCreationException | OCSPException | IOException ex) {
            throw new OCSPValidationException("Não foi possivel efetuar a validação através de OCSP (" + certificate.getSubjectX500Principal().getName() + ")", ex);
        } catch (CertException | CertificateException ex) {
            throw new OCSPValidationException("Não foi possivel efetuar a validação através de OCSP (" + certificate.getSubjectX500Principal().getName() + ")", ex);
        }
    }
}
