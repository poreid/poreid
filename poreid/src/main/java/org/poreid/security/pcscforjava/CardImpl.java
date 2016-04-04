/*
 * Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
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

import org.poreid.pcscforjava.ATR;
import org.poreid.pcscforjava.CardChannel;
import org.poreid.pcscforjava.CardPermission;
import org.poreid.pcscforjava.PCSCErrorValues;
import org.poreid.pcscforjava.CardException;
import org.poreid.pcscforjava.Card;

import static org.poreid.security.pcscforjava.PCSC.*;
import static org.poreid.security.pcscforjava.PCSCDefines.*;

/**
 * Card implementation.
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  Matthieu Leromain
 */
final class CardImpl extends Card {

    private static enum State { OK, REMOVED, DISCONNECTED };

    // the terminal that created this card
    private final TerminalImpl terminal;

    // the native SCARDHANDLE
    final long cardId;

    // atr of this card
    private ATR atr;

    // protocol in use, one of SCARD_PROTOCOL_T0 and SCARD_PROTOCOL_T1
    final int protocol;
    
    // sharing mode in use, one of SCARD_SHARE_SHARED and SCARD_SHARE_DIRECT and
    // SCARD_SHARE_EXCLUSIVE
    final int sharingMode;

    // the basic logical channel (channel 0)
    private final ChannelImpl basicChannel;

    // state of this card connection
    private volatile State state;
        
    /**
     * Constructs a new Card object.
     *
     * <p>This constructor is called by subclasses only. Application should
     * call the {@linkplain CardTerminal#connect CardTerminal.connect()}
     * method to obtain a {@link Card Card} object.
     * 
     * @param terminal the terminal which contains the smart card object.
     * @param protocol the protocol string. It can be: <br />
     * <ul>
     * <li>direct: for terminal direct access.</li>
     * <li>*: for shared access with T = 0 or T = 1 protocol.</li>
     * <li>t=0: for shared access with T = 0 protocol.</li>
     * <li>t=1: for shared access with T = 1 protocol.</li>
     * <li>exclusive_*: for exclusive access with T = 0 or T = 1 protocol.</li>
     * <li>exclusive_t=0: for exclusive access with T = 0 protocol.</li>
     * <li>exclusive_t=1: for exclusive access with T = 1 protocol.</li> 
     * </ul>
     * @param iForceProtocol the protocol string. It can be: <br />
     * <ul>
     * <li>SCARD_PROTOCOL_T0: for T = 0 protocol.</li>
     * <li>SCARD_PROTOCOL_T1: for T = 1 protocol.</li>
     * <li>SCARD_PROTOCOL_T0 | SCARD_PROTOCOL_T1: for T = 0 or 1 protocol.</li>
     * </ul>
     * This is made for bug in some Mac OS X drivers which do not like the 
     * SCARD_PROTOCOL_UNDEFINED value.
     * 
     * @throw PCSCException if a PCSC exception occurs.
     */
    CardImpl(TerminalImpl terminal, String protocol, int iForceProtocol) 
            throws PCSCException {
        this.terminal = terminal;
        int _iSharingMode = SCARD_SHARE_SHARED;
        int connectProtocol;
        
        if(protocol.equalsIgnoreCase("direct")) 
        {
            connectProtocol = iForceProtocol;
            _iSharingMode = SCARD_SHARE_DIRECT;
        } 
        else
        {
            if(protocol.contains("*")) 
                connectProtocol = SCARD_PROTOCOL_T0 | SCARD_PROTOCOL_T1;
            else if(protocol.toLowerCase().contains("t=0")) 
                connectProtocol = SCARD_PROTOCOL_T0;
            else if(protocol.toLowerCase().contains("t=1")) 
                connectProtocol = SCARD_PROTOCOL_T1;
            else 
                throw new IllegalArgumentException("Unsupported protocol " +
                        protocol);
            
            if(protocol.toLowerCase().contains("exclusive_"))
                _iSharingMode = SCARD_SHARE_EXCLUSIVE;
        }
        
        cardId = SCardConnect(terminal.contextId, terminal.name,
                    _iSharingMode, connectProtocol);
        
        if(_iSharingMode != SCARD_SHARE_DIRECT)
        {
            byte[] status = new byte[2];
            byte[] atrBytes = SCardStatus(cardId, status);
            atr = new ATR(atrBytes, getFrequency());
            
            // I do not know why but sometimes SCardStatus does not return
            // the correct ATR !!
            while((atrBytes[0] != 0x3B) && (atrBytes[0] != 0x3F))
            {
                atrBytes = SCardStatus(cardId, status);
                atr = new ATR(atrBytes, getFrequency());
            }    
            
            this.protocol = status[1] & 0xff;
        }
        else
        {
            // Returns fake ATR
            byte[] atrBytes = new byte[17];
            atrBytes[0] = 0x3B;
            atrBytes[1] = 0x0F;
            atrBytes[2] = 0x44;
            atrBytes[3] = 0x49;
            atrBytes[4] = 0x52;
            atrBytes[5] = 0x45;
            atrBytes[6] = 0x43;
            atrBytes[7] = 0x54;
            atrBytes[8] = 0x5F;
            atrBytes[9] = 0x41;
            atrBytes[10] = 0x54;
            atrBytes[11] = 0x52;
            atrBytes[12] = 0x5F;
            atrBytes[13] = 0x46;
            atrBytes[14] = 0x41;
            atrBytes[15] = 0x4B;
            atrBytes[16] = 0x45;
            
            // Default smart card clock is 4MHz
            atr = new ATR(atrBytes, 4000000);
            this.protocol = 0;
        }

        this.sharingMode = _iSharingMode;
        
        basicChannel = new ChannelImpl(this, 0);
        state = State.OK;
    }
    
    /**
     * Constructs a new Card object.
     *
     * <p>This constructor is called by subclasses only. Application should
     * call the {@linkplain CardTerminal#connect CardTerminal.connect()}
     * method to obtain a {@link Card Card} object.
     * 
     * @param terminal the terminal which contains the smart card object.
     * @param protocol the protocol string. It can be: <br />
     * <ul>
     * <li>direct: for terminal direct access.</li>
     * <li>*: for shared access with T = 0 or T = 1 protocol.</li>
     * <li>t=0: for shared access with T = 0 protocol.</li>
     * <li>t=1: for shared access with T = 1 protocol.</li>
     * <li>exclusive_*: for exclusive access with T = 0 or T = 1 protocol.</li>
     * <li>exclusive_t=0: for exclusive access with T = 0 protocol.</li>
     * <li>exclusive_t=1: for exclusive access with T = 1 protocol.</li> 
     * </ul>
     * 
     * @throw PCSCException if a PCSC exception occurs.
     */
    CardImpl(TerminalImpl terminal, String protocol) throws PCSCException {
        this(terminal, protocol, SCARD_PROTOCOL_UNDEFINED);
    }
    
    /**
     * Check the state of the smart card.
     */
    void checkState()  {
        State s = state;
        if (s == State.DISCONNECTED) {
            throw new IllegalStateException("org.poreid.pcscforjava.CardImpl."
                    + "checkState PCSCException: SCARD_W_UNPOWERED_CARD");
        } else if (s == State.REMOVED) {
            throw new IllegalStateException("org.poreid.pcscforjava.CardImpl."
                    + "checkState PCSCException: SCARD_W_REMOVED_CARD");
        }
    }
    
    /**
     * Check if the smart card is valid or not.
     * @return 
     */
    @Override
    public boolean isValid() {
        if (state != State.OK) {
            return false;
        }
        // ping card via SCardStatus
        try {
            SCardStatus(cardId, new byte[2]);
            return true;
        } catch (PCSCException e) {
            // If the connection is performed in direct mode then no problem 
            // with this card.
            if(this.getSharingMode() == SCARD_SHARE_DIRECT)
                return true;
            
            state = State.REMOVED;
            return false;
        }
    }
    
    /**
     * Check the security of the smart card.
     * @param action the action which must be secured.
     */
    private void checkSecurity(String action) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new CardPermission(terminal.name, action));
        }
    }

    /**
     * Manage the removal card from PCSC exception.
     * @param e the PCSC exception.
     */
    void handleError(PCSCException e) {
        if (e.code == PCSCErrorValues.SCARD_W_REMOVED_CARD) {
            state = State.REMOVED;
        }
    }
    
    /**
     * Returns the {@link ATR ATR} of this card.
     *
     * @return the {@link ATR ATR} of this card.
     */
    public ATR getATR() {
        return atr;
    }
    
    /**
     * Returns the protocol in use for this card.
     *
     * @return the protocol in use for this card, for example "T=0", "T=1", 
     * "DIRECT".
     */
    public String getProtocol() {
        switch (protocol) {
        case SCARD_PROTOCOL_T0:
            return "T=0";
        case SCARD_PROTOCOL_T1:
            return "T=1";
        default:
            // should never occur
            return "Unknown protocol " + protocol;
        }
    }
    
    /**
     * Returns the sharing mode of the smart card connection.
     * 
     * @return the sharing mode of the smart card connection. 
     * It can be:<br /><ul>
     * <li>{@link SCARD_SHARE_EXCLUSIVE SCARD_SHARE_EXCLUSIVE} for exclusive 
     * mode.</li>
     * <li>{@link SCARD_SHARE_DIRECT SCARD_SHARE_DIRECT} for direct mode.</li>
     * <li>{@link SCARD_SHARE_SHARED SCARD_SHARE_SHARED} for share mode.</li>
     * </ul>
     */
    public int getSharingMode() {
        return this.sharingMode;
    }
            
    /**
     * Returns the {@link CardChannel CardChannel} for the basic logical 
     * channel. 
     * <br />The basic logical channel has a channel number of 0.
     * 
     * @return the {@link CardChannel CardChannel} for the basic logical 
     * channel.
     *
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required.
     *   {@linkplain CardPermission permission}.
     * @throws IllegalStateException if this card object has been disposed of
     *   via the {@linkplain #disconnect disconnect()} method.
     */
    public CardChannel getBasicChannel() {
        checkSecurity("getBasicChannel");
        checkState();
        return basicChannel;
    }
    
    /**
     * Gets the status word of a byte array.
     * @param b the byte array.
     * @return -1 if an error occurs.<br />
     * The status word of the byte array.
     */
    private static int getSW(byte[] b) {
        if (b.length < 2) {
            return -1;
        }
        int sw1 = b[b.length - 2] & 0xff;
        int sw2 = b[b.length - 1] & 0xff;
        return (sw1 << 8) | sw2;
    }
    
    /**
     * Command to open a channel.
     */
    private static byte[] commandOpenChannel = new byte[] {0, 0x70, 0, 0, 1};
    
    /**
     * Opens a new logical channel to the card and returns it. The channel is
     * opened by issuing a <code>MANAGE CHANNEL</code> command that should use
     * the format <code>[00 70 00 00 01]</code>.
     * 
     * @return the new logical {@link CardChannel CardChannel}.
     *
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     * @throws CardException is a new logical channel could not be opened.
     * @throws IllegalStateException if this card object has been disposed of
     *   via the {@linkplain #disconnect disconnect()} method.
     */
    public CardChannel openLogicalChannel() throws CardException {
        checkSecurity("openLogicalChannel");
        checkState();
        checkExclusive();
        try {
            byte[] response = SCardTransmit
                (cardId, protocol, commandOpenChannel, 0,
                commandOpenChannel.length);
            if ((response.length != 3) || (getSW(response) != 0x9000)) {
                throw new CardException
                        ("org.poreid.pcscforjava.CardImpl.openLogicalChannel "
                        + "PCSCException: SCARD_F_UNKNOWN_ERROR Card response: "
                        + PCSC.toString(response));
            }
            return new ChannelImpl(this, response[0]);
        } catch (PCSCException e) {
            handleError(e);
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.openLogicalChannel "
                        + "PCSCException: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check the excusivity of the request.
     * @throws CardException if a card exception occurs.
     */
    void checkExclusive() throws CardException {        
    }
    
    /**
     * Requests exclusive access to this card.
     *
     * <p>Once a thread has invoked {@link beginExclusive beginExclusive}, only 
     * this thread is allowed to communicate with this card until it calls
     * {@link endExclusive endExclusive}. Other threads attempting communication
     * will receive a {@link CardException CardException}.
     *
     * <p>Applications have to ensure that exclusive access is correctly
     * released. This can be achieved by executing
     * the {@link beginExclusive beginExclusive} and {@link endExclusive 
     * endExclusive} calls in a <code>try ... finally</code> block.
     *
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     * @throws CardException if exclusive access has already been set
     *   or if exclusive access could not be established.
     * @throws IllegalStateException if this card object has been disposed of
     *   via the {@linkplain #disconnect disconnect()} method.
     */
    public synchronized void beginExclusive() throws CardException {
        checkSecurity("exclusive");
        checkState();
        
        try {
            SCardBeginTransaction(cardId);
        } catch (PCSCException e) {
            handleError(e);
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.beginExclusive "
                    + "PCSCException: SCARD_F_UNKNOWN_ERROR", e);
        }        
    }
    
    /**
     * Releases the exclusive access previously established using
     * {@link beginExclusive beginExclusive}.
     *
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     * @throws IllegalStateException if the active Thread does not currently 
     * have exclusive access to this card or if this card object has been 
     * disposed of  via the {@linkplain #disconnect disconnect()} method.
     * @throws CardException if the operation failed.
     */
    public synchronized void endExclusive() throws CardException {
        checkState();
        
        try {
            SCardEndTransaction(cardId, SCARD_LEAVE_CARD);
        } catch (PCSCException e) {
            handleError(e);
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.endExclusive "
                    + "PCSCException: SCARD_F_UNKNOWN_ERROR", e);
        } 
    }
    
    /**
     * Transmits a control command to the terminal device.
     *
     * <p>This can be used to, for example, control terminal functions like
     * a built-in PIN pad or biometrics.
     *
     * @param controlCode the control code of the command.
     * @param command the command data.
     * 
     * @return the response of the {@link transmitControlCommand 
     * transmitControlCommand}.
     *
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     * @throws NullPointerException if command is null.
     * @throws CardException if the card operation failed.
     * @throws IllegalStateException if this card object has been disposed of
     *   via the {@linkplain #disconnect disconnect()} method.
     */
    @Override
    public byte[] transmitControlCommand(int controlCode, byte[] command)
            throws CardException {
        checkSecurity("transmitControl");
        checkState();
        checkExclusive();
        if (command == null) {
            throw new NullPointerException();
        }
        
        try {
            byte[] r = SCardControl(cardId, controlCode, command);
            
            if(r == null)
                return new byte[0];

            return r;
        } catch (PCSCException e) {
            handleError(e);
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.transmitControlCommand "
                    + "PCSCException: " + e.getMessage(), e);
        }
    }
    
    /**
     * Disconnects the connection with this card. After this method returns,
     * calling methods on this object or in {@link CardChannel CardChannels} 
     * associated with this object that require interaction with the card will 
     * raise an IllegalStateException.
     *
     * @param iDisposition must be {@link SCARD_LEAVE_CARD SCARD_LEAVE_CARD} to 
     * leave the card in its current state or {@link SCARD_RESET_CARD 
     * SCARD_RESET_CARD} to reset the card before the disconnection or 
     * {@link SCARD_UNPOWER_CARD SCARD_UNPOWER_CARD} to power off the card 
     * before the disconnection or {@link SCARD_EJECT_CARD SCARD_EJECT_CARD} to 
     * eject the card before the disconnection.
     *
     * @throws CardException if the card operation failed.
     * @throws IllegalArgumentException if a parameter is incoherent with this
     * function.
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     */
    @Override
    public void disconnect(int iDisposition) throws CardException {
        checkSecurity("disconnect");

        if (state != State.OK)
            return;

        switch(iDisposition)
        {
            case SCARD_LEAVE_CARD:
            case SCARD_RESET_CARD:
            case SCARD_UNPOWER_CARD:
            case SCARD_EJECT_CARD:
                break;

            default:
                throw new IllegalArgumentException("Unsupported disposition "
                        + iDisposition);
        }

        checkExclusive();
        try {
            if(iDisposition != SCARD_LEAVE_CARD)
                SCardDisconnect(cardId, iDisposition);
        } catch (PCSCException e) {
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.disconnect "
                    + "PCSCException: " + e.getMessage(), e);
        } finally {
            if(iDisposition != SCARD_LEAVE_CARD)
            {
            state = State.DISCONNECTED;            
            this.terminal.notifyDisconnection();
        }
    }
    }

    /**
     * Gets an attribute of this card.
     *
     * @param iAttribute is the identifier for the attribute to get and must be:
     * <br /><br /><ul>
     * <li>{@link SCARD_ATTR_ATR_STRING SCARD_ATTR_ATR_STRING}: Answer to reset 
     * (ATR) string.</li>
     *<li>{@link SCARD_ATTR_CHANNEL_ID SCARD_ATTR_CHANNEL_ID}: 
     *	unsigned long encoded as 0xDDDDCCCC, where DDDD = data channel type
     *	and CCCC = channel number:<br />
     *	The following encodings are defined for DDDD:<br /><ul>
     *	<li>0x01 serial I/O; CCCC is a port number.</li>
     *	<li>0x02 parallel I/O; CCCC is a port number.</li>
     *	<li>0x04 PS/2 keyboard port; CCCC is zero.</li>
     *  <li>0x08 SCSI; CCCC is SCSI ID number.</li>
     *	<li>0x10 IDE; CCCC is device number.</li>
     *	<li>0x20 USB; CCCC is device number.</li>
     *	<li>0xFy vendor-defined interface with y in the range zero through 15; 
     *  CCCC is vendor defined.</li></ul>
     *<li>{@link SCARD_ATTR_CHARACTERISTICS SCARD_ATTR_CHARACTERISTICS}: 
     *  unsigned long indicating which mechanical characteristics are supported.
     * <br />If zero, no special characteristics are supported.<br />
     * Note that multiple bits can be set:<br /><ul>
     *  <li>0x00000001 Card swallowing mechanism.</li>
     *	<li>0x00000002 Card ejection mechanism.</li>
     *	<li>0x00000004 Card capture mechanism.</li>
     *	<li>All other values are reserved for future use (RFU).</li></ul>
     *<li>{@link SCARD_ATTR_CURRENT_BWT SCARD_ATTR_CURRENT_BWT}: Current block 
     *  waiting time.
     *<li>{@link SCARD_ATTR_CURRENT_CLK SCARD_ATTR_CURRENT_CLK}: Current clock 
     * rate, in kHz.
     *<li>{@link SCARD_ATTR_CURRENT_CWT SCARD_ATTR_CURRENT_CWT}: Current 
     * character waiting time.
     *<li>{@link SCARD_ATTR_CURRENT_D SCARD_ATTR_CURRENT_D}: Bit rate conversion 
     * factor.
     *<li>{@link SCARD_ATTR_CURRENT_EBC_ENCODING 
     * SCARD_ATTR_CURRENT_EBC_ENCODING}: 
     *	Current error block control encoding.<br /><ul>
     *	<li>0 = Longitudinal Redundancy Check (LRC).</li>
     *	<li>1 = Cyclical Redundancy Check (CRC).</li></ul>
     *<li>{@link SCARD_ATTR_CURRENT_F SCARD_ATTR_CURRENT_F}: Clock conversion 
     * factor.
     *<li>{@link SCARD_ATTR_CURRENT_IFSC SCARD_ATTR_CURRENT_IFSC}: Current byte 
     * size for information field size card.
     *<li>{@link SCARD_ATTR_CURRENT_IFSD SCARD_ATTR_CURRENT_IFSD}: Current byte 
     * size for information field size device.
     *<li>{@link SCARD_ATTR_CURRENT_N SCARD_ATTR_CURRENT_N}: Current guard time.
     *<li>{@link SCARD_ATTR_CURRENT_PROTOCOL_TYPE 
     * SCARD_ATTR_CURRENT_PROTOCOL_TYPE}: 
     * 	unsigned long encoded as 0x0rrrpppp where rrr is RFU and should be 
     *  0x000.<br />
     *	pppp encodes the current protocol type. Whichever bit has been set 
     * indicates which ISO protocol is currently in use. (For example, if bit 
     * zero is set, T=0 protocol is in effect.)
     *<li>{@link SCARD_ATTR_CURRENT_W SCARD_ATTR_CURRENT_W}: Current work 
     * waiting time.
     *<li>{@link SCARD_ATTR_DEFAULT_CLK SCARD_ATTR_DEFAULT_CLK}: Default clock 
     * rate, in kHz.
     * <li>{@link SCARD_ATTR_DEFAULT_DATA_RATE SCARD_ATTR_DEFAULT_DATA_RATE}: 
     *	Default data rate, in bps.
     *<li>{@link SCARD_ATTR_DEVICE_FRIENDLY_NAME 
     * SCARD_ATTR_DEVICE_FRIENDLY_NAME}: Reader's display name.
     *<li>{@link SCARD_ATTR_DEVICE_IN_USE SCARD_ATTR_DEVICE_IN_USE}: Reserved 
     * for future use.
     *<li>{@link SCARD_ATTR_DEVICE_SYSTEM_NAME SCARD_ATTR_DEVICE_SYSTEM_NAME}: 
     *	Reader's system name.
     *<li>{@link SCARD_ATTR_DEVICE_UNIT SCARD_ATTR_DEVICE_UNIT}: 
     *	Instance of this vendor's reader attached to the computer. The first 
     * instance will be	device unit 0, the next will be unit 1 (if it is the 
     * same brand of reader) and so on.<br />
     * Two different brands of readers will both have zero for this value.
     *<li>{@link SCARD_ATTR_ICC_INTERFACE_STATUS 
     * SCARD_ATTR_ICC_INTERFACE_STATUS}: Single byte. Zero if smart card 
     * electrical contact is not active; nonzero if contact is active.
     *<li>{@link SCARD_ATTR_ICC_PRESENCE SCARD_ATTR_ICC_PRESENCE}: 
     *	Single byte indicating smart card presence:<br /><ul>
     *	<li>0 = not present.</li>
     *	<li>1 = card present but not swallowed (applies only if reader supports 
     * smart card swallowing).</li>
     *	<li>2 = card present (and swallowed if reader supports smart card 
     * swallowing).</li>
     *	<li>4 = card confiscated.</li></ul>
     *<li>{@link SCARD_ATTR_ICC_TYPE_PER_ATR SCARD_ATTR_ICC_TYPE_PER_ATR}: 
     *	Single byte indicating smart card type:<br /><ul>
     *	<li>0 = unknown type.</li>
     *	<li>1 = 7816 Asynchronous.</li>
     *	<li>2 = 7816 Synchronous.</li>
     *	<li>Other values RFU.</li></ul>
     *<li>{@link SCARD_ATTR_MAX_CLK SCARD_ATTR_MAX_CLK}: Maximum clock rate, in 
     * kHz.
     *<li>{@link SCARD_ATTR_MAX_DATA_RATE SCARD_ATTR_MAX_DATA_RATE}: Maximum 
     * data rate, in bps.
     *<li>{@link SCARD_ATTR_MAX_IFSD SCARD_ATTR_MAX_IFSD}: Maximum bytes for 
     * information file size device.
     *<li>{@link SCARD_ATTR_POWER_MGMT_SUPPORT SCARD_ATTR_POWER_MGMT_SUPPORT}: 
     * Zero if device does not support power down while smart card is inserted. 
     * <br />Nonzero otherwise.
     *<li>{@link SCARD_ATTR_PROTOCOL_TYPES SCARD_ATTR_PROTOCOL_TYPES}: 
     * unsigned long encoded as 0x0rrrpppp where rrr is RFU and should be 0x000.
     * <br />pppp encodes the supported protocol types. A '1' in a given bit 
     * position indicates support for the associated ISO protocol, so if bits 
     * zero and one are set, both T=0 and T=1 protocols are supported.
     *<li>{@link SCARD_ATTR_VENDOR_IFD_SERIAL_NO 
     * SCARD_ATTR_VENDOR_IFD_SERIAL_NO}: Vendor-supplied interface device serial
     *  number.
     *<li>{@link SCARD_ATTR_VENDOR_IFD_TYPE SCARD_ATTR_VENDOR_IFD_TYPE}: 
     * Vendor-supplied interface device type (model designation of reader).
     *<li>{@link SCARD_ATTR_VENDOR_IFD_VERSION SCARD_ATTR_VENDOR_IFD_VERSION}: 
     * Vendor-supplied interface device version (DWORD in the form 0xMMmmbbbb 
     * where MM = major version, mm = minor version, and bbbb = build number).
     *<li>{@link SCARD_ATTR_VENDOR_NAME SCARD_ATTR_VENDOR_NAME}: Vendor name.
     * </ul>
     *
     * @return the attribute requested.
     *
     * @throws CardException if the card operation failed.
     * @throws IllegalArgumentException if a parameter is incoherent with this
     * function.
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     */
    @Override
    public byte[] getAttrib(int iAttribute) throws CardException {
        checkSecurity("getAttrib");
        checkState();
        checkExclusive();
        
        if((iAttribute == PCSCDefines.SCARD_ATTR_PROTOCOL_TYPES) &&
                (!(System.getProperty("os.name").contains("Windows"))))
        {
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.getAttrib "
                    + "PCSCException: SCARD_E_UNSUPPORTED_FEATURE");
        }
        
        try {
            return SCardGetAttrib(cardId, iAttribute);
        } catch (PCSCException ex) {
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.getAttrib "
                    + "PCSCException: " + ex.getMessage(), ex);
        }
    }

    /**
     * Sets an attribute of this card.
     *
     * @param iAttribute is the identifier for the attribute to set and must be:
     * <br /><ul><li>{@link SCARD_ATTR_SUPRESS_T1_IFS_REQUEST 
     * SCARD_ATTR_SUPRESS_T1_IFS_REQUEST}: 
     * Suppress sending of T=1 IFSD packet from the reader to the card.
     * (Can be used if the currently inserted card does not support an
     * IFSD request.)
     * @param pBCommand the new attribute for the identifier request.
     *
     * @throws CardException if the card operation failed.
     * @throws IllegalArgumentException if a parameter is incoherent with this
     * function.
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     */
    @Override
    public void setAttrib(int iAttribute, byte[] pBCommand)
            throws CardException {
        checkSecurity("setAttrib");

        if(iAttribute != SCARD_ATTR_SUPRESS_T1_IFS_REQUEST)
        {
            throw new IllegalArgumentException("Unsupported attribute "
                        + iAttribute);
        }

        checkState();
        checkExclusive();

        try {
            SCardSetAttrib(cardId, iAttribute, pBCommand);
        } catch (PCSCException ex) {
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.setAttrib "
                    + "PCSCException: " + ex.getMessage(), ex);
        }
    }

    /**
     * Reestablish the connection with this card. After this method returns,
     * the connection with the card has been reseted.<br />
     * The reestablishment can be of type WARM_RESET (i.e power on the card
     * without power off it) or COLD_RESET (i.e power on the card after
     * have perfoming a power off).
     *
     * @param iShareMode must be {@link SCARD_SHARE_SHARED SCARD_SHARE_SHARED}
     * if the reader must be in share mode or {@link SCARD_SHARE_EXCLUSIVE 
     * SCARD_SHARE_EXCLUSIVE} if the reader must be in exclusive
     * mode.
     * @param iInitialization must be {@link SCARD_LEAVE_CARD SCARD_LEAVE_CARD} 
     * to leave the card in its current state or {@link SCARD_RESET_CARD 
     * SCARD_RESET_CARD} to perform a WARM_RESET to the
     * card of {@link SCARD_UNPOWER_CARD SCARD_UNPOWER_CARD} to perform a 
     * COLD_RESET to the card.
     *
     * @throws CardException if the card operation failed.
     * @throws IllegalArgumentException if a parameter is incoherent with this
     * function.
     * @throws IllegalStateException if this card object has been disposed of
     *   via the {@linkplain #disconnect disconnect()} method.
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     */
    public void reconnect(int iShareMode, int iInitialization)
            throws CardException {
        byte[] _atr;

        checkSecurity("reconnect");

        if(state != State.OK)
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.reconnect "
                    + "PCSCException: SCARD_W_UNPOWERED_CARD");

        switch(iShareMode)
        {
            case SCARD_SHARE_SHARED:
            case SCARD_SHARE_EXCLUSIVE:
                break;

            default:
                throw new IllegalArgumentException("Unsupported share mode "
                        + iShareMode);
        }

        switch(iInitialization)
        {
            case SCARD_LEAVE_CARD:
            case SCARD_RESET_CARD:
            case SCARD_UNPOWER_CARD:
                break;

            default:
                throw new IllegalArgumentException("Unsupported initialization "
                        + iInitialization);
        }

        checkExclusive();

        try {
            _atr = SCardReconnect(cardId, iShareMode, protocol, iInitialization);
        } catch (PCSCException ex) {
            throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.reconnect "
                    + "PCSCException: " + ex.getMessage(), ex);
        }

        if((_atr != null) && (_atr.length > 0))
        {
            atr = null;
            try {
                atr = new ATR(_atr, getFrequency());
            } catch (PCSCException ex) {
                throw new CardException("org.poreid.pcscforjava."
                    + "CardImpl.reconnect "
                    + "PCSCException: " + ex.getMessage(), ex);
            }
        }
    }
    
    /**
     * Returns the frequency of the smart card object.
     * @return the frequency of the smart card object.
     * @throws PCSCException if a PCSC exception occurs.
     */
    public int getFrequency() throws PCSCException
    {
        byte[]  _clockCard;
        int     _iClockCard = 0;
        int     _i = 0, _j = 0; 
        
        try
        {
            // Get the clock card frequency in KHz
            _clockCard = SCardGetAttrib(cardId, SCARD_ATTR_CURRENT_CLK);
        }
        catch(PCSCException ex)
        {
            if((ex.getMessage().contains("SCARD_E_UNSUPPORTED_FEATURE")) ||
                    (ex.getMessage().contains("SCARD_E_INVALID_VALUE")) || 
                    (ex.getMessage().contains("SCARD_E_INSUFFICIENT_BUFFER")))
            {
                // Default value is 4MHz if no possibility to know it.
                _clockCard = new byte[4];
                _clockCard[0] = (byte) 0xA0;
                _clockCard[1] = (byte) 0x0F;
                _clockCard[2] = 0x00;
                _clockCard[3] = 0x00;
            }
            else 
                throw ex;
        }

        _i = 0;

        while(_i < _clockCard.length)
        {
            int _iTmp = PlatformPCSC.unsignedByteToInt(_clockCard[_i]);
            _iClockCard += _iTmp << (_j * 8);
            _i++;
            _j++;
        }
        
        // Return in Hz
        return (_iClockCard * 1000);
    }

    /**
     * Prints the smart card object.
     * @return the smart card object description.
     */
    @Override
    public String toString() {
        return "PC/SC card in " + terminal.getName()
            + ", protocol " + getProtocol() + ", state " + state;
    }
    
    /**
     * Destructor to the class.
     * @throws Throwable if throwable exception occurs.
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            if (state == State.OK) {
                //SCardDisconnect(cardId, SCARD_LEAVE_CARD);
                //this.terminal.notifyDisconnection();
            }
        } finally {
            super.finalize();
        }
    }

}
