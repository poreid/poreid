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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore.Entry;
import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.smartcardio.CardNotPresentException;
import org.poreid.CardFactory;
import org.poreid.CardTerminalNotPresentException;
import org.poreid.CertificateChainNotFound;
import org.poreid.CertificateNotFound;
import org.poreid.POReIDException;
import org.poreid.POReIDSmartCard;
import org.poreid.PkAlias;
import org.poreid.UnknownCardException;
import org.poreid.config.POReIDConfig;
import org.poreid.dialogs.selectcard.CanceledSelectionException;


public class POReIDKeyStore extends KeyStoreSpi {
    private Locale locale = null;
    private POReIDSmartCard poreidCard;
    private POReIDKeyStoreParameter keyStoreParameter;
    private POReIDsslKeyStoreParameter sslStoreParameter;
    private List<X509Certificate> cachedAuthenticationCertificateChain;
    private List<X509Certificate> cachedQualifiedSignatureCertificateChain;
    private X509Certificate cachedAuthenticationCertificate;
    private X509Certificate cachedQualifiedSignatureCertificate;
    private static final Map<String, PkAlias> POREID_ALIASES = new HashMap<>(2);
    
    static {
        POREID_ALIASES.put(POReIDConfig.AUTENTICACAO, PkAlias.AUTENTICACAO);
        POREID_ALIASES.put(POReIDConfig.ASSINATURA, PkAlias.ASSINATURA);
    }

    
    @Override
    public Key engineGetKey(final String alias, final char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {        
        poreidCard = getEidCard();
        boolean ssl;
       
        if (null == this.keyStoreParameter) {
            locale = null;
        } else {
            locale = this.keyStoreParameter.getLocale();
        }
        
        ssl = (null != this.sslStoreParameter); 
            
        if (POREID_ALIASES.containsKey(alias)){
            byte[] pin = null;
            if (null != password && POReIDConfig.isExternalPinCachePermitted() || ssl){
                pin = !ssl ? StandardCharsets.UTF_8.encode(CharBuffer.wrap(password)).array() : sslStoreParameter.getP();
            }
            return new POReIDPrivateKey(poreidCard, POREID_ALIASES.get(alias),pin,ssl);
        }
        
        return null;   
    }

    
    @Override
    public Certificate[] engineGetCertificateChain(final String alias) {
        poreidCard = getEidCard();

        switch (alias) {
            case POReIDConfig.ASSINATURA:
                try {
                    if (null == cachedQualifiedSignatureCertificateChain) {
                        cachedQualifiedSignatureCertificateChain = poreidCard.getQualifiedSignatureCertificateChain();
                    }
                    return cachedQualifiedSignatureCertificateChain.toArray(new X509Certificate[]{});
                } catch (final CertificateChainNotFound ignore) { }
                break;
            case POReIDConfig.AUTENTICACAO:
                try {
                    if (null == cachedAuthenticationCertificateChain) {
                        cachedAuthenticationCertificateChain = poreidCard.getAuthenticationCertificateChain();
                    }
                    return cachedAuthenticationCertificateChain.toArray(new X509Certificate[]{});
                } catch (final CertificateChainNotFound ignore) { }
                break;          
        }
         
        return null;
    }

    
    @Override
    public Certificate engineGetCertificate(final String alias) {
        poreidCard = getEidCard();

        switch (alias) {
            case POReIDConfig.ASSINATURA:
                try {
                    if (null == cachedQualifiedSignatureCertificate) {
                        cachedQualifiedSignatureCertificate = poreidCard.getQualifiedSignatureCertificate();
                    }
                    return cachedQualifiedSignatureCertificate;
                } catch (final  CertificateNotFound ignore) { }
                break;
            case POReIDConfig.AUTENTICACAO:
                try {
                    if (null == cachedAuthenticationCertificate) {
                        cachedAuthenticationCertificate = poreidCard.getAuthenticationCertificate();
                    }
                    return cachedAuthenticationCertificate;
                } catch (final CertificateNotFound ignore) { }
                break;          
        }
         
        return null;   
    }

    
    @Override
    public Date engineGetCreationDate(final String alias) {
        final X509Certificate certificate = (X509Certificate) this.engineGetCertificate(alias);
        return null != certificate ? certificate.getNotBefore() : null;
    }

    
    @Override
    public void engineSetKeyEntry(final String alias, final Key key, final char[] password, final Certificate[] chain) throws KeyStoreException {
        throw new UnsupportedOperationException("Operação não suportada");
    }

    
    @Override
    public void engineSetKeyEntry(final String alias, final byte[] key, final Certificate[] chain) throws KeyStoreException {
        throw new UnsupportedOperationException("Operação não suportada");
    }

    
    @Override
    public void engineSetCertificateEntry(final String alias, final Certificate cert) throws KeyStoreException {
        throw new UnsupportedOperationException("Operação não suportada");
    }

    
    @Override
    public void engineDeleteEntry(final String alias) throws KeyStoreException {
        throw new UnsupportedOperationException("Operação não suportada");
    }

	
    @Override
    public Enumeration<String> engineAliases() {
        return Collections.enumeration(POREID_ALIASES.keySet());
    }

    
    @Override
    public boolean engineContainsAlias(final String alias) {
        return POREID_ALIASES.containsKey(alias);
    }
	

    
    @Override
    public int engineSize() {
        return POREID_ALIASES.size();
    }

    
    @Override
    public boolean engineIsKeyEntry(final String alias) {
        return POREID_ALIASES.containsKey(alias);
    }

    
    @Override
    public boolean engineIsCertificateEntry(final String alias) {
        return POREID_ALIASES.containsKey(alias);
    }

    
    @Override
    public Entry engineGetEntry(String alias, ProtectionParameter protParam) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        if (protParam != null) {
    		Logger.getLogger(this.getClass().getName()+" engineGetEntry()").warning("Parametro ProtectionParameter é ignorado, utilize null");
    	}
        if (engineContainsAlias(alias)) { 
            return new PrivateKeyEntry((PrivateKey) engineGetKey(alias, null), engineGetCertificateChain(alias));
        }
        return null;
    }
    
    
    @Override
    public String engineGetCertificateAlias(final Certificate cert) {
        if (cert instanceof X509Certificate) {
            BigInteger serial = ((X509Certificate) cert).getSerialNumber();
            for (String alias : POREID_ALIASES.keySet()) {
                if (((X509Certificate) engineGetCertificate(alias)).getSerialNumber() == serial) {
                    return alias;
                }
            }
        }
        return null;
    }

    
    @Override
    public void engineStore(final OutputStream stream, final char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new UnsupportedOperationException("Operação não suportada");
    }

    
    @Override
    public void engineLoad(final InputStream stream, final char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
         if (password != null) {
    		Logger.getLogger(this.getClass().getName()+" engineLoad()").warning("Parametro password é ignorado, utilize null");
         }
        
        getEidCard();
    }

    // TODO: REVER ISTO
    @Override 
    public void engineLoad(final LoadStoreParameter param) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (null == param) {
            return;
        }
    
        if (param instanceof POReIDKeyStoreParameter) {
            this.keyStoreParameter = (POReIDKeyStoreParameter) param;
            this.locale = this.keyStoreParameter.getLocale();
            this.poreidCard = this.keyStoreParameter.getCard();
        } else {
            if (param instanceof POReIDsslKeyStoreParameter) {
                this.sslStoreParameter = (POReIDsslKeyStoreParameter) param;
                this.poreidCard = this.sslStoreParameter.getCard();
            }
        }

        getEidCard();
    }

    
    private POReIDSmartCard getEidCard() {
        if (null == this.poreidCard) {
            try {
                this.poreidCard = CardFactory.getCard(locale);
            } catch (    POReIDException | CardTerminalNotPresentException | UnknownCardException | CardNotPresentException | CanceledSelectionException ex) {
                throw new SecurityException("Erro verifique cartão e/ou leitor",ex);
            }

            if (null == this.poreidCard) {
                throw new SecurityException("Erro cartão não encontrado");
            }
        }
        return this.poreidCard;
    }
}
