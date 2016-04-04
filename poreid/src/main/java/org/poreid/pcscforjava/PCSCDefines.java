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

/**
 * Class which supplies all the PCSC defines for the library.<br />
 * And some functions to manage it.
 *
 * @since   1.6
 * @author  Matthieu Leromain
 */
public class PCSCDefines
{
    private PCSCDefines()
    {
        // No Constructor static class.
    }
    
    /**
     *  Scard scope for user
     */
    public final static int SCARD_SCOPE_USER      =  0x0000;
    /** 
     * Scard scope for terminal
     */
    public final static int SCARD_SCOPE_TERMINAL  =  0x0001;
    /** 
     * Scard scope for system
     */
    public final static int SCARD_SCOPE_SYSTEM    =  0x0002;
    /** 
     * Scard scope global
     */
    public final static int SCARD_SCOPE_GLOBAL    =  0x0003;

    /** 
     * Share exclusive
     */
    public final static int SCARD_SHARE_EXCLUSIVE =  0x0001;
    /** 
     * Share shared
     */
    public final static int SCARD_SHARE_SHARED    =  0x0002;
    /** 
     * Share direct (without physical card)
     */
    public final static int SCARD_SHARE_DIRECT    =  0x0003;
    
    /** 
     * Leave the card in its state
     */
    public final static int SCARD_LEAVE_CARD      =  0x0000;
    /** 
     * Reset the card
     */
    public final static int SCARD_RESET_CARD      =  0x0001;
    /** 
     * Unpower the card
     */
    public final static int SCARD_UNPOWER_CARD    =  0x0002;
    /** 
     * Eject the card
     */
    public final static int SCARD_EJECT_CARD      =  0x0003;

    /** 
     * State of card is unware
     */
    public final static int SCARD_STATE_UNAWARE     = 0x0000;
    /** 
     * State of card is ignore
     */
    public final static int SCARD_STATE_IGNORE      = 0x0001;
    /** 
     * State of card is changed
     */
    public final static int SCARD_STATE_CHANGED     = 0x0002;
    /** 
     * State of card is unknown
     */
    public final static int SCARD_STATE_UNKNOWN     = 0x0004;
    /** 
     * State of card is unavailable
     */
    public final static int SCARD_STATE_UNAVAILABLE = 0x0008;
    /** 
     * State of card is empty
     */
    public final static int SCARD_STATE_EMPTY       = 0x0010;
    /** 
     * State of card is present
     */
    public final static int SCARD_STATE_PRESENT     = 0x0020;
    /** 
     * State of card is atr match
     */
    public final static int SCARD_STATE_ATRMATCH    = 0x0040;
    /** 
     * State of card is exclusive
     */
    public final static int SCARD_STATE_EXCLUSIVE   = 0x0080;
    /** 
     * State of card is inuse
     */
    public final static int SCARD_STATE_INUSE       = 0x0100;
    /** 
     * State of card is mute
     */
    public final static int SCARD_STATE_MUTE        = 0x0200;
    /** 
     * State of card is unpowered
     */
    public final static int SCARD_STATE_UNPOWERED   = 0x0400;

    /** 
     * Vendor information definitions
     */
    public final static int SCARD_CLASS_VENDOR_INFO     = 1;
    /** 
     * Communication definitions
     */
    public final static int SCARD_CLASS_COMMUNICATIONS  = 2;
    /** 
     * Protocol definitions
     */
    public final static int SCARD_CLASS_PROTOCOL        = 3;
    /** 
     * Power Management definitions
     */
    public final static int SCARD_CLASS_POWER_MGMT      = 4;
    /** 
     * Security Assurance definitions
     */
    public final static int SCARD_CLASS_SECURITY        = 5;
    /** 
     * Mechanical characteristic definitions
     */
    public final static int SCARD_CLASS_MECHANICAL      = 6;
    /** 
     * Vendor specific definitions
     */
    public final static int SCARD_CLASS_VENDOR_DEFINED  = 7;
    /** 
     * Interface Device Protocol options
     */
    public final static int SCARD_CLASS_IFD_PROTOCOL    = 8;
    /** 
     * ICC State specific definitions
     */
    public final static int SCARD_CLASS_ICC_STATE       = 9;
    /** 
     * performace counters
     */
    public final static int SCARD_CLASS_PERF            = 0x7ffe;
    /** 
     * System-specific definitions
     */
    public final static int SCARD_CLASS_SYSTEM          = 0x7fff;
    
    /** 
     * SCARD_ATTR_VALUE for ATR_STRING
     */
    public final static int SCARD_ATTR_ATR_STRING = 
            SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, 0x0303);
    /** 
     * SCARD_ATTR_VALUE for CHANNEL_ID
     */
    public final static int SCARD_ATTR_CHANNEL_ID = 
            SCARD_ATTR_VALUE(SCARD_CLASS_COMMUNICATIONS, 0x0110);
    /** 
     * SCARD_ATTR_VALUE for CHARACTERISTICS
     */
    public final static int SCARD_ATTR_CHARACTERISTICS = 
            SCARD_ATTR_VALUE(SCARD_CLASS_MECHANICAL, 0x0150);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_BWT
     */
    public final static int SCARD_ATTR_CURRENT_BWT = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0209);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_CLK        
     */
    public final static int SCARD_ATTR_CURRENT_CLK = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0202);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_CWT
     */
    public final static int SCARD_ATTR_CURRENT_CWT = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x020a);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_D
     */
    public final static int SCARD_ATTR_CURRENT_D = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0204);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_EBC_ENCODING
     */
    public final static int SCARD_ATTR_CURRENT_EBC_ENCODING = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x020b);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_F
     */
    public final static int SCARD_ATTR_CURRENT_F = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0203);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_IFSC
     */
    public final static int SCARD_ATTR_CURRENT_IFSC = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0207);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_IFSD
     */
    public final static int SCARD_ATTR_CURRENT_IFSD = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0208);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_N
     */
    public final static int SCARD_ATTR_CURRENT_N = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0205);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_PROTOCOL_TYPE
     */
    public final static int SCARD_ATTR_CURRENT_PROTOCOL_TYPE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0201);
    /** 
     * SCARD_ATTR_VALUE for CURRENT_W
     */
    public final static int SCARD_ATTR_CURRENT_W = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0206);
    /** 
     * SCARD_ATTR_VALUE for DEFAULT_CLK
     */
    public final static int SCARD_ATTR_DEFAULT_CLK = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0121);
    /** 
     * SCARD_ATTR_VALUE for DEFAULT_DATA_RATE
     */
    public final static int SCARD_ATTR_DEFAULT_DATA_RATE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0123);
    /** 
     * SCARD_ATTR_VALUE for DEVICE_FRIENDLY_NAME
     */
    public final static int SCARD_ATTR_DEVICE_FRIENDLY_NAME = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0003);
    /**
     *  SCARD_ATTR_VALUE for DEVICE_IN_USE
     */
    public final static int SCARD_ATTR_DEVICE_IN_USE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0002);
    /** 
     * SCARD_ATTR_VALUE for DEVICE_SYSTEM_NAME
     */
    public final static int SCARD_ATTR_DEVICE_SYSTEM_NAME = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0004);
    /** 
     * SCARD_ATTR_VALUE for DEVICE_UNIT
     */
    public final static int SCARD_ATTR_DEVICE_UNIT = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0001);
    /** 
     * SCARD_ATTR_VALUE for ICC_INTERFACE_STATUS
     */
    public final static int SCARD_ATTR_ICC_INTERFACE_STATUS = 
            SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, 0x0301);
    /** 
     * SCARD_ATTR_VALUE for ICC_PRESENCE
     */
    public final static int SCARD_ATTR_ICC_PRESENCE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, 0x0300);
    /** 
     * SCARD_ATTR_VALUE for ICC_TYPE_PER_ATR
     */
    public final static int SCARD_ATTR_ICC_TYPE_PER_ATR = 
            SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, 0x0304);
    /** 
     * SCARD_ATTR_VALUE for MAX_CLK
     */
    public final static int SCARD_ATTR_MAX_CLK = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0122);
    /** 
     * SCARD_ATTR_VALUE for MAX_DATA_RATE
     */
    public final static int SCARD_ATTR_MAX_DATA_RATE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0124);
    /** 
     * SCARD_ATTR_VALUE for MAX_IFSD
     */
    public final static int SCARD_ATTR_MAX_IFSD = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0125);
    /** 
     * SCARD_ATTR_VALUE for POWER_MGMT_SUPPORT
     */
    public final static int SCARD_ATTR_POWER_MGMT_SUPPORT = 
            SCARD_ATTR_VALUE(SCARD_CLASS_POWER_MGMT, 0x0131);
    /** 
     * SCARD_ATTR_VALUE for PROTOCOL_TYPES
     */
    public final static int SCARD_ATTR_PROTOCOL_TYPES = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0120);
    /** 
     * SCARD_ATTR_VALUE for VENDOR_IFD_SERIAL_NO
     */
    public final static int SCARD_ATTR_VENDOR_IFD_SERIAL_NO = 
            SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, 0x0103);
    /** 
     * SCARD_ATTR_VALUE for VENDOR_IFD_TYPE
     */
    public final static int SCARD_ATTR_VENDOR_IFD_TYPE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, 0x0101);
    /** 
     * SCARD_ATTR_VALUE for VENDOR_INFO
     */
    public final static int SCARD_ATTR_VENDOR_IFD_VERSION = 
            SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, 0x0102);
    /** 
     * SCARD_ATTR_VALUE for VENDOR_NAME
     */
    public final static int SCARD_ATTR_VENDOR_NAME = 
            SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, 0x0100);
    /**
     * SCARD_ATTR_VALUE for ATTR_SUPRESS_T1_IFS_REQUEST
     */ 
    public final static int SCARD_ATTR_SUPRESS_T1_IFS_REQUEST = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0007);

    /**
     * Returns the SCARD_ATTR_VALUE from a class and a tag.
     * @param iClass the class.
     * @param iTag the tag.
     * @return the SCARD_ATTR_VALUE.
     */
    private static int SCARD_ATTR_VALUE(int iClass, int iTag)
    {
        return (iClass << 16) | iTag;
    }

    /**
     * FEATURE_VERIFY_PIN_START
     */
    public final static byte       FEATURE_VERIFY_PIN_START = (byte) 0x01;

    /**
     * FEATURE_VERIFY_PIN_FINISH
     */
    public final static byte       FEATURE_VERIFY_PIN_FINISH = (byte) 0x02;

    /**
     * FEATURE_MODIFY_PIN_START
     */
    public final static byte       FEATURE_MODIFY_PIN_START = (byte) 0x03;

    /**
     * FEATURE_MODIFY_PIN_FINISH
     */
    public final static byte       FEATURE_MODIFY_PIN_FINISH = (byte) 0x04;

    /**
     * FEATURE_GET_KEY_PRESSED
     */
    public final static byte       FEATURE_GET_KEY_PRESSED = (byte) 0x05;

    /**
     * FEATURE_VERIFY_PIN_DIRECT
     */
    public final static byte       FEATURE_VERIFY_PIN_DIRECT = (byte) 0x06;

    /**
     * FEATURE_MODIFY_PIN_DIRECT
     */
    public final static byte       FEATURE_MODIFY_PIN_DIRECT = (byte) 0x07;

    /**
     * FEATURE_MCT_READER_DIRECT
     */
    public final static byte       FEATURE_MCT_READER_DIRECT = (byte) 0x08;

    /**
     * FEATURE_MCT_UNIVERSAL
     */
    public final static byte       FEATURE_MCT_UNIVERSAL = (byte) 0x09;

    /**
     * FEATURE_IFD_PIN_PROPERTIES
     */
    public final static byte       FEATURE_IFD_PIN_PROPERTIES = (byte) 0x0A;

    /**
     * FEATURE_ABORT
     */
    public final static byte       FEATURE_ABORT = (byte) 0x0B;

    /**
     * FEATURE_SET_SPE_MESSAGE
     */
    public final static byte       FEATURE_SET_SPE_MESSAGE = (byte) 0x0C;

    /**
     * FEATURE_VERIFY_PIN_DIRECT_APP_ID
     */
    public final static byte       FEATURE_VERIFY_PIN_DIRECT_APP_ID = (byte) 0x0D;

    /**
     * FEATURE_MODIFY_PIN_DIRECT_APP_ID
     */
    public final static byte       FEATURE_MODIFY_PIN_DIRECT_APP_ID = (byte) 0x0E;

    /**
     * FEATURE_WRITE_DISPLAY
     */
    public final static byte       FEATURE_WRITE_DISPLAY = (byte) 0x0F;

    /**
     * FEATURE_GET_KEY
     */
    public final static byte       FEATURE_GET_KEY = (byte) 0x10;

    /**
     * FEATURE_IFD_DISPLAY_PROPERTIES
     */
    public final static byte       FEATURE_IFD_DISPLAY_PROPERTIES = (byte) 0x11;
    
    /**
     * FEATURE_GET_TLV_PROPERTIES
     */
    public final static byte       FEATURE_GET_TLV_PROPERTIES = (byte) 0x12;
    
    /**
     * FEATURE_CCID_ESC_COMMAND
     */
    public final static byte       FEATURE_CCID_ESC_COMMAND = (byte) 0x13;

    /**
     * Array of FEATURE Strings.
     */
    final static String[]   FEATURES = {"VERIFY_PIN_START", "VERIFY_PIN_FINISH",
    "MODIFY_PIN_START", "MODIFY_PIN_FINISH", "GET_KEY_PRESSED",
    "VERIFY_PIN_DIRECT", "MODIFY_PIN_DIRECT", "MCT_READER_DIRECT",
    "MCT_UNIVERSAL", "IFD_PIN_PROPERTIES", "ABORT", "SET_SPE_MESSAGE",
    "VERIFY_PIN_DIRECT_APP_ID", "MODIFY_PIN_DIRECT_APP_ID", "WRITE_DISPLAY",
    "GET_KEY", "IFD_DISPLAY_PROPERTIES", "GET_TLV_PROPERTIES", 
    "CCID_ESC_COMMAND"};

    /**
     * IFD_HANDLER: features supported
     */
    final static byte[]     IFD_HANDLER = {
        (byte)0x06, (byte)0x04, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x06,
        (byte)0x07, (byte)0x04, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x07};

    /**
     * bPINOperation for a PIN Verification
     */
    final static byte       bPINOperation_PINVerification = (byte)0x00;

    /**
     * bPINOperation for a PIN Modification
     */
    final static byte       bPINOperation_PINModification = (byte)0x01;

    /**
     * Modifies the code for a correct SCARD code.
     * @param code the code.
     * @return the SCARD code.
     */
    public static int SCARD_CTL_CODE(int code) {
        if(System.getProperty("os.name").contains("Windows"))
            // cf. WinIOCTL.h
            return (0x31 << 16 | (code) << 2);
        else
            // cf. reader.h
            return 0x42000000 + (code);
    }
}
