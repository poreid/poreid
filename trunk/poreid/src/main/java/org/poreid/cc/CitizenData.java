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

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.poreid.POReIDException;
import org.poreid.SmartCardFileException;
import org.poreid.dialogs.pindialogs.PinBlockedException;
import org.poreid.dialogs.pindialogs.PinEntryCancelledException;
import org.poreid.dialogs.pindialogs.PinTimeoutException;

/**
 * Especifica as funcionalidades a fornecer com o cartão de cidadão
 * @author POReID
 */
public interface CitizenData {

    /**
     * Retorna os dados do cidadão
     * @return dados do cidadão
     * @throws SmartCardFileException Exceção lançada quando ocorre um erro durante uma operação sobre um ficheiro existente no cartão
     */
    CitizenCardIdAttributes getID() throws SmartCardFileException;

    /**
     * Retorna o conteúdo do ficheiro da morada
     * @return morada
     * @throws org.poreid.dialogs.pindialogs.PinTimeoutException Exceção lançada quando o PIN não é introduzido dentro do tempo especificado (30 segundos)
     * @throws org.poreid.dialogs.pindialogs.PinEntryCancelledException Exceção lançada quando a introdução do PIN é cancelada
     * @throws org.poreid.dialogs.pindialogs.PinBlockedException Exceção lançada quando o PIN está bloqueado
     * @throws org.poreid.POReIDException Exceção genérica do POReID, tipicamente encapsula outra exceção
     */
    CitizenCardAddressAttributes getAddress() throws PinTimeoutException, PinEntryCancelledException, PinBlockedException, POReIDException;

    /**
     * Retorna o conteúdo do ficheiro das notas pessoais
     * @return conteúdo do ficheiro das notas pessoais
     * @throws SmartCardFileException Exceção lançada quando ocorre um erro durante uma operação sobre um ficheiro existente no cartão
     */
    byte[] readPersonalNotes() throws SmartCardFileException;

    /**
     * Retorna metadados e foto no formato jpeg2000
     * @return metadados e foto no formato jpeg2000
     * @throws SmartCardFileException Exceção lançada quando ocorre um erro durante uma operação sobre um ficheiro existente no cartão
     */
    CitizenCardPhotoAttributes getPhotoData() throws SmartCardFileException;

    /**
     * Retorna o conteúdo do ficheiro SOD
     * @return conteúdo do ficheiro SOD
     * @throws SmartCardFileException Exceção lançada quando ocorre um erro durante uma operação sobre um ficheiro existente no cartão
     */
    byte[] getSOD() throws SmartCardFileException;

    /**
     * Grava as notas pessoais no cartão. Este método deve ser invocado com as notas pessoais completas e não apenas o excerto modificado.
     * @param notes notas pessoais do cidadão para gravar no cartão
     * @throws SmartCardFileException Exceção lançada quando ocorre um erro durante uma operação sobre um ficheiro existente no cartão
     * @throws org.poreid.dialogs.pindialogs.PinTimeoutException Exceção genérica do POReID, tipicamente encapsula outra exceção
     * @throws org.poreid.POReIDException Exceção genérica do POReID, tipicamente encapsula outra exceção
     * @throws org.poreid.dialogs.pindialogs.PinEntryCancelledException Exceção lançada quando a introdução do PIN é cancelada
     * @throws org.poreid.dialogs.pindialogs.PinBlockedException Exceção lançada quando o PIN está bloqueado
     */
    void savePersonalNotes(String notes) throws SmartCardFileException, PinTimeoutException, POReIDException, PinEntryCancelledException, PinBlockedException;
    
    
    /**
     * Retorna a chave pública existente no cartão
     * @return chave pública
     * @throws SmartCardFileException Exceção lançada quando ocorre um erro durante uma operação sobre um ficheiro existente no cartão
     * @throws InvalidKeySpecException Exception for invalid key specifications
     * @throws NoSuchAlgorithmException Exceção lançada quando é solicitado um algoritmo criptográfico que não está disponível no sistema
     */
    PublicKey getPublicKey() throws SmartCardFileException, InvalidKeySpecException, NoSuchAlgorithmException;

}
