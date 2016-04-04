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

/**
 * Access to native PC/SC functions and definition of PC/SC constants.
 * Initialization and platform specific PC/SC constants are handled in
 * the platform specific superclass.
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  Matthieu Leromain
 */
final class PCSC extends PlatformPCSC {

    private PCSC() {
        // no instantiation
    }

    /**
     * Check if the PC/SC functions are available on this platform.
     * @throws RuntimeException if the PC/SC functions are not available on this
     * platform.
     */
    static void checkAvailable() throws RuntimeException {
        if (initException != null) {
            throw new UnsupportedOperationException
                    ("PC/SC not available on this platform", initException);
        }
    }

    /**
     * The SCardEstablishContext function establishes the resource manager 
     * context (the scope) within which database operations are performed.
     * @param scope Scope of the resource manager context.<br />
     * This parameter can be one of the following values.<ul>
     * <li>SCARD_SCOPE_USER: Database operations are performed within the domain
     * of the user.</li>
     * <li>SCARD_SCOPE_SYSTEM: Database operations are performed within the 
     * domain of the system. The calling application must have appropriate 
     * access permissions for any database actions.</li></ul>
     * @return Handle that identifies the resource manager context.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native long SCardEstablishContext
            (int scope)
            throws PCSCException;

    /**
     * The SCardReleaseContext function closes an established resource manager 
     * context, freeing any resources allocated under that context, including 
     * SCARDHANDLE objects and memory allocated using the SCARD_AUTOALLOCATE 
     * length designator.
     * @param lContextId Handle that identifies the resource manager context. 
     * The resource manager context can be set by a previous call to 
     * SCardEstablishContext.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native void SCardReleaseContext
            (long lContextId)
            throws PCSCException;

    /**
     * The SCardCancel function terminates all outstanding actions within a 
     * specific resource manager context.<br /><br />
     * The only requests that you can cancel are those that require waiting for 
     * external action by the smart card or user. Any such outstanding action 
     * requests will terminate with a status indication that the action was 
     * canceled. This is especially useful to force outstanding 
     * SCardGetStatusChange calls to terminate.
     * @param lContextId Handle that identifies the resource manager context. 
     * The resource manager context is set by a previous call to 
     * SCardEstablishContext.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native void SCardCancel
            (long lContextId)
            throws PCSCException;
    
    /**
     * The SCardIsValidContext function determines whether a smart card context 
     * handle is valid.
     * @param lContextId Handle that identifies the resource manager context. 
     * The resource manager context can be set by a previous call to 
     * SCardEstablishContext.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native void SCardIsValidContext
            (long lContextId)
            throws PCSCException;

    /**
     * The SCardListReaders function provides the list of readers within a set 
     * of named reader groups, eliminating duplicates.<br /><br />
     * The caller supplies a list of reader groups, and receives the list of 
     * readers within the named groups. Unrecognized group names are ignored. 
     * This function only returns readers within the named groups that are 
     * currently attached to the system and available for use.
     * @param contextId Handle that identifies the resource manager context. 
     * The resource manager context can be set by a previous call to 
     * SCardEstablishContext.
     * @return An array of readers string names.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native String[] SCardListReaders
            (long contextId)
            throws PCSCException;

    /**
     * The SCardConnect function establishes a connection (using a specific 
     * resource manager context) between the calling application and a smart 
     * card contained by a specific reader. If no card exists in the specified 
     * reader, an error is returned.
     * @param contextId A handle that identifies the resource manager context. 
     * The resource manager context is set by a previous call to 
     * SCardEstablishContext.
     * @param readerName The name of the reader that contains the target card.
     * @param shareMode A flag that indicates whether other applications may 
     * form connections to the card.<ul>
     * <li>SCARD_SHARE_SHARED: This application is willing to share the card 
     * with other applications.</li>
     * <li>SCARD_SHARE_EXCLUSIVE: This application is not willing to share the 
     * card with other applications.</li>
     * <li>SCARD_SHARE_DIRECT: This application is allocating the reader for its
     * private use, and will be controlling it directly. No other applications 
     * are allowed access to it.</li></ul>
     * @param preferredProtocols A bitmask of acceptable protocols for the 
     * connection. Possible values may be combined with the OR operation.<ul>
     * <li>SCARD_PROTOCOL_T0: T=0 is an acceptable protocol.</li>
     * <li>SCARD_PROTOCOL_T1: T=1 is an acceptable protocol.</li>
     * <li>0: This parameter may be zero only if dwShareMode is set to 
     * SCARD_SHARE_DIRECT. In this case, no protocol negotiation will be 
     * performed by the drivers until an IOCTL_SMARTCARD_SET_PROTOCOL control 
     * directive is sent with SCardControl.</li></ul>
     * @return Handle that identifies the connection to the smart card in the 
     * designated reader.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native long SCardConnect
            (long contextId, String readerName, int shareMode, 
            int preferredProtocols) throws PCSCException;
    
    /**
     * The SCardReconnect function reestablishes an existing connection between 
     * the calling application and a smart card. This function moves a card 
     * handle from direct access to general access, or acknowledges and clears 
     * an error condition that is preventing further access to the card.
     * @param lCardHandle Reference value obtained from a previous call to 
     * SCardConnect.
     * @param iShareMode Flag that indicates whether other applications may 
     * form connections to this card.
     * <ul>
     * <li>SCARD_SHARE_SHARED: This application is willing to share the card 
     * with other applications.</li>
     * <li>SCARD_SHARE_EXCLUSIVE: This application is not willing to share the 
     * card with other applications.</li></ul>
     * @param iPreferredProtocols A bitmask of acceptable protocols for the 
     * connection. Possible values may be combined with the OR operation.<ul>
     * <li>SCARD_PROTOCOL_T0: T=0 is an acceptable protocol.</li>
     * <li>SCARD_PROTOCOL_T1: T=1 is an acceptable protocol.</li></ul>
     * @param iInitialization Type of initialization that should be performed 
     * on the card.<ul>
     * <li>SCARD_LEAVE_CARD: Do not do anything special on reconnect.</li>
     * <li>SCARD_RESET_CARD: Reset the card (Warm Reset).</li>
     * <li>SCARD_UNPOWER_CARD: Power down the card and reset it (Cold Reset).
     * </li></ul>
     * @return The Answer To Reset smart card response.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native byte[] SCardReconnect
            (long lCardHandle, int iShareMode, int iPreferredProtocols, 
            int iInitialization)
            throws PCSCException;

    /**
     * The SCardTransmit function sends a service request to the smart card and 
     * expects to receive data back from the card.
     * @param cardId A reference value returned from the SCardConnect function.
     * @param protocol A bitmask of acceptable protocols for the 
     * connection. Possible values may be combined with the OR operation.<ul>
     * <li>SCARD_PROTOCOL_T0: T=0 is an acceptable protocol.</li>
     * <li>SCARD_PROTOCOL_T1: T=1 is an acceptable protocol.</li>
     * <li>0: This parameter may be zero only if dwShareMode is set to 
     * SCARD_SHARE_DIRECT. In this case, no protocol negotiation will be 
     * performed by the drivers until an IOCTL_SMARTCARD_SET_PROTOCOL control 
     * directive is sent with SCardControl.</li></ul>
     * @param buf The command to send to the smart card.
     * @param ofs The offset from which the command must be sent to the smart 
     * card.
     * @param len The length of the command.
     * @return The smart card response.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native byte[] SCardTransmit
            (long cardId, int protocol, byte[] buf, int ofs, int len)
            throws PCSCException;

    /**
     * The SCardStatus function provides the current status of a smart card in 
     * a reader. You can call it any time after a successful call to 
     * SCardConnect and before a successful call to SCardDisconnect. It does 
     * not affect the state of the reader or reader driver.
     * @param cardId Reference value returned from SCardConnect.
     * @param status Current state of the smart card. This variable is in/out.
     * <ul><li>SCARD_ABSENT: There is no card in the reader.</li>
     * <li>SCARD_PRESENT: There is a card in the reader, but it has not been 
     * moved into position for use.</li>
     * <li>SCARD_SWALLOWED: There is a card in the reader in position for use. 
     * The card is not powered.</li>
     * <li>SCARD_POWERED: Power is being provided to the card, but the reader 
     * driver is unaware of the mode of the card.</li>
     * <li>SCARD_NEGOTIABLE: The card has been reset and is awaiting PTS 
     * negotiation.</li>
     * <li>SCARD_SPECIFIC: The card has been reset and specific communication 
     * protocols have been established.</li></ul>
     * @return The Answer To Reset of the smart card.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static byte[] SCardStatus(long cardId, byte[] status) throws PCSCException
    {
        String[]  _psReaderName = new String[1];
        byte [] _pBResponse = SCardStatus(cardId, status, _psReaderName);
        return _pBResponse;
    }

    /**
     * The SCardStatus function provides the current status of a smart card in 
     * a reader. You can call it any time after a successful call to 
     * SCardConnect and before a successful call to SCardDisconnect. It does 
     * not affect the state of the reader or reader driver.
     * @param cardId Reference value returned from SCardConnect.
     * @param status Current state of the smart card. This variable is in/out.
     * <ul><li>SCARD_ABSENT: There is no card in the reader.</li>
     * <li>SCARD_PRESENT: There is a card in the reader, but it has not been 
     * moved into position for use.</li>
     * <li>SCARD_SWALLOWED: There is a card in the reader in position for use. 
     * The card is not powered.</li>
     * <li>SCARD_POWERED: Power is being provided to the card, but the reader 
     * driver is unaware of the mode of the card.</li>
     * <li>SCARD_NEGOTIABLE: The card has been reset and is awaiting PTS 
     * negotiation.</li>
     * <li>SCARD_SPECIFIC: The card has been reset and specific communication 
     * protocols have been established.</li></ul>
     * @param psReaderName the names of the readers which you want to check the
     * status.
     * @return The Answer To Reset of the smart card.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native byte[] SCardStatus
            (long cardId, byte[] status, String[] psReaderName)
            throws PCSCException;

    /**
     * The SCardDisconnect function terminates a connection previously opened 
     * between the calling application and a smart card in the target reader.
     * @param cardId Reference value obtained from a previous call to 
     * SCardConnect.
     * @param disposition Action to take on the card in the connected reader on 
     * close. <ul>
     * <li>SCARD_LEAVE_CARD: Do not do anything special.</li>
     * <li>SCARD_RESET_CARD: Reset the card.</li>
     * <li>SCARD_UNPOWER_CARD: Power down the card.</li>
     * <li>SCARD_EJECT_CARD: Eject the card.</li></ul>    
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native void SCardDisconnect
            (long cardId, int disposition)
            throws PCSCException;

    /**
     * The SCardGetStatusChange function blocks execution until the current 
     * availability of the cards in a specific set of readers changes.<br />
     * <br />
     * The caller supplies a list of readers to be monitored by an 
     * SCARD_READERSTATE array and the maximum amount of time (in milliseconds) 
     * that it is willing to wait for an action to occur on one of the listed 
     * readers. Note that SCardGetStatusChange uses the user-supplied value in 
     * the dwCurrentState members of the rgReaderStates SCARD_READERSTATE array 
     * as the definition of the current state of the readers. The function 
     * returns when there is a change in availability, having filled in the 
     * dwEventState members of rgReaderStates appropriately.
     * @param contextId A handle that identifies the resource manager context. 
     * The resource manager context is set by a previous call to the 
     * SCardEstablishContext function.
     * @param timeout The maximum amount of time, in milliseconds, to wait for 
     * an action. A value of zero causes the function to return immediately. A 
     * value of INFINITE causes this function never to time out.
     * @param currentState Current state of the smart card. This variable is 
     * in/out.
     * <ul><li>SCARD_ABSENT: There is no card in the reader.</li>
     * <li>SCARD_PRESENT: There is a card in the reader, but it has not been 
     * moved into position for use.</li>
     * <li>SCARD_SWALLOWED: There is a card in the reader in position for use. 
     * The card is not powered.</li>
     * <li>SCARD_POWERED: Power is being provided to the card, but the reader 
     * driver is unaware of the mode of the card.</li>
     * <li>SCARD_NEGOTIABLE: The card has been reset and is awaiting PTS 
     * negotiation.</li>
     * <li>SCARD_SPECIFIC: The card has been reset and specific communication 
     * protocols have been established.</li></ul>
     * @param readerNames the names of the readers which you want to check the
     * status.
     * @return dwEventState[] of the same size and order as readerNames[].
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native int[] SCardGetStatusChange
            (long contextId, long timeout, int[] currentState, 
            String[] readerNames) throws PCSCException;
    
    /**
     * The SCardBeginTransaction function starts a transaction.<br /><br />
     * The function waits for the completion of all other transactions before it 
     * begins. After the transaction starts, all other applications are blocked 
     * from accessing the smart card while the transaction is in progress.
     * @param cardId A reference value obtained from a previous call to 
     * SCardConnect.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native void SCardBeginTransaction
            (long cardId)
            throws PCSCException;

    /**
     * The SCardEndTransaction function completes a previously declared 
     * transaction, allowing other applications to resume interactions with the 
     * card.
     * @param cardId A reference value obtained from a previous call to 
     * SCardConnect.
     * @param disposition Action to take on the card in the connected reader on 
     * close. <ul>
     * <li>SCARD_LEAVE_CARD: Do not do anything special.</li>
     * <li>SCARD_RESET_CARD: Reset the card.</li>
     * <li>SCARD_UNPOWER_CARD: Power down the card.</li>
     * <li>SCARD_EJECT_CARD: Eject the card.</li></ul>    
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native void SCardEndTransaction
            (long cardId, int disposition)
            throws PCSCException;
    
    /**
     * The SCardControl function gives you direct control of the reader. You 
     * can call it any time after a successful call to SCardConnect and before 
     * a successful call to SCardDisconnect. The effect on the state of the 
     * reader depends on the control code.
     * @param cardId Reference value returned from SCardConnect.
     * @param controlCode Control code for the operation. This value identifies 
     * the specific operation to be performed.
     * @param sendBuffer Buffer that contains the data required to perform the 
     * operation. This parameter can be NULL if the dwControlCode parameter 
     * specifies an operation that does not require input data.
     * @return Response of the reader.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native byte[] SCardControl
            (long cardId, int controlCode, byte[] sendBuffer)
            throws PCSCException;
    
    /**
     * The SCardGetAttrib function retrieves the current reader attributes for 
     * the given handle. It does not affect the state of the reader, driver, 
     * or card.
     * @param lCardId Reference value returned from SCardConnect.
     * @param iAttribute Identifier for the attribute to get. The following 
     * table lists possible values for dwAttrId. These values are read-only. 
     * Note that vendors may not support all attributes. 
     * <li>{@link SCARD_ATTR_ATR_STRING SCARD_ATTR_ATR_STRING}: Answer to reset 
     * (ATR) string.</li>
     * <li>{@link SCARD_ATTR_CHANNEL_ID SCARD_ATTR_CHANNEL_ID}: 
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
     * <li>{@link SCARD_ATTR_CHARACTERISTICS SCARD_ATTR_CHARACTERISTICS}: 
     *  unsigned long indicating which mechanical characteristics are supported.
     * <br />If zero, no special characteristics are supported.<br />
     * Note that multiple bits can be set:<br /><ul>
     *  <li>0x00000001 Card swallowing mechanism.</li>
     *	<li>0x00000002 Card ejection mechanism.</li>
     *	<li>0x00000004 Card capture mechanism.</li>
     *	<li>All other values are reserved for future use (RFU).</li></ul>
     * <li>{@link SCARD_ATTR_CURRENT_BWT SCARD_ATTR_CURRENT_BWT}: Current block 
     *  waiting time.
     * <li>{@link SCARD_ATTR_CURRENT_CLK SCARD_ATTR_CURRENT_CLK}: Current clock 
     * rate, in kHz.
     * <li>{@link SCARD_ATTR_CURRENT_CWT SCARD_ATTR_CURRENT_CWT}: Current 
     * character waiting time.
     * <li>{@link SCARD_ATTR_CURRENT_D SCARD_ATTR_CURRENT_D}: Bit rate conversion 
     * factor.
     * <li>{@link SCARD_ATTR_CURRENT_EBC_ENCODING 
     * SCARD_ATTR_CURRENT_EBC_ENCODING}: 
     *	Current error block control encoding.<br /><ul>
     *	<li>0 = Longitudinal Redundancy Check (LRC).</li>
     *	<li>1 = Cyclical Redundancy Check (CRC).</li></ul>
     * <li>{@link SCARD_ATTR_CURRENT_F SCARD_ATTR_CURRENT_F}: Clock conversion 
     * factor.
     * <li>{@link SCARD_ATTR_CURRENT_IFSC SCARD_ATTR_CURRENT_IFSC}: Current byte 
     * size for information field size card.
     * <li>{@link SCARD_ATTR_CURRENT_IFSD SCARD_ATTR_CURRENT_IFSD}: Current byte 
     * size for information field size device.
     * <li>{@link SCARD_ATTR_CURRENT_N SCARD_ATTR_CURRENT_N}: Current guard time.
     * <li>{@link SCARD_ATTR_CURRENT_PROTOCOL_TYPE 
     * SCARD_ATTR_CURRENT_PROTOCOL_TYPE}: 
     * 	unsigned long encoded as 0x0rrrpppp where rrr is RFU and should be 
     *  0x000.<br />
     *	pppp encodes the current protocol type. Whichever bit has been set 
     * indicates which ISO protocol is currently in use. (For example, if bit 
     * zero is set, T=0 protocol is in effect.)
     * <li>{@link SCARD_ATTR_CURRENT_W SCARD_ATTR_CURRENT_W}: Current work 
     * waiting time.
     * <li>{@link SCARD_ATTR_DEFAULT_CLK SCARD_ATTR_DEFAULT_CLK}: Default clock 
     * rate, in kHz.
     * <li>{@link SCARD_ATTR_DEFAULT_DATA_RATE SCARD_ATTR_DEFAULT_DATA_RATE}: 
     *	Default data rate, in bps.
     * <li>{@link SCARD_ATTR_DEVICE_FRIENDLY_NAME 
     * SCARD_ATTR_DEVICE_FRIENDLY_NAME}: Reader's display name.
     * <li>{@link SCARD_ATTR_DEVICE_IN_USE SCARD_ATTR_DEVICE_IN_USE}: Reserved 
     * for future use.
     * <li>{@link SCARD_ATTR_DEVICE_SYSTEM_NAME SCARD_ATTR_DEVICE_SYSTEM_NAME}: 
     *	Reader's system name.
     * <li>{@link SCARD_ATTR_DEVICE_UNIT SCARD_ATTR_DEVICE_UNIT}: 
     *	Instance of this vendor's reader attached to the computer. The first 
     * instance will be	device unit 0, the next will be unit 1 (if it is the 
     * same brand of reader) and so on.<br />
     * Two different brands of readers will both have zero for this value.
     * <li>{@link SCARD_ATTR_ICC_INTERFACE_STATUS 
     * SCARD_ATTR_ICC_INTERFACE_STATUS}: Single byte. Zero if smart card 
     * electrical contact is not active; nonzero if contact is active.
     * <li>{@link SCARD_ATTR_ICC_PRESENCE SCARD_ATTR_ICC_PRESENCE}: 
     *	Single byte indicating smart card presence:<br /><ul>
     *	<li>0 = not present.</li>
     *	<li>1 = card present but not swallowed (applies only if reader supports 
     * smart card swallowing).</li>
     *	<li>2 = card present (and swallowed if reader supports smart card 
     * swallowing).</li>
     *	<li>4 = card confiscated.</li></ul>
     * <li>{@link SCARD_ATTR_ICC_TYPE_PER_ATR SCARD_ATTR_ICC_TYPE_PER_ATR}: 
     *	Single byte indicating smart card type:<br /><ul>
     *	<li>0 = unknown type.</li>
     *	<li>1 = 7816 Asynchronous.</li>
     *	<li>2 = 7816 Synchronous.</li>
     *	<li>Other values RFU.</li></ul>
     * <li>{@link SCARD_ATTR_MAX_CLK SCARD_ATTR_MAX_CLK}: Maximum clock rate, in 
     * kHz.
     * <li>{@link SCARD_ATTR_MAX_DATA_RATE SCARD_ATTR_MAX_DATA_RATE}: Maximum 
     * data rate, in bps.
     * <li>{@link SCARD_ATTR_MAX_IFSD SCARD_ATTR_MAX_IFSD}: Maximum bytes for 
     * information file size device.
     * <li>{@link SCARD_ATTR_POWER_MGMT_SUPPORT SCARD_ATTR_POWER_MGMT_SUPPORT}: 
     * Zero if device does not support power down while smart card is inserted. 
     * <br />Nonzero otherwise.
     * <li>{@link SCARD_ATTR_PROTOCOL_TYPES SCARD_ATTR_PROTOCOL_TYPES}: 
     * unsigned long encoded as 0x0rrrpppp where rrr is RFU and should be 0x000.
     * <br />pppp encodes the supported protocol types. A '1' in a given bit 
     * position indicates support for the associated ISO protocol, so if bits 
     * zero and one are set, both T=0 and T=1 protocols are supported.
     * <li>{@link SCARD_ATTR_VENDOR_IFD_SERIAL_NO 
     * SCARD_ATTR_VENDOR_IFD_SERIAL_NO}: Vendor-supplied interface device serial
     *  number.
     * <li>{@link SCARD_ATTR_VENDOR_IFD_TYPE SCARD_ATTR_VENDOR_IFD_TYPE}: 
     * Vendor-supplied interface device type (model designation of reader).
     * <li>{@link SCARD_ATTR_VENDOR_IFD_VERSION SCARD_ATTR_VENDOR_IFD_VERSION}: 
     * Vendor-supplied interface device version (DWORD in the form 0xMMmmbbbb 
     * where MM = major version, mm = minor version, and bbbb = build number).
     * <li>{@link SCARD_ATTR_VENDOR_NAME SCARD_ATTR_VENDOR_NAME}: Vendor name.
     * </ul>
     * @return the current reader attribute.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native byte[] SCardGetAttrib
            (long lCardId, int iAttribute)
            throws PCSCException;
    
    /**
     * The SCardSetAttrib function sets the given reader attribute for the 
     * given handle. It does not affect the state of the reader, reader driver, 
     * or smart card. Not all attributes are supported by all readers (nor can 
     * they be set at all times) as many of the attributes are under direct 
     * control of the transport protocol.
     * @param lCardId Reference value returned from SCardConnect.
     * @param iAttribute Identifier for the attribute to set. The values are 
     * write-only. Note that vendors may not support all attributes. 
     * @param pBCommand Pointer to a buffer that supplies the attribute whose 
     * ID is supplied in dwAttrId.
     * @throws PCSCException if a PC/SC exception occurs.
     */
    static native void SCardSetAttrib
            (long lCardId, int iAttribute, byte[] pBCommand)
            throws PCSCException;

    // Infinite timeout
    final static int TIMEOUT_INFINITE = 0xffffffff;

    private final static char[] hexDigits = "0123456789abcdef".toCharArray();
    
    /**
     * Returns string description of byte array.
     * @param b the byte array.
     * @return string description of byte array.
     */
    public static String toString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 3);
        for (int i = 0; i < b.length; i++) {
            int k = b[i] & 0xff;
            if (i != 0) {
                sb.append(':');
            }
            sb.append(hexDigits[k >>> 4]);
            sb.append(hexDigits[k & 0xf]);
        }
        return sb.toString();
    }

}
