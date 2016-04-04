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

package org.poreid;

/**
 * Exceção lançada quando não é possivel obter um certificado existente no cartão
 * @author POReID
 */
public class CertificateNotFound extends Exception{
    
    /**
     * Constroi uma nova exceção com a mensagem de detalhe
     * @param detalhe A mensagem de detalhe relativa à exceção
     */
    public CertificateNotFound(String detalhe) {
        super(detalhe);
    }

    /**
     * Constroi uma nova exceção com a mensagem de detalhe e a causa
     * @param detalhe A mensagem de detalhe relativa à exceção
     * @param causa A causa da exceção
     */
    public CertificateNotFound(String detalhe, Exception causa) {
        super(detalhe, causa);
    }
}
