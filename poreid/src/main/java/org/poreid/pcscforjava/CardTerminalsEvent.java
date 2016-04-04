/*
 * Copyright (c) 2012, Redbilled.fr. All rights reserved.
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

package org.poreid.pcscforjava;

import java.util.List;

/**
 * {@link CardTerminalsEvent CardTerminalsEvent} is an object use to manage
 * the various insertion / removal of a {@link CardTerminal CardTerminal} object
 * in the {@link CardTerminals CardTerminals} list.<br /><br />
 * 
 * This object can be seen as a Plug & Play callback. The management of these
 * Plug & Play callbacks is performed directly by the {@link TerminalFactory 
 * TerminalFactory}.
 * 
 * 
 * @see TerminalFactory
 * @see CardTerminals
 *
 * @since   1.6
 * @author  Matthieu Leromain
 */
public interface CardTerminalsEvent {

    /**
     * Updates the card terminals list when an event of type card terminal
     * insertion / removal occurs.
     *
     * <p>This method is called as a callback by subclasses only. Application
     * should call {@linkplain TerminalFactory#setPnPCallback} which sets the
     * callback for the PnP notification.</p>
     * 
     * @param cardTerminals the current list of {@link CardTerminal 
     * CardTerminal}.
     * 
     * @throws CardException if a card operation failed.
     */
    public abstract void updateCardTerminalsListByEvent
            (List<CardTerminal> cardTerminals) throws CardException;
}