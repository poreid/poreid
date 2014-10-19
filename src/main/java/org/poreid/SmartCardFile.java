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
package org.poreid;

/**
 *
 * @author POReID
 */
public interface SmartCardFile {

    /**
     * Obtem identificador do ficheiro
     * @return identificador do ficheiro
     */
    String getFileId();

    /**
     * Indica se o ficheiro pode fazer parte da cache
     * @return true se pode ser lido/escrito para cache, false se não pode. 
     */
    boolean isCacheable();

    /**
     * Indica o deslocamento onde a leitura se deve iniciar
     * @return deslocamento onde a leitura se deve iniciar.
     */
    int getOffset();

    /**
     * Indica o número de bytes existentes no ficheiro (total ou relativo a deslocamento)
     * @return número de bytes
     */
    int getLenght();

    /**
     * Método utilizado em ficheiros que contêm um conjunto de dados heterogénico, serve para separar os dados na cache.
     * @return sufixo de cada elemento do conjunto de dados
     */
    String getSuffix();

    /**
     * Indica se o ficheiro pode ser atualizado (geralmente apenas aplicável a ficheiros tipo SOD)
     * @return true se poder ser atualizado, false se não pode.
     */
    boolean isUpdateable();

    /**
     * Retorna o tamanho em bytes da zona que deve ser verificada relativamente a alterações (aplicável a ficheiros atualizáveis tipo SOD)
     * @return tamanho em bytes
     */
    int getDiffLenght();

    /**
     * Retorna o deslocamento em bytes da zona que deve ser verificada relativamente a alterações (aplicável a ficheiros atualizáveis tipo SOD)
     * @return deslocamento em bytes
     */
    int getDiffOffset();

    /**
     * Identifica o PIN necessário para ler/escrever no ficheiro
     * @return PIN a fornecer ao verifyPIN
     */
    Pin getPin();

    /**
     * Retorna o tamanho máximo do ficheiro onde é permitida a escrita (este limite é definido na aplicação)
     * @return tamanho máximo em bytes do ficheiro
     */
    int getMaximumSize();
}
