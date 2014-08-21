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
package org.poreid.pinpad;

import java.io.IOException;

/**
 * Leitores com pinpad implementam esta interface
 * @author POReID
 */
public interface ReaderWithPinPad {

    /**
     * Retorna uma estrutura que será passada ao leitor de cartões com pinpad
     * @param timeOut Tempo limite
     * @param minPinSize Tamanho minimo do pin
     * @param maxPinSize Tamanho máximo do pin
     * @param apdu Instrução a enviar ao cartão
     * @return estrutura que será passada ao leitor de cartões com pinpad
     * @throws IOException Exceção lançada se ocorrer algum problema com instâncias de ByteArrayOutputStream
     */
    ReaderWithPinPadData getVerifyPinDirect(byte timeOut, byte minPinSize, byte maxPinSize, byte[] apdu) throws IOException;

    /**
     * Retorna uma estrutura que será passada ao leitor de cartões com pinpad
     * @param verifyToModify indica se ocorreu uma verificação de pin
     * @param timeOut Tempo limite
     * @param minPinSize Tamanho minimo do pin
     * @param maxPinSize Tamanho máximo do pin
     * @param apdu Instrução a enviar ao cartão
     * @return estrutura que será passada ao leitor de cartões com pinpad
     * @throws IOException Exceção lançada se ocorrer algum problema com instâncias de ByteArrayOutputStream
     */
    ReaderWithPinPadData getModifyPinDirect(boolean verifyToModify, byte timeOut, byte minPinSize, byte maxPinSize, byte[] apdu) throws IOException;
}
