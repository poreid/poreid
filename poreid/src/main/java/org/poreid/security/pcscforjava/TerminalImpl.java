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

import org.poreid.pcscforjava.CardPermission;
import org.poreid.pcscforjava.CardTerminal;
import org.poreid.pcscforjava.PCSCErrorValues;
import org.poreid.pcscforjava.CardNotPresentException;
import org.poreid.pcscforjava.CardException;
import org.poreid.pcscforjava.Card;

import static org.poreid.security.pcscforjava.PCSC.*;
import static org.poreid.security.pcscforjava.PCSCDefines.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CardTerminal implementation.
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  Matthieu Leromain
 */
final class TerminalImpl extends CardTerminal {

    /**
     * Native SCARDCONTEXT
     */
    long contextId;

    /**
     * The name of this terminal (native PC/SC name)
     */
    final String name;
    
    /**
     * The card of the terminal.
     */
    private CardImpl card;

    /**
     * Constructs a new CardTerminal object.
     *
     * <p>This constructor is called by subclasses only. Application should
     * call {@linkplain CardTerminals#list list()}
     * or {@linkplain CardTerminals#getTerminal getTerminal()}
     * to obtain a CardTerminal object.
     */
    TerminalImpl(long contextId, String name) {
        this.contextId = contextId;
        this.name = name;
        this.card = null;
    }
    
    /**
     * Sets the contextId.
     * @param contextId 
     */
    public void setContextId(long contextId)
    {
        this.contextId = contextId;
    }

    /**
     * Returns the unique name of this terminal.
     *
     * @return the unique name of this terminal.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Establishes a connection to the card.<br />
     * If a connection has previously established using
     * the specified protocol, this method returns the same {@link Card Card} 
     * object as the previous call.
     *
     * @param protocol the protocol to use ("T=0", "T=1", or "DIRECT"), or "*" 
     * to connect using any available protocol.
     * 
     * @return Card the instance of Card connection.
     *
     * @throws NullPointerException if protocol is null.
     * @throws IllegalArgumentException if protocol is an invalid protocol
     *   specification.
     * @throws CardNotPresentException if no card is present in this terminal.
     * @throws CardException if a connection could not be established
     *   using the specified protocol or if a connection has previously been
     *   established using a different protocol.
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     */
    public synchronized Card connect(String protocol) throws CardException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new CardPermission(name, "connect"));
        }
        if (card != null) {
            if (card.isValid()) {
                String cardProto = card.getProtocol();
                if (protocol.equals("*") || 
                        protocol.equalsIgnoreCase(cardProto) ||
                        card.getSharingMode() == SCARD_SHARE_DIRECT) {
                    return card;
                } else {
                    throw new CardException("org.poreid.pcscforjava."
                    + "TerminalImpl.connect "
                    + "PCSCException: SCARD_E_PROTO_MISMATCH " 
                            + "Cannot connect using " + protocol
                        + ", connection already established using " 
                            + cardProto);
                }
            } else {
                card = null;
            }
        }
        try {
            card =  new CardImpl(this, protocol);
            return card;
        } catch (PCSCException e) {
            if (e.code == PCSCErrorValues.SCARD_W_REMOVED_CARD) {
                throw new CardNotPresentException("org.poreid.pcscforjava."
                    + "TerminalImpl.connect "
                    + "PCSCException: SCARD_E_NO_SMARTCARD", e);
            }
            else if((e.code == PCSCErrorValues.SCARD_E_INVALID_VALUE) && 
                    (protocol.equalsIgnoreCase("direct")))
            {
                try {
                    card = new CardImpl(this, protocol, 
                        SCARD_PROTOCOL_T0 | SCARD_PROTOCOL_T1);
                    return card;            
                } catch (PCSCException ex) {
                    throw new CardException("org.poreid.pcscforjava."
                    + "TerminalImpl.connect "
                    + "PCSCException: " + ex.getMessage(), ex);
                }
                
            }
            else {
                throw new CardException("org.poreid.pcscforjava."
                    + "TerminalImpl.connect "
                    + "PCSCException: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Returns whether a card is present in this terminal.
     *
     * @return whether a card is present in this terminal.
     *
     * @throws CardException if the status could not be determined.
     */
    public boolean isCardPresent() throws CardException {
        try {
            int[] status = SCardGetStatusChange(contextId, 0,
                    new int[] {SCARD_STATE_UNAWARE}, new String[] {name});
            if(status != null)
                return (status[0] & SCARD_STATE_PRESENT) != 0;
            else
                return false;
        } catch (PCSCException e) {
                throw new CardException("org.poreid.pcscforjava."
                    + "TerminalImpl.isCardPresent "
                    + "PCSCException: " + e.getMessage(), e);
        }
    }
    
    /**
     * Common function of waitForCardAbsent and waitForCardPresent.
     * Manages the waiting for a state card.
     * @param wantPresent true if the card must be present.\n
     * false otherwise.
     * @param timeout the timeout of waiting.
     * @return true if the waiting event is satisfied.\n
     * false otherwise.
     * @throws CardException if a CardException occurs.
     */
    private boolean waitForCard(boolean wantPresent, long timeout)
            throws CardException
    {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout must not be negative");
        }
        if (timeout == 0) {
            timeout = TIMEOUT_INFINITE;
        }
        int[] status = new int[] {SCARD_STATE_UNAWARE};
        String[] readers = new String[] {name};
        try {
            // check if card status already matches
            status = SCardGetStatusChange(contextId, 0, status, readers);
            boolean present;
            if(status != null)
                present = (status[0] & SCARD_STATE_PRESENT) != 0;
            else
                present = !wantPresent;
            
            if (wantPresent == present) {
                return true;
            }

            // no match, wait
            status = SCardGetStatusChange(contextId, timeout, status, readers);
            if(status != null)
                present = (status[0] & SCARD_STATE_PRESENT) != 0;
            else
                present = !wantPresent;
            // should never happen
            if (wantPresent != present) {
                //throw new CardException("wait mismatch");
                return false;
            }
            return true;
        } catch (PCSCException e) {
            if (e.code == PCSCErrorValues.SCARD_E_TIMEOUT) {
                return false;
            } else {
                throw new CardException("org.poreid.pcscforjava."
                    + "TerminalImpl.waitForCard "
                    + "PCSCException: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Waits until a card is present in this terminal or the timeout
     * expires. If the method returns due to an expired timeout, it returns
     * false. Otherwise it return true.
     *
     * <P>If a card is present in this terminal when this
     * method is called, it returns immediately.
     *
     * @param timeout if positive, block for up to <code>timeout</code>
     *   milliseconds; if zero, block indefinitely; must not be negative.
     * @return false if the method returns due to an expired timeout,
     *   true otherwise.
     *
     * @throws IllegalArgumentException if timeout is negative
     * @throws CardException if the operation failed
     */
    public boolean waitForCardPresent(long timeout) throws CardException {
        return waitForCard(true, timeout);
    }

    /**
     * Waits until a card is absent in this terminal or the timeout
     * expires. If the method returns due to an expired timeout, it returns
     * false. Otherwise it return true.
     *
     * <P>If no card is present in this terminal when this
     * method is called, it returns immediately.
     *
     * @param timeout if positive, block for up to <code>timeout</code>
     *   milliseconds; if zero, block indefinitely; must not be negative
     * @return false if the method returns due to an expired timeout,
     *   true otherwise.
     *
     * @throws IllegalArgumentException if timeout is negative.
     * @throws CardException if the operation failed.
     */
    public boolean waitForCardAbsent(long timeout) throws CardException {
        return waitForCard(false, timeout);
    }
    
    /**
     * Returns a string description of the terminal object.
     * @return a string description of the terminal object.
     */
    public String toString() {
        return "PC/SC terminal " + name;
    }

    /**
     * Terminates all outstanding actions with this terminal.<br />
     * The only requests that you can cancel are those that require waiting for
     * external action by the smart card or user.<br /> 
     * Any such outstanding action requests will terminate with a status 
     * indication that the action was canceled.
     *
     * @throws CardException if the card operation failed.
     */
    @Override
    public synchronized void cancelOperation() throws CardException {
        try {
            SCardCancel(contextId);
        }
        catch(PCSCException ex)
        {
            throw new CardException("org.poreid.pcscforjava."
                    + "TerminalImpl.cancelOperation "
                    + "PCSCException: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Gets the status of the card in this card terminal.
     *
     * @return the status of the card in this card terminal.<br /><ul>
     * 
     * <li>The first array byte of the array contains the ATR of the card.<br />
     *		Warning the byte is NULL if there is no card in this
     *		card terminal.</li>
     * <li>The second one is just a byte and contains the state of the card it 
     * must be:<br /><ul>
     * <li><code>SCARD_ABSENT</code> if there is no card in the reader.</li>
     * <li><code>SCARD_PRESENT</code> if there is a card in the reader, but it
     *      has not been moved into position for use.</li>
     * <li><code>SCARD_SWALLOWED</code> There is a card in the reader in position
     *		for use. The card is not powered.</li>
     * <li><code>SCARD_POWERED</code> Power is being provided to the card, but the
     *		reader driver is unaware of the mode of the card.</li>
     * <li><code>SCARD_NEGOTIABLE</code> The card has been reset and is awaiting
     *		PTS negotiation.</li>
     * <li><code>SCARD_SPECIFIC</code> The card has been reset and specific
     *		communication protocols have been established.</li></ul>
     * <li>The third one is just a byte and contains the current protocol of the
     * card it must be:<br /><ul>
     * <li><code>SCARD_PROTOCOL_RAW</code> if raw transfer protocol is in use.
     * </li>
     * <li><code>SCARD_PROTOCOL_T0</code> if the ISO 7816-3 T=0 protocol is in 
     * use.</li>
     * <li><code>SCARD_PROTOCOL_T1</code> if the ISO 7816-3 T=1 protocol is in 
     * use.</li></ul>
     * </ul>
     *
     * @throws CardException if the card operation failed.
     */
    @Override
    public synchronized byte[][] getCardStatus() throws CardException {
        byte[][] _ppBResult = new byte[3][];
        byte[]   _pBStatus = new byte[2];
        byte[]   _pBAtr;

        try { _pBAtr = SCardStatus(contextId, _pBStatus); }
        catch (PCSCException ex) 
        { throw new CardException("org.poreid.pcscforjava."
                    + "TerminalImpl.getCardStatus "
                    + "PCSCException: " + ex.getMessage(), ex); }

        _ppBResult[0] = _pBAtr;
        
        _ppBResult[1] = new byte[1];
        _ppBResult[1][0] = _pBStatus[0];
        _ppBResult[2] = new byte[1];
        _ppBResult[2][0] = _pBStatus[1];

        return _ppBResult;
    }

    /**
     * Notify a disconnection of the terminal.
     */
    synchronized void notifyDisconnection()
    {
        this.card = null;
    }

    @Override
    protected void finalize() throws Throwable {
       notifyDisconnection();
       super.finalize();
    }
}
