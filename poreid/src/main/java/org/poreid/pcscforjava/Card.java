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

import static org.poreid.pcscforjava.PCSCDefines.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A Smart Card with which a connection has been established.<br />
 * Card objects are obtained by calling {@link CardTerminal#connect 
 * CardTerminal.connect()}.
 *
 * @see CardTerminal
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  JSR 268 Expert Group
 * @author  Matthieu Leromain
*/
public abstract class Card {

    /**
     * Constructs a new Card object.
     *
     * <p>This constructor is called by subclasses only. Application should
     * call the {@linkplain CardTerminal#connect CardTerminal.connect()}
     * method to obtain a {@link Card Card} object.
     */
    protected Card() {
        // empty
    }

    /**
     * Returns the {@link ATR ATR} of this card.
     *
     * @return the {@link ATR ATR} of this card.
     */
    public abstract ATR getATR();

    /**
     * Returns the protocol in use for this card.
     *
     * @return the protocol in use for this card, for example "T=0", "T=1", 
     * "DIRECT".
     */
    public abstract String getProtocol();

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
    public abstract CardChannel getBasicChannel();

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
    public abstract CardChannel openLogicalChannel() throws CardException;

    /**
     * Requests exclusive access to this card.
     *
     * <p>Once a thread has invoked {@link #beginExclusive beginExclusive}, only 
     * this thread is allowed to communicate with this card until it calls
     * {@link #endExclusive endExclusive}. Other threads attempting communication
     * will receive a {@link CardException CardException}.
     *
     * <p>Applications have to ensure that exclusive access is correctly
     * released. This can be achieved by executing
     * the {@link #beginExclusive beginExclusive} and {@link #endExclusive 
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
    public abstract void beginExclusive() throws CardException;

    /**
     * Releases the exclusive access previously established using
     * {@link #beginExclusive beginExclusive}.
     *
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     * @throws IllegalStateException if the active Thread does not currently 
     * have exclusive access to this card or if this card object has been 
     * disposed of  via the {@linkplain #disconnect disconnect()} method.
     * @throws CardException if the operation failed.
     */
    public abstract void endExclusive() throws CardException;

    /**
     * Transmits a control command to the terminal device.
     *
     * <p>This can be used to, for example, control terminal functions like
     * a built-in PIN pad or biometrics.
     *
     * @param controlCode the control code of the command.
     * @param command the command data.
     * 
     * @return the response of the {@link #transmitControlCommand 
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
    public abstract byte[] transmitControlCommand(int controlCode,
            byte[] command) throws CardException;

    /**
     * Reestablish the connection with this card. After this method returns,
     * the connection with the card has been reseted.<br />
     * The reestablishment can be of type WARM_RESET (i.e power on the card
     * without power off it) or COLD_RESET (i.e power on the card after
     * have perfoming a power off).
     *
     * @param iShareMode must be {@link PCSCDefines#SCARD_SHARE_SHARED 
     * SCARD_SHARE_SHARED}
     * if the reader must be in share mode or {@link 
     * PCSCDefines#SCARD_SHARE_EXCLUSIVE 
     * SCARD_SHARE_EXCLUSIVE} if the reader must be in exclusive
     * mode.
     * @param iInitialization must be {@link PCSCDefines#SCARD_LEAVE_CARD 
     * SCARD_LEAVE_CARD} 
     * to leave the card in its current state or {@link 
     * PCSCDefines#SCARD_RESET_CARD 
     * SCARD_RESET_CARD} to perform a WARM_RESET to the
     * card of {@link PCSCDefines#SCARD_UNPOWER_CARD SCARD_UNPOWER_CARD} to 
     * perform a COLD_RESET to the card.
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
    public abstract void reconnect(int iShareMode, int iInitialization)
            throws CardException;

    /**
     * Disconnects the connection with this card. After this method returns,
     * calling methods on this object or in {@link CardChannel CardChannels} 
     * associated with this object that require interaction with the card will 
     * raise an IllegalStateException.
     *
     * @param iDisposition must be {@link PCSCDefines#SCARD_LEAVE_CARD 
     * SCARD_LEAVE_CARD} to 
     * leave the card in its current state or {@link 
     * PCSCDefines#SCARD_RESET_CARD 
     * SCARD_RESET_CARD} to reset the card before the disconnection or 
     * {@link PCSCDefines#SCARD_UNPOWER_CARD 
     * SCARD_UNPOWER_CARD} to power off the card 
     * before the disconnection or {@link PCSCDefines#SCARD_EJECT_CARD 
     * SCARD_EJECT_CARD} to 
     * eject the card before the disconnection.
     *
     * @throws CardException if the card operation failed.
     * @throws IllegalArgumentException if a parameter is incoherent with this
     * function.
     * @throws SecurityException if a SecurityManager exists and the
     *   caller does not have the required
     *   {@linkplain CardPermission permission}.
     */
    public abstract void disconnect(int iDisposition) throws CardException;
	
    /**
     * Gets an attribute of this card.
     *
     * @param iAttribute is the identifier for the attribute to get and must be:
     * <br /><br /><ul>
     * <li>{@link PCSCDefines#SCARD_ATTR_ATR_STRING SCARD_ATTR_ATR_STRING}: 
     * Answer to reset 
     * (ATR) string.</li>
     *<li>{@link PCSCDefines#SCARD_ATTR_CHANNEL_ID SCARD_ATTR_CHANNEL_ID}: 
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
     *<li>{@link PCSCDefines#SCARD_ATTR_CHARACTERISTICS 
     * SCARD_ATTR_CHARACTERISTICS}: 
     *  unsigned long indicating which mechanical characteristics are supported.
     * <br />If zero, no special characteristics are supported.<br />
     * Note that multiple bits can be set:<br /><ul>
     *  <li>0x00000001 Card swallowing mechanism.</li>
     *	<li>0x00000002 Card ejection mechanism.</li>
     *	<li>0x00000004 Card capture mechanism.</li>
     *	<li>All other values are reserved for future use (RFU).</li></ul>
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_BWT SCARD_ATTR_CURRENT_BWT}: 
     * Current block 
     *  waiting time.
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_CLK SCARD_ATTR_CURRENT_CLK}: 
     * Current clock 
     * rate, in kHz.
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_CWT SCARD_ATTR_CURRENT_CWT}: 
     * Current 
     * character waiting time.
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_D SCARD_ATTR_CURRENT_D}: Bit 
     * rate conversion 
     * factor.
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_EBC_ENCODING 
     * SCARD_ATTR_CURRENT_EBC_ENCODING}: 
     *	Current error block control encoding.<br /><ul>
     *	<li>0 = Longitudinal Redundancy Check (LRC).</li>
     *	<li>1 = Cyclical Redundancy Check (CRC).</li></ul>
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_F SCARD_ATTR_CURRENT_F}: Clock 
     * conversion 
     * factor.
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_IFSC SCARD_ATTR_CURRENT_IFSC}: 
     * Current byte 
     * size for information field size card.
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_IFSD SCARD_ATTR_CURRENT_IFSD}: 
     * Current byte 
     * size for information field size device.
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_N SCARD_ATTR_CURRENT_N}: 
     * Current guard time.
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_PROTOCOL_TYPE 
     * SCARD_ATTR_CURRENT_PROTOCOL_TYPE}: 
     * 	unsigned long encoded as 0x0rrrpppp where rrr is RFU and should be 
     *  0x000.<br />
     *	pppp encodes the current protocol type. Whichever bit has been set 
     * indicates which ISO protocol is currently in use. (For example, if bit 
     * zero is set, T=0 protocol is in effect.)
     *<li>{@link PCSCDefines#SCARD_ATTR_CURRENT_W SCARD_ATTR_CURRENT_W}: 
     * Current work 
     * waiting time.
     *<li>{@link PCSCDefines#SCARD_ATTR_DEFAULT_CLK SCARD_ATTR_DEFAULT_CLK}: 
     * Default clock 
     * rate, in kHz.
     * <li>{@link PCSCDefines#SCARD_ATTR_DEFAULT_DATA_RATE 
     * SCARD_ATTR_DEFAULT_DATA_RATE}: 
     *	Default data rate, in bps.
     *<li>{@link PCSCDefines#SCARD_ATTR_DEVICE_FRIENDLY_NAME 
     * SCARD_ATTR_DEVICE_FRIENDLY_NAME}: Reader's display name.
     *<li>{@link PCSCDefines#SCARD_ATTR_DEVICE_IN_USE 
     * SCARD_ATTR_DEVICE_IN_USE}: Reserved 
     * for future use.
     *<li>{@link PCSCDefines#SCARD_ATTR_DEVICE_SYSTEM_NAME 
     * SCARD_ATTR_DEVICE_SYSTEM_NAME}: 
     *	Reader's system name.
     *<li>{@link PCSCDefines#SCARD_ATTR_DEVICE_UNIT SCARD_ATTR_DEVICE_UNIT}: 
     *	Instance of this vendor's reader attached to the computer. The first 
     * instance will be	device unit 0, the next will be unit 1 (if it is the 
     * same brand of reader) and so on.<br />
     * Two different brands of readers will both have zero for this value.
     *<li>{@link PCSCDefines#SCARD_ATTR_ICC_INTERFACE_STATUS 
     * SCARD_ATTR_ICC_INTERFACE_STATUS}: Single byte. Zero if smart card 
     * electrical contact is not active; nonzero if contact is active.
     *<li>{@link PCSCDefines#SCARD_ATTR_ICC_PRESENCE SCARD_ATTR_ICC_PRESENCE}: 
     *	Single byte indicating smart card presence:<br /><ul>
     *	<li>0 = not present.</li>
     *	<li>1 = card present but not swallowed (applies only if reader supports 
     * smart card swallowing).</li>
     *	<li>2 = card present (and swallowed if reader supports smart card 
     * swallowing).</li>
     *	<li>4 = card confiscated.</li></ul>
     *<li>{@link PCSCDefines#SCARD_ATTR_ICC_TYPE_PER_ATR 
     * SCARD_ATTR_ICC_TYPE_PER_ATR}: 
     *	Single byte indicating smart card type:<br /><ul>
     *	<li>0 = unknown type.</li>
     *	<li>1 = 7816 Asynchronous.</li>
     *	<li>2 = 7816 Synchronous.</li>
     *	<li>Other values RFU.</li></ul>
     *<li>{@link PCSCDefines#SCARD_ATTR_MAX_CLK SCARD_ATTR_MAX_CLK}: 
     * Maximum clock rate, in 
     * kHz.
     *<li>{@link PCSCDefines#SCARD_ATTR_MAX_DATA_RATE 
     * SCARD_ATTR_MAX_DATA_RATE}: Maximum 
     * data rate, in bps.
     *<li>{@link PCSCDefines#SCARD_ATTR_MAX_IFSD SCARD_ATTR_MAX_IFSD}: 
     * Maximum bytes for 
     * information file size device.
     *<li>{@link PCSCDefines#SCARD_ATTR_POWER_MGMT_SUPPORT 
     * SCARD_ATTR_POWER_MGMT_SUPPORT}: 
     * Zero if device does not support power down while smart card is inserted. 
     * <br />Nonzero otherwise.
     *<li>{@link PCSCDefines#SCARD_ATTR_PROTOCOL_TYPES 
     * SCARD_ATTR_PROTOCOL_TYPES}: 
     * unsigned long encoded as 0x0rrrpppp where rrr is RFU and should be 0x000.
     * <br />pppp encodes the supported protocol types. A '1' in a given bit 
     * position indicates support for the associated ISO protocol, so if bits 
     * zero and one are set, both T=0 and T=1 protocols are supported.
     *<li>{@link PCSCDefines#SCARD_ATTR_VENDOR_IFD_SERIAL_NO 
     * SCARD_ATTR_VENDOR_IFD_SERIAL_NO}: Vendor-supplied interface device serial
     *  number.
     *<li>{@link PCSCDefines#SCARD_ATTR_VENDOR_IFD_TYPE 
     * SCARD_ATTR_VENDOR_IFD_TYPE}: 
     * Vendor-supplied interface device type (model designation of reader).
     *<li>{@link PCSCDefines#SCARD_ATTR_VENDOR_IFD_VERSION 
     * SCARD_ATTR_VENDOR_IFD_VERSION}: 
     * Vendor-supplied interface device version (DWORD in the form 0xMMmmbbbb 
     * where MM = major version, mm = minor version, and bbbb = build number).
     *<li>{@link PCSCDefines#SCARD_ATTR_VENDOR_NAME SCARD_ATTR_VENDOR_NAME}: 
     * Vendor name.
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
    public abstract byte[] getAttrib(int iAttribute) throws CardException;

    /**
     * Sets an attribute of this card.
     *
     * @param iAttribute is the identifier for the attribute to set and must be:
     * <br /><ul><li>{@link PCSCDefines#SCARD_ATTR_SUPRESS_T1_IFS_REQUEST 
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
    public abstract void setAttrib(int iAttribute, byte[] pBCommand)
            throws CardException;

    /**
     * IOCTL for GET_FEATURE_REQUEST
     */
    protected final int IOCTL_GET_FEATURE_REQUEST = SCARD_CTL_CODE(3400);

    /**
     * Hashmap containing the IOCTL keys and values.
     */
    protected Map<Byte, Integer> m_features;

    /**
     * Transmits a modifyPinDirect to the terminal device using the
     * {@linkplain #transmitControlCommand} method.
     * @param abyModifyPin the modify pin command.
     * @return response to this command.
     * @throws CardException if a card operation failed.
     */
    public byte[] modifyPinDirect(byte[] abyModifyPin) throws CardException {
        int _ioctl = 0;

        if (!hasFeature(FEATURE_MODIFY_PIN_DIRECT))
            throw new CardException("org.poreid.pcscforjava."
                    + "Card.modifyPinDirect "
                    + "PCSCException: SCARD_E_UNSUPPORTED_FEATURE");

        _ioctl = this.m_features.get(FEATURE_MODIFY_PIN_DIRECT);
        return(transmitControlCommand(_ioctl, abyModifyPin));
  }

    /**
     * Transmits a verifyPinDirect to the terminal device using the
     * {@linkplain #transmitControlCommand} method.
     * @param abyVerifyPin the verify pin command.
     * @return response to this command.
     * @throws CardException if a card operation failed.
     */
    public byte[] verifyPinDirect(byte[] abyVerifyPin) throws CardException {
        int _ioctl = 0;

        if (!hasFeature(FEATURE_VERIFY_PIN_DIRECT))
            throw new CardException("org.poreid.pcscforjava."
                    + "Card.verifyPinDirect "
                    + "PCSCException: SCARD_E_UNSUPPORTED_FEATURE");

        _ioctl = this.m_features.get(FEATURE_VERIFY_PIN_DIRECT);
        return(transmitControlCommand(_ioctl, abyVerifyPin));
    }
    
    /**
     * Gets the TLV properties of the terminal.
     * @return the TLV properties of the terminal.
     * @throws CardException if a card operation failed.
     */
    public byte[] getTlvProperties() throws CardException {
        int _ioctl = 0;

        if (!hasFeature(FEATURE_GET_TLV_PROPERTIES))
            throw new CardException("org.poreid.pcscforjava."
                    + "Card.getTlvProperties "
                    + "PCSCException: SCARD_E_UNSUPPORTED_FEATURE");

        _ioctl = this.m_features.get(FEATURE_GET_TLV_PROPERTIES);
        return(transmitControlCommand(_ioctl, new byte[0]));
    }
    
    /**
     * Aborts the current CCID command.
     * @return the response of the terminal to the abort command.
     * @throws CardException if a card operation failed.
     */
    public byte[] abort() throws CardException {
        int _ioctl = 0;

        if (!hasFeature(FEATURE_ABORT))
            throw new CardException("org.poreid.pcscforjava."
                    + "Card.abort "
                    + "PCSCException: SCARD_E_UNSUPPORTED_FEATURE");

        _ioctl = this.m_features.get(FEATURE_ABORT);
        return(transmitControlCommand(_ioctl, new byte[0]));
    }
    
    /**
     * Gets the control code for a feature.
     * @param BFeature the feature. It can be:
     * <br /><ul><li>{@link PCSCDefines#FEATURE_VERIFY_PIN_START 
     * FEATURE_VERIFY_PIN_START}</li>
     * <li>{@link PCSCDefines#FEATURE_VERIFY_PIN_FINISH 
     * FEATURE_VERIFY_PIN_FINISH}</li>
     * <li>{@link PCSCDefines#FEATURE_MODIFY_PIN_START 
     * FEATURE_MODIFY_PIN_START}</li>
     * <li>{@link PCSCDefines#FEATURE_MODIFY_PIN_FINISH 
     * FEATURE_MODIFY_PIN_FINISH}</li>
     * <li>{@link PCSCDefines#FEATURE_GET_KEY_PRESSED 
     * FEATURE_GET_KEY_PRESSED}</li>
     * <li>{@link PCSCDefines#FEATURE_MODIFY_PIN_DIRECT 
     * FEATURE_MODIFY_PIN_DIRECT}</li>
     * <li>{@link PCSCDefines#FEATURE_MCT_READER_DIRECT 
     * FEATURE_MCT_READER_DIRECT}</li>
     * <li>{@link PCSCDefines#FEATURE_MCT_UNIVERSAL 
     * FEATURE_MCT_UNIVERSAL}</li>
     * <li>{@link PCSCDefines#FEATURE_IFD_PIN_PROPERTIES 
     * FEATURE_IFD_PIN_PROPERTIES}</li>
     * <li>{@link PCSCDefines#FEATURE_ABORT 
     * FEATURE_ABORT}</li>
     * <li>{@link PCSCDefines#FEATURE_SET_SPE_MESSAGE 
     * FEATURE_SET_SPE_MESSAGE}</li>
     * <li>{@link PCSCDefines#FEATURE_VERIFY_PIN_DIRECT_APP_ID 
     *      FEATURE_VERIFY_PIN_DIRECT_APP_ID}</li>
     * <li>{@link PCSCDefines#FEATURE_MODIFY_PIN_DIRECT_APP_ID 
     *      FEATURE_MODIFY_PIN_DIRECT_APP_ID}</li>
     * <li>{@link PCSCDefines#FEATURE_WRITE_DISPLAY FEATURE_WRITE_DISPLAY}</li> 
     * <li>{@link PCSCDefines#FEATURE_GET_KEY FEATURE_GET_KEY}</li> 
     * <li>{@link PCSCDefines#FEATURE_IFD_DISPLAY_PROPERTIES 
     *      FEATURE_IFD_DISPLAY_PROPERTIES}</li> 
     * <li>{@link PCSCDefines#FEATURE_GET_TLV_PROPERTIES 
     * FEATURE_GET_TLV_PROPERTIES}</li>  
     * <li>{@link PCSCDefines#FEATURE_CCID_ESC_COMMAND 
     * FEATURE_CCID_ESC_COMMAND}</li></ul>
     * @return the control code for the feature.
     * @throws CardException if a card operation failed.
     */
    public int getControlCodeForFeature(byte BFeature) throws CardException
    {
        if (!hasFeature(BFeature))
            throw new CardException("org.poreid.pcscforjava."
                    + "Card.verifyPinDirect "
                    + "PCSCException: SCARD_E_UNSUPPORTED_FEATURE");
        
        return this.m_features.get(BFeature);
    }

    /**
     * Allows knowing if a feature is supported by the terminal.
     * @param feature the feature.
     * @return true if supported.
     * @throws CardException if a card operation failed.
     */
    protected boolean hasFeature(Byte feature) throws CardException {
        if (this.m_features != null)
            return this.m_features.containsKey(feature);
        else
        {
            queryFeatures();
            return this.m_features.containsKey(feature);
        }
    }

    /**
     * Retrieves the available features and put it in a hashmap.
     * @throws CardException if a card operation failed.
     */
    protected void queryFeatures() throws CardException {
        this.m_features = new HashMap<Byte, Integer>();
        byte[] _abyResponse = transmitControlCommand(IOCTL_GET_FEATURE_REQUEST,
                new byte[0]);

        // tag
        // length in bytes (always 4)
        // control code value for supported feature (in big endian)
        for (int i = 0; i < _abyResponse.length; i += 6)
        {
            Byte feature = new Byte(_abyResponse[i]);
            int ioctlBigEndian = ((0xff & _abyResponse[i + 2]) << 24) |
                  ((0xff & _abyResponse[i + 3]) << 16) |
                  ((0xff & _abyResponse[i + 4]) << 8) |
                  (0xff & _abyResponse[i + 5]);
            Integer ioctl = new Integer(ioctlBigEndian);
            this.m_features.put(feature, ioctl);
        }
    }

    /**
     * Returns the sharing mode of the smart card connection.
     * 
     * @return the sharing mode of the smart card connection. 
     * It can be:<br /><ul>
     * <li>{@link PCSCDefines#SCARD_SHARE_EXCLUSIVE SCARD_SHARE_EXCLUSIVE} 
     * for exclusive 
     * mode.</li>
     * <li>{@link PCSCDefines#SCARD_SHARE_DIRECT SCARD_SHARE_DIRECT} 
     * for direct mode.</li>
     * <li>{@link PCSCDefines#SCARD_SHARE_SHARED SCARD_SHARE_SHARED} 
     * for share mode.</li>
     * </ul>
     */
    public abstract int getSharingMode();   
    
    public abstract boolean isValid();
}
