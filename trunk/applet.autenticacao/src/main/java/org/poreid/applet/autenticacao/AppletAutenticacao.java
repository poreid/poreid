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

package org.poreid.applet.autenticacao;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JApplet;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
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
 * @author ruim
 */
public class AppletAutenticacao extends JApplet {
    private static final String HTTPS = "https";
    private String nonce;
    private URL postURL;
    private String[] requestedData = null;
    
    
    @Override
    public String[][] getParameterInfo() {
        String pinfo[][] = {
            {"nonce", "string", "identificador único"},
            {"post.url", "url", "url para onde efetuar o post"},
            {"data.requested", "string", "dados requisitados para além da assinatura (id, morada, foto) em formato csv"}
        };
        
        return pinfo;
    }
    
    
    @Override
    public void init() {
        
        if (!HTTPS.equalsIgnoreCase(this.getCodeBase().getProtocol())) {
            Logger.getLogger(AppletAutenticacao.class.getName()).log(Level.SEVERE, "Erro: necessário utilizar https");
            System.exit(0);
        } else {
            nonce = getParameter("nonce");
            String url = getParameter("post.url");
            String requestedDataList = getParameter("data.requested");

            Security.addProvider(new POReIDProvider());

            if (null != url && !url.isEmpty()) {
                try {
                    postURL = new URL(url);
                    if (!HTTPS.equalsIgnoreCase(postURL.getProtocol())) {
                        postError(postURL, new ErrorMessage("Erro: parâmetro post.url não usa https"));
                    }

                    if (null == nonce || nonce.isEmpty()) {
                        postError(postURL, new ErrorMessage("Erro: falta parâmetro nonce"));
                    }

                    if (null != requestedDataList && !requestedDataList.isEmpty()) {
                        requestedData = requestedDataList.split(",");
                    }
                } catch (MalformedURLException ex) {
                    Logger.getLogger(AppletAutenticacao.class.getName()).log(Level.SEVERE, "parâmetro post.url inválido", ex);
                    System.exit(0);
                }
            } else {
                Logger.getLogger(AppletAutenticacao.class.getName()).log(Level.SEVERE, "parâmetro post.url não fornecido");
                System.exit(0);
            }
        }
    }
    
    
    @Override
    public void start() {
        byte[] id = null;
        byte[] morada = null;
        byte[] foto = null;
        CitizenData cd = new CitizenData();
        
        try {
            CitizenCard cc = CardFactory.getCard();
            
            if (null != requestedData) {
                for (String reqData : requestedData) {
                    switch (reqData.toLowerCase()) {
                        case "id":
                            id = cc.getID().getRawData();
                            cd.setId(id);
                            break;
                        case "morada":
                            morada = cc.getAddress().getRawData();
                            cd.setAddress(morada);
                            break;
                        case "foto":
                            foto = cc.getPhotoData().getRawData();
                            cd.setPhoto(foto);
                            break;
                    }
                }
            }
            
            if (null != id || null != morada || null != foto) {
                cd.setSod(cc.getSOD());
            }
            
            cd.setCertificate(cc.getAuthenticationCertificate().getEncoded());
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            if (null != id) {
                md.update(id);
            }
            if (null != morada){
                md.update(morada);
            }
            if (null != foto){
                md.update(foto);
            }
            md.update(nonce.getBytes());
            
            POReIDKeyStoreParameter ksParam = new POReIDKeyStoreParameter();
            ksParam.setCard(cc);
            KeyStore ks = KeyStore.getInstance(POReIDConfig.POREID);
            ks.load(ksParam);
            Signature signature = Signature.getInstance("SHA256withRSA");
            PrivateKey pk = (PrivateKey) ks.getKey(POReIDConfig.AUTENTICACAO, null);
            signature.initSign(pk);
            signature.update(md.digest());                     
            cd.setSignature(signature.sign());
            
            createOkSubmitForm(postURL, cd);
        } catch (PinTimeoutException | PinEntryCancelledException | PinBlockedException | CardNotPresentException | CertificateNotFound | CardTerminalNotPresentException | UnknownCardException | CanceledSelectionException | POReIDException | SmartCardFileException | NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException | UnrecoverableKeyException | InvalidKeyException | SignatureException ex) {
            createErrorSubmitForm(postURL,new ErrorMessage("Erro: verifique exceção", ex));
        }
        
        System.exit(0);
    }
    
    
    private void postError(URL postURL, ErrorMessage message){
        createErrorSubmitForm(postURL, message);
        System.exit(0);
    }

    
    private void createErrorSubmitForm(URL submitUrl, ErrorMessage message) throws JSException {
        JSObject document = (JSObject) JSObject.getWindow(this).getMember("document");
        JSObject body = (JSObject) document.getMember("body");
        
        JSObject redirectForm = (JSObject) document.call("createElement", new Object[]{"form"});
        
        redirectForm.setMember("name", "redirectForm");
        redirectForm.setMember("method", "POST");
        redirectForm.setMember("action", submitUrl.getPath());
        
        addInputField(document, redirectForm, "mensagem", message.getMessage());
        addInputField(document, redirectForm, "excecao", message.getThrowableMessage());
        
        body.call("appendChild", new Object[]{redirectForm});
        
        redirectForm.call("submit");
    }
    
    
    private void createOkSubmitForm(URL submitUrl, CitizenData data) throws JSException {
        
        JSObject document = (JSObject) JSObject.getWindow(this).getMember("document");
        JSObject body = (JSObject) document.getMember("body");
        
        JSObject redirectForm = (JSObject) document.call("createElement", new Object[]{"form"});
        
        redirectForm.setMember("name", "redirectForm");
        redirectForm.setMember("method", "POST");
        redirectForm.setMember("action", submitUrl.getPath());
        
        addInputField(document, redirectForm, "id", data.getID());
        addInputField(document, redirectForm, "morada", data.getAddress());
        addInputField(document, redirectForm, "foto", data.getPhoto());
        addInputField(document, redirectForm, "sod", data.getSod());   
        addInputField(document, redirectForm, "certificado", data.getCertificate());
        addInputField(document, redirectForm, "assinatura", data.getSignature());
        addInputField(document, redirectForm, "nonce", nonce);
        
        body.call("appendChild", new Object[]{redirectForm});
        
        redirectForm.call("submit");
    }
    
    
    private void addInputField(JSObject parentDocument, JSObject targetForm, String fieldName, String fieldValue) throws JSException {
        if (null != fieldValue) {
            JSObject newField = (JSObject) parentDocument.call("createElement", new Object[]{"input"});
            newField.setMember("type", "hidden");
            newField.setMember("name", fieldName);
            newField.setMember("value", fieldValue);

            targetForm.call("appendChild", new Object[]{newField});
        }
    }
}
