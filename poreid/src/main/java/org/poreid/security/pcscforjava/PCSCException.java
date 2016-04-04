/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

import org.poreid.pcscforjava.PCSCErrorValues;

/**
 * Exception for PC/SC errors. The native code portion checks the return value
 * of the SCard* functions. If it indicates an error, the native code constructs
 * an instance of this exception, throws it, and returns to Java.
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  Matthieu Leromain
 */
final class PCSCException extends Exception {

    private static final long serialVersionUID = 4181137171979130432L;
    
    /**
     * The last PCSC exception integer code which occured.
     */
    final int code;
    
    /**
     * Constructs a PCSCException object.
     * @param code the PCSC exception integer code.
     */
    PCSCException(int code) {
        super(PCSCErrorValues.toErrorString(code));
        this.code = code;
    }
}
