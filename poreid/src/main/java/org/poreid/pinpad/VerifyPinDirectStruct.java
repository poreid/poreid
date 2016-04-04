/*
 * The MIT License
 *
 * Copyright 2014, 2015, 2016 Rui Martinho (rmartinho@gmail.com), António Braz (antoniocbraz@gmail.com)
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
package org.poreid.pinpad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author POReID
 */
class VerifyPinDirectStruct {
    private byte bTimeOut;
    private byte bTimeOut2;
    private byte bmFormatString;
    private byte bmPINBlockString;
    private byte bmPINLengthFormat;
    private byte[] wPINMaxExtraDigit;
    private byte bEntryValidationCondition;
    private byte bNumberMessage;
    private byte[] wLangId;
    private byte bMessageIndex;
    private byte[] bTeoPrologue;
    private final byte[] dataLength = new byte[] {0,0,0,0};
    private byte[] abData;
    

    protected VerifyPinDirectStruct setbTimeOut(byte bTimeOut) {
        this.bTimeOut = bTimeOut;
        return this;
    }

    protected VerifyPinDirectStruct setbTimeOut2(byte bTimeOut2) {
        this.bTimeOut2 = bTimeOut2;
        return this;
    }

    protected VerifyPinDirectStruct setBmFormatString(byte bmFormatString) {
        this.bmFormatString = bmFormatString;
        return this;
    }

    protected VerifyPinDirectStruct setBmPINBlockString(byte bmPINBlockString) {
        this.bmPINBlockString = bmPINBlockString;
        return this;
    }

    protected VerifyPinDirectStruct setBmPINLengthFormat(byte bmPINLengthFormat) {
        this.bmPINLengthFormat = bmPINLengthFormat;
        return this;
    }

    protected VerifyPinDirectStruct setwPINMaxExtraDigit(byte[] wPINMaxExtraDigit) {
        this.wPINMaxExtraDigit = wPINMaxExtraDigit;
        return this;
    }

    protected VerifyPinDirectStruct setbEntryValidationCondition(byte bEntryValidationCondition) {
        this.bEntryValidationCondition = bEntryValidationCondition;
        return this;
    }

    protected VerifyPinDirectStruct setbNumberMessage(byte bNumberMessage) {
        this.bNumberMessage = bNumberMessage;
        return this;
    }

    protected VerifyPinDirectStruct setwLangId(byte[] wLangId) {
        this.wLangId = wLangId;
        return this;
    }

    protected VerifyPinDirectStruct setbMessageIndex(byte bMessageIndex) {
        this.bMessageIndex = bMessageIndex;
        return this;
    }

    protected VerifyPinDirectStruct setbTeoPrologue(byte[] bTeoPrologue) {
        this.bTeoPrologue = bTeoPrologue;
        return this;
    }

    protected VerifyPinDirectStruct setAbData(byte[] abData) {
        this.abData = abData;
        dataLength[0] = (byte)(abData.length & 0xff);
        return this;
    }
    
    protected byte[] getVerifyPinDirectStruct() throws IOException {
        ByteArrayOutputStream fvpd = new ByteArrayOutputStream();

        fvpd.write(bTimeOut);                   // bTimeOut
        fvpd.write(bTimeOut2);                  // bTimeOut2 -- 0 when not supported by some readers.
        fvpd.write(bmFormatString);             // bmFormatString -- page 12 - http://www.idvation.com/uploads/media/REF_ACR83_v1.4.0.5_11.pdf
        fvpd.write(bmPINBlockString);           // bmPINBlockString -- apdu doesnt have pin length, only pin block lenght
        fvpd.write(bmPINLengthFormat);          // bmPINLengthFormat -- no pin length => no pin length format
        fvpd.write(wPINMaxExtraDigit);          // wPINMaxExtraDigit -- maximum PIN size in digit
        fvpd.write(bEntryValidationCondition);  // bEntryValidationCondition -- 0x02 validation key pressed
        fvpd.write(bNumberMessage);             // bNumberMessage -- Enter PIN
        fvpd.write(wLangId);                    // wLangId -- english
        fvpd.write(bMessageIndex);              // bMsgIndex -- should be 0
        fvpd.write(bTeoPrologue);               // bTeoPrologue
        fvpd.write(dataLength);                 // ulDataLength[0]
        fvpd.write(abData);                     // apdu
        
        return fvpd.toByteArray();
    } 
}
