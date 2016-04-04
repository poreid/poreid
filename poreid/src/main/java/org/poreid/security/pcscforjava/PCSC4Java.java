/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.poreid.security.pcscforjava;

import org.poreid.pcscforjava.TerminalFactorySpi;
import org.poreid.pcscforjava.CardTerminals;
import org.poreid.pcscforjava.PCSCResource;
import java.security.*;


/**
 * Provider object for PC/SC.
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  Matthieu Leromain
 */
public final class PCSC4Java extends Provider {

    private static final long serialVersionUID = 6168388284028876579L;
    
    /**
     * Constructor of PCSC4Java terminal factory.
     */
    public PCSC4Java() {
        super("PCSC4Java", PCSCResource.getDecVersion(), 
                "PC/SC 4 Java provider");
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                put("TerminalFactory.PC/SC",
                    "org.poreid.security.pcscforjava.PCSC4Java$Factory");
                return null;
            }
        });
    }
    
    /**
     * Real factory class.
     */
    public static final class Factory extends TerminalFactorySpi {
        /**
         * Real factory constructor.
         * @param obj unused parameter. Must be null.
         * @throws PCSCException if a PC/SC exception occurs.
         */
        public Factory(Object obj) throws PCSCException {
            if (obj != null) {
                throw new IllegalArgumentException
                    ("PCSC 4 Java factory does not use parameters");
            }
            // Make sure PCSC is available and that we can obtain a context
            PCSC.checkAvailable();
            PCSCTerminals.initContext();
        }
        /**
         * Returns the available readers.
         * This must be a new object for each call.
         */
        protected CardTerminals engineTerminals() {
            return new PCSCTerminals();
        }
        
        /**
         * Destroy the available terminals.
         * @return true if the deletion is OK false otherwise.
         */
        @Override
        protected boolean destroyTerminals() {
            try 
            {
                PCSCTerminals.releaseContext();
            } 
            catch (PCSCException ex) 
            {
                return false;
            }
            
            return true;
        }
    }

}
