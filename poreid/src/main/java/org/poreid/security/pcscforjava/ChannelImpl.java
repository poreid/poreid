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

import org.poreid.pcscforjava.CardChannel;
import org.poreid.pcscforjava.CommandAPDU;
import org.poreid.pcscforjava.CardException;
import org.poreid.pcscforjava.ResponseAPDU;
import org.poreid.pcscforjava.Card;
import java.nio.*;
import java.security.AccessController;


import static org.poreid.security.pcscforjava.PCSC.*;

import org.poreid.pcscforjava.sun.internal.GetPropertyAction;
//import sun.security.action.GetPropertyAction;

/**
 * CardChannel implementation.
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  Matthieu Leromain
 */
final class ChannelImpl extends CardChannel {

    // the card this channel is associated with
    private final CardImpl card;

    // the channel number, 0 for the basic logical channel
    private final int channel;

    // whether this channel has been closed. only logical channels can be closed
    private volatile boolean isClosed;

    /**
     * Constructs a new CardChannel object.
     *
     * <p>This constructor is called by subclasses only. Application should
     * call the {@linkplain Card#getBasicChannel} and
     * {@linkplain Card#openLogicalChannel} methods to obtain a CardChannel
     * object.
     */
    ChannelImpl(CardImpl card, int channel) {
        this.card = card;
        this.channel = channel;
    }
    
    /**
     * Check the close of the logical channel.
     */
    void checkClosed() {
        card.checkState();
        if (isClosed) {
            throw new IllegalStateException("Logical channel has been closed");
        }
    }

    /**
     * Returns the Card this channel is associated with.
     *
     * @return the Card this channel is associated with
     */
    public Card getCard() {
        return card;
    }

    /**
     * Returns the channel number of this CardChannel. A channel number of
     * 0 indicates the basic logical channel.
     *
     * @return the channel number of this CardChannel.
     *
     * @throws IllegalStateException if this channel has been
     *   {@linkplain #close closed} or if the corresponding Card has been
     *   {@linkplain Card#disconnect disconnected}.
     */
    public int getChannelNumber() {
        checkClosed();
        return channel;
    }
    
    /**
     * Check a manage channel.
     * @param b the command APDU.
     */
    private static void checkManageChannel(byte[] b) {
        if (b.length < 4) {
            throw new IllegalArgumentException
                ("Command APDU must be at least 4 bytes long");
        }
        if ((b[0] >= 0) && (b[1] == 0x70)) {
            throw new IllegalArgumentException
                ("Manage channel command not allowed, use openLogicalChannel()");
        }
    }

    /**
     * Transmits the specified command APDU to the Smart Card and returns the
     * response APDU.
     *
     * <p>The CLA byte of the command APDU is automatically adjusted to
     * match the channel number of this CardChannel.
     *
     * <p>Note that this method cannot be used to transmit
     * <code>MANAGE CHANNEL</code> APDUs. Logical channels should be managed
     * using the {@linkplain Card#openLogicalChannel} and {@linkplain
     * CardChannel#close CardChannel.close()} methods.
     *
     * <p>Implementations should transparently handle artifacts
     * of the transmission protocol.
     * For example, when using the T=0 protocol, the following processing
     * should occur as described in ISO/IEC 7816-4:
     *
     * <ul>
     * <li><p>if the response APDU has an SW1 of <code>61</code>, the
     * implementation should issue a <code>GET RESPONSE</code> command
     * using <code>SW2</code> as the <code>Le</code>field.
     * This process is repeated as long as an SW1 of <code>61</code> is
     * received. The response body of these exchanges is concatenated
     * to form the final response body.<br />
     * Warning the automatic <code>GET RESPONSE</code> is used with a CLA of 
     * 0x00. If you do not want to perform a <code>GET RESPONSE</code> with
     * a CLA of 0x00 then do not use the automatic mode.
     *
     * <li><p>if the response APDU is <code>6C XX</code>, the implementation
     * should reissue the command using <code>XX</code> as the
     * <code>Le</code> field.
     * </ul>
     *
     * <p>If the parameters are setted to true then the ResponseAPDU returned by
     * this method is the result after this processing has been performed.
     *
     * @param command the command APDU
     * @param bAutoGetResp indicates if the automatic get response must be
     * performed or not.
     * @param bAutoReissue indicates if the automatic reissue must be performed
     * or not.
     * @return the response APDU received from the card
     *
     * @throws IllegalStateException if this channel has been
     *   {@linkplain #close closed} or if the corresponding Card has been
     *   {@linkplain Card#disconnect disconnected}.
     * @throws IllegalArgumentException if the APDU encodes a
     *   <code>MANAGE CHANNEL</code> command
     * @throws NullPointerException if command is null
     * @throws CardException if the card operation failed
     */
    public ResponseAPDU transmit(CommandAPDU command, boolean bAutoGetResp,
            boolean bAutoReissue) throws CardException {
        checkClosed();
        card.checkExclusive();
        byte[] commandBytes = command.getBytes();
        byte[] responseBytes = doTransmit(commandBytes, bAutoGetResp,
                bAutoReissue);
        return new ResponseAPDU(responseBytes);
    }

    /**
     * Transmits the command APDU stored in the command ByteBuffer and receives
     * the reponse APDU in the response ByteBuffer.
     *
     * <p>The command buffer must contain valid command APDU data starting
     * at <code>command.position()</code> and the APDU must be
     * <code>command.remaining()</code> bytes long.
     * Upon return, the command buffer's position will be equal
     * to its limit; its limit will not have changed. The output buffer
     * will have received the response APDU bytes. Its position will have
     * advanced by the number of bytes received, which is also the return
     * value of this method.
     *
     * <p>The CLA byte of the command APDU is automatically adjusted to
     * match the channel number of this CardChannel.
     *
     * <p>Note that this method cannot be used to transmit
     * <code>MANAGE CHANNEL</code> APDUs. Logical channels should be managed
     * using the {@linkplain Card#openLogicalChannel} and {@linkplain
     * CardChannel#close CardChannel.close()} methods.
     *
     * <p>See {@linkplain #transmit transmit()} for a discussion of the handling
     * of response APDUs with the SW1 values <code>61</code> or <code>6C</code>.
     *
     * @param command the buffer containing the command APDU
     * @param bAutoGetResp indicates if the automatic get response must be
     * performed or not.
     * @param bAutoReissue indicates if the automatic reissue must be performed
     * or not.
     * @param response the buffer that shall receive the response APDU from
     *   the card
     * @return the length of the received response APDU
     *
     * @throws IllegalStateException if this channel has been
     *   {@linkplain #close closed} or if the corresponding Card has been
     *   {@linkplain Card#disconnect disconnected}.
     * @throws NullPointerException if command or response is null
     * @throws ReadOnlyBufferException if the response buffer is read-only
     * @throws IllegalArgumentException if command and response are the
     *   same object, if <code>response</code> may not have
     *   sufficient space to receive the response APDU
     *   or if the APDU encodes a <code>MANAGE CHANNEL</code> command
     * @throws CardException if the card operation failed
     */
    public int transmit(ByteBuffer command, boolean bAutoGetResp,
            boolean bAutoReissue, ByteBuffer response) throws CardException {
        checkClosed();
        card.checkExclusive();
        if ((command == null) || (response == null)) {
            throw new NullPointerException();
        }
        if (response.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        if (command == response) {
            throw new IllegalArgumentException
                    ("command and response must not be the same object");
        }
        if (response.remaining() < 258) {
            throw new IllegalArgumentException
                    ("Insufficient space in response buffer");
        }
        byte[] commandBytes = new byte[command.remaining()];
        command.get(commandBytes);
        byte[] responseBytes = doTransmit(commandBytes, bAutoGetResp,
                bAutoReissue);
        response.put(responseBytes);
        return responseBytes.length;
    }

    private final static boolean t0GetResponse =
        getBooleanProperty("org.poreid.security.pcscforjava.t0GetResponse",
        true);

    private final static boolean t1GetResponse =
        getBooleanProperty("org.poreid.security.pcscforjava.t1GetResponse",
        true);

    private final static boolean t1StripLe =
        getBooleanProperty("org.poreid.security.pcscforjava.t1StripLe",
        false);

    /**
     * Returns the boolean property.
     * @param name the name of the action.
     * @param def the default return value.
     * @return true or false.
     */
    private static boolean getBooleanProperty(String name, boolean def) {
        String val = AccessController.doPrivileged(new GetPropertyAction(name));
        if (val == null) {
            return def;
        }
        if (val.equalsIgnoreCase("true")) {
            return true;
        } else if (val.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new IllegalArgumentException
                (name + " must be either 'true' or 'false'");
        }
    }

    /**
     * Concat two array bytes.
     * @param b1 the first array byte to concat.
     * @param b2 the second array byte to concat.
     * @param n2 the length of the second array byte to concat.
     * @return The concatenation.
     */
    private byte[] concat(byte[] b1, byte[] b2, int n2) {
        int n1 = b1.length;
        if ((n1 == 0) && (n2 == b2.length)) {
            return b2;
        }
        byte[] res = new byte[n1 + n2];
        System.arraycopy(b1, 0, res, 0, n1);
        System.arraycopy(b2, 0, res, n1, n2);
        return res;
    }

    /**
     * Array byte empty.
     */
    private final static byte[] B0 = new byte[0];

    /**
     * Performs a low level transmission.
     * @param command the command to send to the smart card.
     * @param bAutoGetResp if an automatic GetResponse is requested.
     * @param bAutoReissue if an automatic Reissue is requested.
     * @return the smart card response matching to the auto parameters.
     * @throws CardException if a card exception occurs.
     */
    private synchronized byte[] doTransmit(byte[] command, boolean bAutoGetResp,
            boolean bAutoReissue) throws CardException {
        // note that we modify the 'command' array in some cases, so it must
        // be a copy of the application provided data.
        try {
            checkManageChannel(command);
            setChannel(command);
            int n = command.length;
            boolean t0 = card.protocol == SCARD_PROTOCOL_T0;
            boolean t1 = card.protocol == SCARD_PROTOCOL_T1;
            if (t0 && (n >= 7) && (command[4] == 0)) {
                throw new CardException
                        ("org.poreid.pcscforjava."
                    + "ChannelImpl.doTransmit "
                    + "PCSCException: SCARD_F_INTERNAL_ERROR " 
                        + "Extended length forms not supported for T=0");
            }
            if ((t0 || (t1 && t1StripLe)) && (n >= 7)) {
                int lc = command[4] & 0xff;
                if (lc != 0) {
                    if (n == lc + 6) {
                        n--;
                    }
                } else {
                    lc = ((command[5] & 0xff) << 8) | (command[6] & 0xff);
                    if (n == lc + 9) {
                        n -= 2;
                    }
                }
            }
            boolean getresponse = (t0 && t0GetResponse) || (t1 && t1GetResponse);
            int k = 0;
            byte[] result = B0;
            while (true) {
                if (++k >= 32) {
                    throw new CardException("org.poreid.pcscforjava."
                    + "ChannelImpl.doTransmit "
                    + "PCSCException: SCARD_F_COMM_ERROR "
                            + "Could not obtain response");
                }
                byte[] response = SCardTransmit
                    (card.cardId, card.protocol, command, 0, n);
                int rn = response.length;
                if (getresponse && (rn >= 2)) {
                    if(bAutoReissue) {
                        // see ISO 7816/2005, 5.1.3
                        if ((rn == 2) && (response[0] == 0x6c)) {
                            // Resend command using SW2 as short Le field
                            command[n - 1] = response[1];
                            continue;
                        }
                    }

                    if(bAutoGetResp) {
                        if (response[rn - 2] == 0x61) {
                            // Issue a GET RESPONSE command 
                            // using SW2 as short Le field
                            if (rn > 2) {
                                result = concat(result, response, rn - 2);
                            }
                            
                            command[0] = 0x00;
                            command[1] = (byte)0xC0;
                            command[2] = 0;
                            command[3] = 0;
                            command[4] = response[rn - 1];
                            n = 5;
                            continue;
                        }
                    }
                }
                result = concat(result, response, rn);
                break;
            }
            return result;
        } catch (PCSCException e) {
            card.handleError(e);
            throw new CardException("org.poreid.pcscforjava."
                    + "ChannelImpl.doTransmit "
                    + "PCSCException: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the status word of a smart card response.
     * @param res the smart card response.
     * @return the status word of a smart card response.
     * @throws CardException if a card exception occurs.
     */
    private static int getSW(byte[] res) throws CardException {
        if (res.length < 2) {
            throw new CardException("org.poreid.pcscforjava."
                    + "ChannelImpl.getSW "
                    + "PCSCException: SCARD_F_INTERNAL_ERROR Invalid response "
                    + "length: " + res.length);
        }
        int sw1 = res[res.length - 2] & 0xff;
        int sw2 = res[res.length - 1] & 0xff;
        return (sw1 << 8) | sw2;
    }

    /**
     * Returns true if the smart card response is 0x90 0x00.
     * @param res the smart card response.
     * @return true if the smart card response is 0x90 0x00.<br />
     * false otherwise.
     * @throws CardException if a card exception occurs.
     */
    private static boolean isOK(byte[] res) throws CardException {
        return (res.length == 2) && (getSW(res) == 0x9000);
    }

    /**
     * Set the channel class of an apdu.
     * @param com the apdu.
     */
    private void setChannel(byte[] com) {
        
        // No check the CLA, it is the choice of the application !!!
        return;
        
        /*int cla = com[0];
        if (cla < 0) {
            // proprietary class format, cannot set or check logical channel
            // for now, just return
            return;
        }
        // classes 001x xxxx is reserved for future use in ISO, ignore
        if ((cla & 0xe0) == 0x20) {
            return;
        }
        // see ISO 7816/2005, table 2 and 3
        if (channel <= 3) {
            // mask of bits 7, 1, 0 (channel number)
            // 0xbc == 1011 1100
            com[0] &= 0xbc;
            com[0] |= channel;
        } else if (channel <= 19) {
            // mask of bits 7, 3, 2, 1, 0 (channel number)
            // 0xbc == 1011 0000
            com[0] &= 0xb0;
            com[0] |= 0x40;
            com[0] |= (channel - 4);
        } else {
            throw new RuntimeException("Unsupported channel number: " + channel);
        }*/
    }

    /**
     * Closes this CardChannel. The logical channel is closed by issuing
     * a <code>MANAGE CHANNEL</code> command that should use the format
     * <code>[xx 70 80 0n]</code> where <code>n</code> is the channel number
     * of this channel and <code>xx</code> is the <code>CLA</code>
     * byte that encodes this logical channel and has all other bits set to 0.
     * After this method returns, calling other
     * methods in this class will raise an IllegalStateException.
     *
     * <p>Note that the basic logical channel cannot be closed using this
     * method. It can be closed by calling {@link Card#disconnect}.
     *
     * @throws CardException if the card operation failed
     * @throws IllegalStateException if this CardChannel represents a
     *   connection the basic logical channel
     */
    public void close() throws CardException {
        if (getChannelNumber() == 0) {
            throw new IllegalStateException("Cannot close basic logical channel");
        }
        if (isClosed) {
            return;
        }
        card.checkExclusive();
        try {
            byte[] com = new byte[] {0x00, 0x70, (byte)0x80, 0};
            com[3] = (byte)getChannelNumber();
            setChannel(com);
            byte[] res = SCardTransmit(card.cardId, card.protocol, com, 0, com.length);
            if (isOK(res) == false) {
                throw new CardException("org.poreid.pcscforjava."
                    + "ChannelImpl.close "
                    + "PCSCException: SCARD_F_INTERNAL_ERROR " + 
                        PCSC.toString(res));
            }
        } catch (PCSCException e) {
            card.handleError(e);
            throw new CardException("org.poreid.pcscforjava."
                    + "ChannelImpl.close "
                    + "PCSCException: " + e.getMessage() + " "
                    + "Could not close channel", e);
        } finally {
            isClosed = true;
        }
    }

    /**
     * Returns string representation of the channel.
     * @return string representation of the channel.
     */
    public String toString() {
        return "PC/SC channel " + channel;
    }

}
