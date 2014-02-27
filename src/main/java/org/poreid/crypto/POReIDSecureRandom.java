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

package org.poreid.crypto;

import java.security.SecureRandomSpi;
import javax.smartcardio.CardNotPresentException;
import org.poreid.CardFactory;
import org.poreid.CardTerminalNotPresentException;
import org.poreid.POReIDException;
import org.poreid.POReIDSmartCard;
import org.poreid.UnknownCardException;
import org.poreid.dialogs.selectcard.CanceledSelectionException;


public class POReIDSecureRandom extends SecureRandomSpi {
    private POReIDSmartCard eIDCard;

    @Override
    protected void engineSetSeed(final byte[] seed) {/* efetuado no cartão */}

    @Override
    protected void engineNextBytes(final byte[] bytes) {
        generateBytes(0, bytes);
    }

    @Override
    protected byte[] engineGenerateSeed(final int numBytes) {
        return generateBytes(numBytes, null);
    }
    
    private byte[] generateBytes(int numBytes, byte[] bytes){
        eIDCard = getPOReIDCard();
        byte[] temp = null;
        byte[] random;
        int length;
        
        if (null != bytes){
            length = bytes.length;
        } else {
            length = numBytes;
            temp = new byte[length];
        }
        
        try {
            int i = length / 8;
            int j = length % 8;
            for (int k = 0; k < i; k++) {
                random = eIDCard.getChallenge();  
                System.arraycopy(random, 0, (null != bytes) ? bytes : temp, k * 8, length < 8 ? length : 8);
            }
            if (j > 0) {
                random = eIDCard.getChallenge();
                System.arraycopy(random, 0, (null != bytes) ? bytes : temp, i * 8, j); 
            }
            
            return (null != bytes) ? bytes : temp;
        } catch (POReIDException ex) {
            throw new RuntimeException("Não foi possivel gerar bloco de bytes", ex);
        }
    }

    private POReIDSmartCard getPOReIDCard() {
        if (null != this.eIDCard) {
            return this.eIDCard;
        }
        try {
            this.eIDCard = CardFactory.getCard();
        } catch (POReIDException | CardTerminalNotPresentException | UnknownCardException | CardNotPresentException | CanceledSelectionException ex) {
            throw new SecurityException("Erro verifique cartão e/ou leitor");
        }
        
        return this.eIDCard;
    }
}
