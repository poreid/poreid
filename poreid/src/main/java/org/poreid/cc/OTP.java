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
package org.poreid.cc;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import org.poreid.POReIDException;
import org.poreid.Pin;
import org.poreid.common.Util;
import org.poreid.crypto.CanContinue;
import org.poreid.crypto.POReIDSocketFactory;
import org.poreid.dialogs.dialog.DialogController;
import org.poreid.dialogs.pindialogs.otpfeedback.OTPFeedbackDialogController;
import org.poreid.json.JSONObject;
import org.poreid.json.JSONTokener;

/**
 *
 * @author POReID
 */
class OTP {
    private final String OTP_CONNECT_URL = "https://otp.cartaodecidadao.pt/CAPPINChange/connect";
    private final String OTP_SEND_PARAMETERS_URL = "https://otp.cartaodecidadao.pt/CAPPINChange/sendParameters";
    private final String OTP_SEND_CHANGE_PIN_RESPONSE_URL = "https://otp.cartaodecidadao.pt/CAPPINChange/sendChangePINResponse";
    private final String OTP_SCRIPT_COUNTER_PARAMETERS_URL = "https://otp.cartaodecidadao.pt/CAPPINChange/sendResetScriptCounterParameters";
    private final String OTP_SCRIPT_COUNTER_RESPONSE_URL = "https://otp.cartaodecidadao.pt/CAPPINChange/resetScriptCounterResponse";
    private final String OTP_TRUST_STORE = "/org/poreid/cc/keystores/poreid.cc.otp.ks";
    private final String OTP_TRUST_STORE_PASSWORD = "";
    private SSLSocketFactory sslSocketFactory;
    private String cookie;
    private final POReIDCard card;
    private Pin pin;
    private byte[] p;
    private final byte[] GET_PROCESSING_OPTIONS = new byte[]{(byte) 0x80, (byte) 0xA8,  0x00,  0x00,  0x02, (byte) 0x83,  0x00};
    private final byte[] GET_DATA__PIN_TRY_COUNTER = new byte[]{(byte) 0x80, (byte) 0xCA, (byte) 0x9F,  0x17,  0x04};
    private final byte[] READ_RECORD = new byte[]{ 0x00, (byte) 0xB2,  0x01,  0x0C,  0x5F};
    private final byte EMV_PLAIN_TEXT_PIN_PROP = (byte)0x80;
    private final byte COUNTER = 0;
    private final byte AAC = 0x00;
    private final byte TC = 0x40;
    private final byte ARQC = (byte)0x80;
    private boolean errorExpected;
    private OTPFeedbackDialogController otpDialogCtl;
    private final ResourceBundle bundle;
    private final Proxy proxy;
    
    protected OTP(POReIDCard card, Pin pin, ByteBuffer pins[]) throws POReIDException{
        try {
            this.card = card;
            this.pin = pin;
            this.p = pins[1].array();
            this.proxy = card.getCardSpecificReferences().getProxy();
            this.bundle = CCConfig.getBundle(OTP.class.getSimpleName(),card.getCardSpecificReferences().getLocale());
            sslSocketFactory = POReIDSocketFactory.getSSLSocketFactoryOTP(new CanContinue__(), card, OTP_TRUST_STORE, OTP_TRUST_STORE_PASSWORD, pins[0].array());
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | IOException | CertificateException | InvalidAlgorithmParameterException ex) {
            throw new POReIDException("Não foi possivel iniciar o processo de alteração do pin OTP", ex);
        }
    }
    
    
    protected void doOTPPinModify() throws POReIDException {
        byte[] unlockRequestApdu;
        byte[] cdol2;

        otpDialogCtl = OTPFeedbackDialogController.getInstance(pin.getLabel(), card.getCardSpecificReferences().getLocale());
        otpDialogCtl.displayOTPFeedbackDialog();
        
        try {
            httpPostDummyRequest();

            oTPTransmit(Util.hexToBytes(card.getCardSpecificReferences().getEmvAID()));

            unlockRequestApdu = httpPostNewPin(getOTPParameters(p));
            
            otpDialogCtl.updateState();
            ResponseAPDU response = oTPTransmit(unlockRequestApdu);
            
            httpPostChangeUnlockPINResponse(new PinChangeUnlockResponse(response.getSW()));
            
            cdol2 = httpPostResetScriptCounter(getOTPOnlineTransactionParameters(p));
            httpPostResetScriptCounterResponse(new ResetScriptCounterResponse(oTPTransmitIgnoreErrors(generateAC(TC, cdol2)).getSW()));
        } finally {
            oTPTransmit(Util.hexToBytes(card.getCardSpecificReferences().getAID()));
        }
    }
    
    
    protected void finish(){
        otpDialogCtl.updateState();
    }
    
    
    private void httpPostDummyRequest() throws POReIDException{
        try {
            String post = new JSONObject().put("connect", "").toString();
            HttpsURLConnection con = (HttpsURLConnection) new URL(OTP_CONNECT_URL).openConnection(this.proxy);
            con.setSSLSocketFactory(sslSocketFactory);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/plain");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", String.valueOf(post.getBytes(StandardCharsets.UTF_8).length));
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
                out.writeBytes(post);
                out.flush();
            }
            
            otpDialogCtl.updateState();
            
            if (HttpsURLConnection.HTTP_OK == con.getResponseCode()) {
                JSONObject js = new JSONObject(new JSONTokener(con.getInputStream()));
                if (js.has("connect")) {
                    cookie = con.getHeaderField("Set-Cookie");
                    return;
                }
            }
            
            warnCitizen(new POReIDException("Não foi possivel iniciar o processo de alteração do pin OTP"));
        } catch (IOException ex) {
            warnCitizen(new POReIDException("Não foi possivel iniciar o processo de alteração do pin OTP", ex));
        } 
    }
    
    
    private byte[] httpPostNewPin(PinPafUpdate ppu) throws POReIDException{    
        try {
            HttpsURLConnection con = (HttpsURLConnection) new URL(OTP_SEND_PARAMETERS_URL).openConnection(this.proxy);
            con.setSSLSocketFactory(sslSocketFactory);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/plain");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", "" + String.valueOf(ppu.toString().getBytes(StandardCharsets.UTF_8).length));
            con.setRequestProperty("Cookie", cookie);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
                out.writeBytes(ppu.toString());
                out.flush();
            }
            
            otpDialogCtl.updateState();
            
            if (HttpsURLConnection.HTTP_OK == con.getResponseCode()) {
                JSONObject js = new JSONObject(new JSONTokener(con.getInputStream()));
                if (js.has("PinChangeUnlockRequest") && js.getJSONObject("PinChangeUnlockRequest").has("apdu")) {
                    js.getJSONObject("PinChangeUnlockRequest").getString("apdu");                    
                    return Util.hexToBytes(js.getJSONObject("PinChangeUnlockRequest").getString("apdu"));           
                } 
            } 
            
            warnCitizen(new POReIDException("Não foi possivel continuar o processo de alteração do pin OTP"));
        } catch (IOException ex) {
            warnCitizen(new POReIDException("Não foi possivel continuar o processo de alteração do pin OTP", ex));
        }
        return null;
    }
    
    
    private void httpPostChangeUnlockPINResponse(PinChangeUnlockResponse pcur) throws POReIDException {
        try {
            HttpsURLConnection con = (HttpsURLConnection) new URL(OTP_SEND_CHANGE_PIN_RESPONSE_URL).openConnection(this.proxy);
            con.setSSLSocketFactory(sslSocketFactory);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/plain");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", String.valueOf(pcur.toString().getBytes(StandardCharsets.UTF_8).length));
            con.setRequestProperty("Cookie", cookie);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
                out.writeBytes(pcur.toString());
                out.flush();
            }
            
            if (HttpsURLConnection.HTTP_OK == con.getResponseCode()) {
                JSONObject js = new JSONObject(new JSONTokener(con.getInputStream()));
                if (js.has("sendChangePINResponse")){         
                    return;
                }
            }
            
            warnCitizen(new POReIDException("Não foi possivel continuar o processo de alteração do pin OTP"));
        } catch (IOException ex) {
            warnCitizen(new POReIDException("Não foi possivel continuar o processo de alteração do pin OTP", ex));
        }
    }
    
    
    private byte[] httpPostResetScriptCounter(OnlineTransactionParameters otp) throws POReIDException{
        try {
            HttpsURLConnection con = (HttpsURLConnection) new URL(OTP_SCRIPT_COUNTER_PARAMETERS_URL).openConnection(this.proxy);
            con.setSSLSocketFactory(sslSocketFactory);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/plain");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", String.valueOf(otp.toString().getBytes(StandardCharsets.UTF_8).length));
            con.setRequestProperty("Cookie", cookie);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
                out.writeBytes(otp.toString());
                out.flush();
            }
            
            otpDialogCtl.updateState();
            
            if (HttpsURLConnection.HTTP_OK == con.getResponseCode()) {
                JSONObject js = new JSONObject(new JSONTokener(con.getInputStream()));
                if (js.has("OnlineTransactionRequest") && js.getJSONObject("OnlineTransactionRequest").has("cdol2")) {
                    return Util.hexToBytes(js.getJSONObject("OnlineTransactionRequest").getString("cdol2"));
                }
            }
            
            warnCitizen(new POReIDException("Não foi possivel continuar o processo de alteração do pin OTP"));
        } catch (IOException ex) {
            warnCitizen(new POReIDException("Não foi possivel continuar o processo de alteração do pin OTP", ex));
        }
        return null;
    }

  
    private void httpPostResetScriptCounterResponse(ResetScriptCounterResponse rscr) throws POReIDException{
        try {
            HttpsURLConnection con = (HttpsURLConnection) new URL(OTP_SCRIPT_COUNTER_RESPONSE_URL).openConnection(this.proxy);
            con.setSSLSocketFactory(sslSocketFactory);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/plain");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", String.valueOf(rscr.toString().getBytes(StandardCharsets.UTF_8).length));
            con.setRequestProperty("Cookie", cookie);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
                out.writeBytes(rscr.toString());
                out.flush();
            }
            
            if (HttpsURLConnection.HTTP_OK == con.getResponseCode() || errorExpected) {
                otpDialogCtl.updateState();
                return;
            }
            
            warnCitizen(new POReIDException("Não foi possivel continuar o processo de alteração do pin OTP"));
        } catch (IOException ex) {
            warnCitizen(new POReIDException("Não foi possivel continuar o processo de alteração do pin OTP", ex));
        }
    }
    
    
    private PinPafUpdate getOTPParameters(byte[] p) throws POReIDException {
        byte[] retbuf;
        PinPafUpdate ppu = new PinPafUpdate();
        
        ppu.setPin(new String(p, StandardCharsets.UTF_8));
        ppu.setCdol1(card.getCardSpecificReferences().getCDOL1());
        ppu.setCounter(COUNTER);
        
        oTPTransmit(GET_PROCESSING_OPTIONS);
        retbuf = oTPTransmit(GET_DATA__PIN_TRY_COUNTER).getBytes();
        ppu.setPinTryCounter(retbuf[3]);
        
        retbuf = oTPTransmit(READ_RECORD).getBytes();
        ppu.setPan(Util.extractFromASN1(retbuf, 21, 8));
        ppu.setPanSeqNumber(String.format("%02x", retbuf[32]));

        retbuf = oTPTransmit(generateAC(AAC, Util.hexToBytes(card.getCardSpecificReferences().getCDOL1()))).getBytes();     
        ppu.setArqc(Util.extractFromASN1(retbuf, 14, 8));
        ppu.setAtc(Util.extractFromASN1(retbuf, 9, 2));
       
        return ppu;
    }
    
    
    private OnlineTransactionParameters getOTPOnlineTransactionParameters(byte[] p) throws POReIDException {
        byte[] retbuf;
        byte[] otpVerifyPin = new CommandAPDU(0x00, 0x20, 0x00, EMV_PLAIN_TEXT_PIN_PROP, createOTPPinBlock(p)).getBytes();
        OnlineTransactionParameters otp = new OnlineTransactionParameters();
        
        oTPTransmit(Util.hexToBytes(card.getCardSpecificReferences().getEmvAID()));
        
        otp.setCdol1(card.getCardSpecificReferences().getCDOL1());
        otp.setCounter(COUNTER);
        
        retbuf = oTPTransmit(READ_RECORD).getBytes();
        otp.setPan(Util.extractFromASN1(retbuf, 21, 8));
        otp.setPanSeqNumber(String.format("%02x", retbuf[32]));
        
        oTPTransmit(GET_PROCESSING_OPTIONS);
        oTPTransmit(GET_DATA__PIN_TRY_COUNTER).getBytes();
        
        oTPTransmit(otpVerifyPin);
        
        retbuf = oTPTransmit(generateAC(ARQC, Util.hexToBytes(card.getCardSpecificReferences().getCDOL1()))).getBytes();
        otp.setArqc(Util.extractFromASN1(retbuf, 14, 8));
        otp.setAtc(Util.extractFromASN1(retbuf, 9, 2));
        
        return otp;
    }
    
    
    private byte[] createOTPPinBlock(byte[] p){
        byte[] bcd = new byte[8];
        
        Arrays.fill(bcd, (byte)0xff);
        bcd[0] = (byte) (0x02 << 4 | (byte)(p.length));
        for (int i=0; i<p.length/2+p.length%2; i++){
            bcd[i+1] = (byte)((p[i*2]-0x30)<< 4 | (i*2+1 < p.length ? (p[i*2+1]-0x30) : 0x0f));
        }
        
        return bcd;
    }
    
    
    private byte[] generateAC(byte referenceControlParameter, byte[] cdol) {
        byte[] apdu = null;
        switch(referenceControlParameter){
            case ARQC:
            case AAC:
                apdu = new byte[40];
                apdu[0] = (byte) 0x80;                  // CLA
                apdu[1] = (byte) 0xAE;                  // INS
                apdu[2] = referenceControlParameter;    // P1
                apdu[3] = 0x00;                         // P2
                apdu[4] = (byte)(cdol.length + 6);
                System.arraycopy(cdol, 0,apdu, 5, cdol.length);
                apdu[34] = 0x34;
                apdu[35] = 0x00;
                apdu[36] = 0x00;
                apdu[37] = 0x01;
                apdu[38] = 0x00;
                apdu[39] = 0x01;                                               
                break;
            case TC:
                apdu = new byte[cdol.length+5];
                apdu[0] = (byte) 0x80;                  // CLA
                apdu[1] = (byte) 0xAE;                  // INS
                apdu[2] = referenceControlParameter;    // P1
                apdu[3] = 0x00;                         // P2
                apdu[4] = (byte) cdol.length;
                System.arraycopy(cdol, 0,apdu, 5, cdol.length);
                break;
        }
        
        return apdu;
    }
    
    private ResponseAPDU oTPTransmitIgnoreErrors(byte[] apdu) throws POReIDException{
        ResponseAPDU response = transmit(apdu);
        
        switch(response.getSW()){
            case 0x9000:
                return response;
            case 0x6A80:
            case 0x6A86: 
                errorExpected = true;
                return response;
            default:
                warnCitizen(new POReIDException("Não foi possível modificar o" + pin.getLabel() + ". Código de estado: " + response.getSW()));
        }
        return null;
    }
    
    
    private ResponseAPDU oTPTransmit(byte[] apdu) throws POReIDException{
        ResponseAPDU response = transmit(apdu);
        
        if (0x9000 != response.getSW()) {
                warnCitizen(new POReIDException("Não foi possível modificar o" + pin.getLabel() + ". Código de estado: " + response.getSW()));
        }
        
        return response;
    }
    
    
    private ResponseAPDU transmit(byte[] apdu) throws POReIDException{
        try {
            return card.getCardSpecificReferences().getCard().getBasicChannel().transmit(new CommandAPDU(apdu));
        } catch (CardException ex) {
            warnCitizen(new POReIDException("Não foi possível modificar o" + pin.getLabel() + ". Código de estado: ",ex));
        }
        
        return null;
    }
    
    
    private void warnCitizen(POReIDException ex) throws POReIDException{
        otpDialogCtl.closeDialog();
        
        DialogController.getInstance(MessageFormat.format(bundle.getString("dialog.otp.error.title"), pin.getLabel()), MessageFormat.format(bundle.getString("dialog.otp.error.message"), 
                pin.getLabel()), card.getCardSpecificReferences().getLocale(), true).displayDialog();
        
        throw ex;
    }
    
    private static class CanContinue__ implements CanContinue {

        @Override
        public boolean proceed() {
            return Thread.currentThread().getStackTrace()[3].getClassName().equalsIgnoreCase(CCConfig.AUTHORIZED_INVOCATION);
        }
    }
}
