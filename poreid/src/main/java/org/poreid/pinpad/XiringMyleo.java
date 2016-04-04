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

import java.io.IOException;

/**
 * Gera estruturas para verificação / modificação de PIN para o leitor Xiring MyLeo
 * @author POReID
 */
public class XiringMyleo implements ReaderWithPinPad{
    private static final int ioCtlSmartcardLcdMessages = 0;
    
    
    @Override
    public ReaderWithPinPadData getVerifyPinDirect(byte timeOut, byte minPinSize, byte maxPinSize, byte[] apdu) throws IOException {
        return new ReaderWithPinPadData(new VerifyPinDirectStruct().
                setbTimeOut((byte) timeOut).
                setbTimeOut2((byte) 0).
                setBmFormatString((byte) 0x82).
                setBmPINBlockString((byte) 0).
                setBmPINLengthFormat((byte) 0).setwPINMaxExtraDigit(new byte[]{(byte) maxPinSize, (byte) minPinSize}).
                setbEntryValidationCondition((byte) 0x02).
                setbNumberMessage((byte) 0x01).
                setwLangId(new byte[]{0x16, 0x08}).
                setbTeoPrologue(new byte[]{(byte) 0, (byte) 0, (byte) 0}).
                setAbData(apdu).getVerifyPinDirectStruct(),
                null, ioCtlSmartcardLcdMessages);
    }

    
    @Override
    public ReaderWithPinPadData getModifyPinDirect(boolean verifyToModify, byte timeOut, byte minPinSize, byte maxPinSize, byte[] apdu) throws IOException {
        return new ReaderWithPinPadData(new ModifyPinDirectStruct().
                setbTimeOut((byte)timeOut).
                setbTimeOut2((byte)timeOut).
                setBmFormatString((byte)(0x02)).
                setBmPINBlockString((byte)0x00).
                setBmPINLengthFormat((byte)0x00).
                setbInsertionOffsetOld((byte)0x00).
                setbInsertionOffsetNew((byte)(verifyToModify ? 0x00 : maxPinSize)).
                setwPINMaxExtraDigit(new byte[]{(byte)maxPinSize, (byte)minPinSize}).
                setbConfirmPIN((byte)(verifyToModify ? 0x01 : 0x03)).
                setbEntryValidationCondition((byte)0x02).
                setbNumberMessage((byte)(verifyToModify ? 0x02 : 0x03)).
                setwLangId(new byte[]{0x16,0x08}).
                setbMessageIndex(new byte[]{(byte)0x00, (byte)0x00, (byte)0x00}).
                setbTeoPrologue(new byte[]{(byte)0x00, (byte)0x00, (byte)0x00}).
                setAbData(apdu).getModifyPinDirectStruct(),
                null,ioCtlSmartcardLcdMessages);
    }
}
