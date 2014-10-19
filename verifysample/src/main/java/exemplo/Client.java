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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.poreid.CardFactory;
import org.poreid.CardNotPresentException;
import org.poreid.CardTerminalNotPresentException;
import org.poreid.CertificateNotFound;
import org.poreid.POReIDException;
import org.poreid.SmartCardFileException;
import org.poreid.UnknownCardException;
import org.poreid.cc.CitizenCard;
import org.poreid.config.POReIDConfig;
import org.poreid.crypto.POReIDKeyStoreParameter;
import org.poreid.crypto.POReIDProvider;
import org.poreid.dialogs.pindialogs.PinBlockedException;
import org.poreid.dialogs.pindialogs.PinEntryCancelledException;
import org.poreid.dialogs.pindialogs.PinTimeoutException;
import org.poreid.dialogs.selectcard.CanceledSelectionException;

/**
 *
 * @author POReID
 */
public class Client {
    private byte[] sod;
    private byte[] authcert;
    private byte[] id;
    private byte[] photo;
    private byte[] address;
    private final String uuid;
    private byte[] signatureBytes;
    
    
    public Client(String uuid){
        this.uuid = uuid;
    }
    
    public void signAndReadAttributes() {
        try {
            Security.addProvider(new POReIDProvider());
            CitizenCard cc = CardFactory.getCard();
            
            sod = cc.getSOD();
            authcert = cc.getAuthenticationCertificate().getEncoded();
            id = cc.getID().getRawData();
            photo = cc.getPhotoData().getRawData();
            address = cc.getAddress().getRawData();
                        
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(id);
            md.update(address);
            md.update(photo);
            md.update(uuid.getBytes());
            
            /* já temos uma instância do CC vamos utilizá-la */
            POReIDKeyStoreParameter ksParam = new POReIDKeyStoreParameter();
            ksParam.setCard(cc);
            KeyStore ks = KeyStore.getInstance(POReIDConfig.POREID);
            ks.load(ksParam);
            Signature signature = Signature.getInstance("SHA256withRSA");
            PrivateKey pk = (PrivateKey) ks.getKey(POReIDConfig.AUTENTICACAO, null);
            signature.initSign(pk);
            signature.update(md.digest());                     
            signatureBytes = signature.sign();
            
        } catch (PinTimeoutException | PinEntryCancelledException | PinBlockedException | CertificateEncodingException | CardTerminalNotPresentException | UnknownCardException | CardNotPresentException | CanceledSelectionException | POReIDException | SmartCardFileException | CertificateNotFound | NoSuchAlgorithmException | KeyStoreException | IOException | UnrecoverableKeyException | InvalidKeyException | SignatureException ex) {
            throw new RuntimeException("Não foi possivel ler o cartão("+ex.getMessage()+")",ex);        
        } catch (CertificateException ex) {
            throw new RuntimeException("Não foi possivel ler o cartão("+ex.getMessage()+")",ex);
        }
    }
    
    public String getUUID(){
        return uuid;
    }
    
    public byte[] getSOD(){
        return sod;
    }
    
    public byte[] getCertificate(){
        return authcert;
    }
    
    public byte[] getID(){
        return id;
    }
    
    public byte[] getPhoto(){
        return photo;                
    }
    
    public byte[] getAddress(){
        return address;                
    }
    
    public byte[] getSignature(){
        return signatureBytes;
    }
}
