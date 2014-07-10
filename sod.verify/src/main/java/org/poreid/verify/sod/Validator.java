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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.poreid.verify.util.Util;

/**
 *
 * @author POReID
 */
public class Validator {
    private final String BI = "bi";
    private final KeyStore keystore;
    private X509Certificate certificate;
    private SOD sod;
    private CitizenIdentificationAttributes id;
    private CitizenAddressAttributes address;
    private CitizenPhotoAttributes photo;
    private UUID uuid;
    private byte[] signatureBytes;    
    
    
    public Validator(KeyStore keystore){
        this.keystore = keystore;
    }
    
    
    public void setSOD(byte[] sod) throws ValidatorException{
        try {
            this.sod =  (null != sod) ? new SOD(Arrays.copyOfRange(sod, 4, sod.length), keystore) : null;
        } catch (SODException ex) {
            throw new ValidatorException("Formato inválido - SOD", ex);
        }
    }
    
    
    public void setID(byte[] id){
        this.id = (null != id) ? new CitizenIdentificationAttributes(id) : null;
    }
    
    
    public void setAddress(byte[] address){
        this.address = (null != address) ? new CitizenAddressAttributes(address) : null;
    }
    
    
    public void setPhoto(byte[] photo){        
        this.photo = (null != photo) ? new CitizenPhotoAttributes(photo) : null;
    }
    
    
    public void validate() throws ValidatorException{
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            if (null != id) {
                md.update(id.getRawData());
            }
            if (null != address) {
                md.update(address.getRawData());
            }
            if (null != photo) {
                md.update(photo.getRawData());
            }
            md.update(uuid.toString().getBytes());        
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(certificate);
            sig.update(md.digest());            
            if (!sig.verify(signatureBytes)){
                throw new ValidatorException("Não foi possivel validar os dados enviados (assinatura)");
            }
            
            if (!Util.isLeafCertificateValid(keystore, certificate)){
                throw new ValidatorException("Não foi possivel validar os dados enviados (certificado)");
            }
            
            if (null != sod) {
                if (sod.verify()) {
                    if (!Arrays.equals(id.getHash(), sod.getCitizenIdentificationHash())) {
                        throw new ValidatorException("Resumo da identificação do cidadão não coincide com o resumo no SOD");
                    }

                    if (null != address && !Arrays.equals(address.getHash(), sod.getCitizenAddressHash())) {
                        throw new ValidatorException("Resumo da morada do cidadão não coincide com o resumo no SOD");
                    }

                    if (null != photo && !Arrays.equals(photo.getHash(), sod.getCitizenPhoto())) {
                        throw new ValidatorException("Resumo da fotografia do cidadão não coincide com o resumo no SOD");
                    }
                } else {
                    throw new ValidatorException("Não foi possivel validar o SOD");
                }
                
                if (!getCitizenIdentification().getCivilianIdNumber().equals(getCivilianIdNumber(certificate))) {
                    throw new ValidatorException("Os dados enviados não coincidem com os dados do certificado");
                }
            }
        } catch (InvalidKeyException | SignatureException | InvalidNameException | LeafCertificateValidationException | NoSuchAlgorithmException | UnsupportedEncodingException | SODException ex) {
            throw new ValidatorException(ex.getMessage(), ex);        
        }
    }
    
    
    private String getCivilianIdNumber(X509Certificate certificate) throws InvalidNameException {        
        String serialNumber = BCStyle.INSTANCE.oidToDisplayName(BCStyle.SERIALNUMBER);
        Map<String, String> oidMap = new HashMap<>();        
        
        oidMap.put(BCStyle.SERIALNUMBER.getId(), serialNumber);        
        String subjectName = certificate.getSubjectX500Principal().getName(X500Principal.RFC2253, oidMap);

        for (Rdn rdn : new LdapName(subjectName).getRdns()) {
            if (serialNumber.equalsIgnoreCase(rdn.getType())) {
                return rdn.getValue().toString().toLowerCase().replace(BI, "");
            }
        }

        return "";
    }
    
    
    public CitizenAddressAttributes getCitizenAddress(){
        return address;
    }
    
    
    public CitizenIdentificationAttributes getCitizenIdentification(){
        return id;
    }
    
    
    public CitizenPhotoAttributes getPhotoAttributes(){
        return photo;
    }

    
    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    
    public void setUUID(UUID recvUuid) {
        this.uuid = recvUuid;
    }

    
    public void setSignature(byte[] signatureBytes) {
        this.signatureBytes = signatureBytes;
    }
}
