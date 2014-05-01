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
package exemplo;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;
import org.poreid.verify.sod.Validator;
import org.poreid.verify.sod.ValidatorException;

/**
 *
 * @author POReID
 */
public class Server {
    private UUID uuid;
    private UUID recvUuid;
    private byte[] sod;
    private byte[] authCert;
    private byte[] id;
    private byte[] photo;
    private byte[] address;
    private byte[] signatureBytes;
    
    
    public String getUUID(){
        uuid = UUID.randomUUID();
        return uuid.toString();
    }
    
    public void receiveNonce(String uuid){
        this.recvUuid = UUID.fromString(uuid);
    }
    
    public void receiveSOD(byte[] sod){
        this.sod = sod;
    }
    
    public void receiveAuthCert(byte[] authCert){
        this.authCert = authCert;
    }
    
    public void receiveID(byte[] id){
        this.id = id;
    }
    
    public void receivePhoto(byte[] photo){
        this.photo = photo;
    }
    
    public void receiveAddress(byte[] address){
        this.address = address;
    }
    
    public void receiveSignature(byte[] signatureBytes){
        this.signatureBytes = signatureBytes;
    }
    
    public void validate() throws InvalidDataException {
        
        try {
            if (!uuid.equals(recvUuid)){
                throw new InvalidDataException("Não foi possivel validar os dados enviados");
            }
                           
            KeyStore kstore = KeyStore.getInstance("JKS");
            kstore.load(Server.class.getResourceAsStream("/poreid.cc.ks"), null);
                   
            Validator validator = new Validator(kstore);
            validator.setCertificate(readCertificate(authCert));
            validator.setUUID(recvUuid);
            validator.setSignature(signatureBytes);
            validator.setSOD(sod);
            validator.setID(id);
            validator.setAddress(address);
            validator.setPhoto(photo);
            
            validator.validate();        
        } catch (NoSuchAlgorithmException  | CertificateException | IOException | KeyStoreException | ValidatorException ex) {
            throw new InvalidDataException("Não foi possivel validar os dados enviados ("+ex.getMessage()+")", ex);        
        }
    }
    
    
    private X509Certificate readCertificate(byte[] cert) throws CertificateException, FileNotFoundException, IOException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");       
        return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert));    
    }
}
