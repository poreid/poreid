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
package org.poreid.macosx.smartcardio;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactorySpi;

/**
 * Provider object for PC/SC.
 *
 * @author  POReID based on Andreas Sterbenz implementation
 */
public final class MacOSXCustomPCSCProvider extends Provider {

    public MacOSXCustomPCSCProvider() {
        super("MacOSXCustomPCSC", 0.1d, "Mac OSX Custom PC/SC provider");
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                put("TerminalFactory.MacOSXCustomPCSC", "org.poreid.macosx.smartcardio.MacOSXCustomPCSCProvider$Factory");
                return null;
            }
        });
    }

    
    public static final class Factory extends TerminalFactorySpi {

        public Factory(Object obj) throws PCSCException {
            if (obj != null) {
                throw new IllegalArgumentException("MacOSXCustomPCSCProvider factory does not use parameters");
            }

            PCSC.checkAvailable();
            PCSCTerminals.initContext();
        }

        @Override
        protected CardTerminals engineTerminals() {
            return new PCSCTerminals();
        }
    }
}
