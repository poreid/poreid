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

import java.security.cert.X509Certificate;
import java.util.List;
import org.poreid.dialogs.pindialogs.PinBlockedException;
import org.poreid.dialogs.pindialogs.PinEntryCancelledException;
import org.poreid.dialogs.pindialogs.PinTimeoutException;

/**
 *
 * @author POReID
 */
public interface POReIDSmartCard extends SmartCardUIData, AutoCloseable {

    /**
     * Produz uma assinatura digital obedecendo aos parametros fornecidos
     * @param hash resumo criptográfico
     * @param pinCode este parametro estará preenchido se o código PIN for recolhido externamente (este parametro é desabilitado via ficheiro de configuração)
     * @param digestAlgo Algoritmo de resumo
     * @param pkAlias Alias da chave privada a utilizar
     * @param sch Tipo de padding a utilizar na assinatura
     * @return assinatura digital
     * @throws PinTimeoutException Exceção lançada quando o pin não é introduzido no intervalo de tempo designado (tipicamente 30 segundos)
     * @throws PinEntryCancelledException Exceção lançada quando o utilizador cancela a introdução do pin
     * @throws PinBlockedException Exceção lançada quando é detetado o bloqueio do pin
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    byte[] sign(byte hash[], byte[] pinCode, String digestAlgo, PkAlias pkAlias, RSAPaddingSchemes... sch) throws PinTimeoutException, PinEntryCancelledException, PinBlockedException, POReIDException;

    /**
     * Verificação de PIN
     * @param pin identificação do pin a verificar (pin de autenticação, assinatura,..)
     * @param pinCode este parametro estará preenchido se o código PIN for recolhido externamente (este parametro é desabilitado via ficheiro de configuração)
     * @return true se o PIN for válido false se o PIN for inválido.
     * @throws PinTimeoutException Exceção lançada quando o pin não é introduzido no intervalo de tempo designado (tipicamente 30 segundos)
     * @throws PinEntryCancelledException Exceção lançada quando o utilizador cancela a introdução do pin
     * @throws PinBlockedException Exceção lançada quando é detetado o bloqueio do pin
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    boolean verifyPin(Pin pin, byte[] pinCode) throws PinTimeoutException, PinEntryCancelledException, PinBlockedException, POReIDException;

    /**
     * Modificação de PIN
     * @param pin identificação do pin a modificar (pin de autenticação, assinatura,..)
     * @throws PinBlockedException Exceção lançada quando é detetado o bloqueio do pin
     * @throws PinEntryCancelledException Exceção lançada quando o utilizador cancela a introdução do pin
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    void ModifyPin(Pin pin) throws PinBlockedException, PinEntryCancelledException, POReIDException;

    /**
     * Gera um conjuto de 8 bytes aleatórios
     * @return 8 bytes aleatórios.
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    byte[] getChallenge() throws POReIDException;

    /**
     * Retorna o número de tentativas até bloquear o PIN.
     * @param pin identificação do pin (pin de autenticação, assinatura,..)
     * @return número de tentativas até bloquear o PIN.
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    int getPinStatus(Pin pin) throws POReIDException;

    /**
     * Retorna o certificado contido no ficheiro
     * @param file identificação do ficheiro que contem o certificado
     * @return certificado na forma de objecto X509Certificate 
     * @throws CertificateNotFound Exceção lançada quando não é encontrado o certificado pretendido
     */
    X509Certificate getCertificate(SmartCardFile file) throws CertificateNotFound;

    /**
     * Retorna o certificado de autenticação
     * @return certificado na forma de objecto X509Certificate 
     * @throws CertificateNotFound Exceção lançada quando não é encontrado o certificado pretendido
     */
    X509Certificate getAuthenticationCertificate() throws CertificateNotFound;

    /**
     * Retorna o certificado de assinatura qualificada
     * @return certificado na forma de objecto X509Certificate 
     * @throws CertificateNotFound Exceção lançada quando não é encontrado o certificado pretendido
     */
    X509Certificate getQualifiedSignatureCertificate() throws CertificateNotFound;

    /**
     * Retorna o caminho completo entre o certificado de assinatura qualificada e o seu certificado raíz 
     * @return lista de certificados ordenados do certificado de assinatura qualificada para o certificado raíz.
     * @throws CertificateChainNotFound Exceção lançada quando não é possivel construir um caminho de certificação para o certificado
     */
    List<X509Certificate> getQualifiedSignatureCertificateChain() throws CertificateChainNotFound;

    /**
     * Retorna o caminho completo entre o certificado de autenticação e o seu certificado raíz
     * @return lista de certificados ordenados do certificado de autenticação para o certificado raíz.
     * @throws CertificateChainNotFound Exceção lançada quando não é possivel construir um caminho de certificação para o certificado
     */
    List<X509Certificate> getAuthenticationCertificateChain() throws CertificateChainNotFound;

    /**
     * Retorna o caminho completo entre o certificado contido no ficheiro e o seu certificado raíz
     * @param file identificação do ficheiro que contem o certificado
     * @return lista de certificados ordenados do certificado contido no ficheiro para o certificado raíz.
     * @throws CertificateChainNotFound Exceção lançada quando não é possivel construir um caminho de certificação para o certificado
     */
    List<X509Certificate> getCertificateChain(SmartCardFile file) throws CertificateChainNotFound;
    
    /**
     * Verifica se o cartão está presente no leitor
     * @return true se o cartão estiver no leitor
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    boolean isPOReIDSmartcardPresent() throws POReIDException;
    
    /**
     * Termina a ligação com o cartão
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    @Override
    void close() throws POReIDException;
}
