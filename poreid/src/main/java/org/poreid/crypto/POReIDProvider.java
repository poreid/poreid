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

package org.poreid.crypto;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import org.poreid.config.POReIDConfig;

public class POReIDProvider extends Provider {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "POReID";
    private static final String INFO = "Open Source JCA Provider - Cartão de Cidadão";
    private static final double VERSION = 0.1d;

    
    public POReIDProvider() {
        super(NAME, VERSION, INFO);
        
          AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                if (!(System.getSecurityManager() instanceof POReIDSecurityManager)) {
                    System.setSecurityManager(new POReIDSecurityManager(System.getSecurityManager()));
                }
                return null;
            }
        });
        
        final Map<String, String> signatureServiceAttributes = new HashMap<>();
        signatureServiceAttributes.put(POReIDConfig.SUPPORTED_KEY_CLASSES,POReIDPrivateKey.class.getName());
        
        putService(new POReIDService(this, POReIDConfig.KEYSTORE, POReIDConfig.POREID, POReIDKeyStore.class.getName()));
        putService(new POReIDService(this, POReIDConfig.DIGITAL_SIGNATURE, "SHA1withRSA",POReIDSignature.class.getName(), signatureServiceAttributes));
        putService(new POReIDService(this, POReIDConfig.DIGITAL_SIGNATURE, "SHA256withRSA",POReIDSignature.class.getName(), signatureServiceAttributes)); 
        putService(new POReIDService(this, POReIDConfig.DIGITAL_SIGNATURE, "NONEwithRSA",POReIDSignature.class.getName(), signatureServiceAttributes));
	putService(new POReIDService(this, POReIDConfig.KEY_MANAGER_FACTORY, POReIDConfig.POREID, POReIDKeyManagerFactory.class.getName()));
    }
}
