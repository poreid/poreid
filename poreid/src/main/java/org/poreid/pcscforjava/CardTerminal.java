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

package org.poreid.pcscforjava;

/**
 * A Smart Card terminal, sometimes refered to as a Smart Card Reader.<br />
 * A CardTerminal object can be obtained by calling
 * {@linkplain CardTerminals#list}
 * or {@linkplain CardTerminals#getTerminal CardTerminals.getTerminal()}.
 *
 * <p>Note that physical card readers with slots for multiple cards are
 * represented by one <code>CardTerminal</code> object per such slot.
 *
 * @see CardTerminals
 * @see TerminalFactory
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  JSR 268 Expert Group
 * @author  Matthieu Leromain
 */
public abstract class CardTerminal {

    /**
     * Constructs a new CardTerminal object.
     *
     * <p>This constructor is called by subclasses only. Application should
     * call {@linkplain CardTerminals#list list()}
     * or {@linkplain CardTerminals#getTerminal getTerminal()}
     * to obtain a CardTerminal object.
     */
    protected CardTerminal() {
        // empty
    }

    /**
     * Returns the unique name of this terminal.
     *
     * @return the unique name of this terminal.
     */
    public abstract String getName();

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
    public abstract Card connect(String protocol) throws CardException;
    
    /**
     * Returns whether a card is present in this terminal.
     *
     * @return whether a card is present in this terminal.
     *
     * @throws CardException if the status could not be determined.
     */
    public abstract boolean isCardPresent() throws CardException;

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
    public abstract boolean waitForCardPresent(long timeout) 
            throws CardException;

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
    public abstract boolean waitForCardAbsent(long timeout) 
            throws CardException;

    /**
     * Terminates all outstanding actions with this terminal.<br />
     * The only requests that you can cancel are those that require waiting for
     * external action by the smart card or user.<br /> 
     * Any such outstanding action requests will terminate with a status 
     * indication that the action was canceled.
     *
     * @throws CardException if the card operation failed.
     */
    public abstract void cancelOperation() throws CardException;
	
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
    public abstract byte[][] getCardStatus() throws CardException;
}
