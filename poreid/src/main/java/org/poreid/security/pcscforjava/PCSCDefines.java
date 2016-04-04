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
package org.poreid.security.pcscforjava;

/**
 * Class which supplies all the PCSC defines for the library.<br />
 * And some functions to manage it.
 *
 * @since   1.6
 * @author  Matthieu Leromain
 */
public class PCSCDefines
{
    /**
     * SCARD_SCOPE_USER
     */
    final static int SCARD_SCOPE_USER      =  0x0000;
    /**
     * SCARD_SCOPE_TERMINAL
     */
    final static int SCARD_SCOPE_TERMINAL  =  0x0001;
    /**
     * SCARD_SCOPE_SYSTEM
     */
    final static int SCARD_SCOPE_SYSTEM    =  0x0002;
    /**
     * SCARD_SCOPE_GLOBAL
     */
    final static int SCARD_SCOPE_GLOBAL    =  0x0003;

    /**
     * SCARD_SHARE_EXCLUSIVE
     */
    final static int SCARD_SHARE_EXCLUSIVE =  0x0001;
    /**
     * SCARD_SHARE_SHARED
     */
    final static int SCARD_SHARE_SHARED    =  0x0002;
    /**
     * SCARD_SHARE_DIRECT
     */
    final static int SCARD_SHARE_DIRECT    =  0x0003;
    
    /**
     * SCARD_LEAVE_CARD
     */
    final static int SCARD_LEAVE_CARD      =  0x0000;
    /**
     * SCARD_RESET_CARD
     */
    final static int SCARD_RESET_CARD      =  0x0001;
    /**
     * SCARD_UNPOWER_CARD
     */
    final static int SCARD_UNPOWER_CARD    =  0x0002;
    /**
     * SCARD_EJECT_CARD
     */
    final static int SCARD_EJECT_CARD      =  0x0003;

    /**
     * SCARD_STATE_UNAWARE
     */
    final static int SCARD_STATE_UNAWARE     = 0x0000;
    /**
     * SCARD_STATE_IGNORE
     */
    final static int SCARD_STATE_IGNORE      = 0x0001;
    /**
     * SCARD_STATE_CHANGED
     */
    final static int SCARD_STATE_CHANGED     = 0x0002;
    /**
     * SCARD_STATE_UNKNOWN
     */
    final static int SCARD_STATE_UNKNOWN     = 0x0004;
    /**
     * SCARD_STATE_UNAVAILABLE
     */
    final static int SCARD_STATE_UNAVAILABLE = 0x0008;
    /**
     * SCARD_STATE_EMPTY
     */
    final static int SCARD_STATE_EMPTY       = 0x0010;
    /**
     * SCARD_STATE_PRESENT
     */
    final static int SCARD_STATE_PRESENT     = 0x0020;
    /**
     * SCARD_STATE_ATRMATCH
     */
    final static int SCARD_STATE_ATRMATCH    = 0x0040;
    /**
     * SCARD_STATE_EXCLUSIVE
     */
    final static int SCARD_STATE_EXCLUSIVE   = 0x0080;
    /**
     * SCARD_STATE_INUSE
     */
    final static int SCARD_STATE_INUSE       = 0x0100;
    /**
     * SCARD_STATE_MUTE
     */
    final static int SCARD_STATE_MUTE        = 0x0200;
    /**
     * SCARD_STATE_UNPOWERED
     */
    final static int SCARD_STATE_UNPOWERED   = 0x0400;
    

    /**
     * Vendor information definitions
     */
    final static int SCARD_CLASS_VENDOR_INFO     = 1;
    /**
     * Communication definitions
     */
    final static int SCARD_CLASS_COMMUNICATIONS  = 2;
    /**
     * Protocol definitions
     */
    final static int SCARD_CLASS_PROTOCOL        = 3;
    /**
     * Power Management definitions
     */
    final static int SCARD_CLASS_POWER_MGMT      = 4;
    /**
     * Security Assurance definitions
     */
    final static int SCARD_CLASS_SECURITY        = 5;
    /**
     * Mechanical characteristic definitions
     */
    final static int SCARD_CLASS_MECHANICAL      = 6;
    /**
     * Vendor specific definitions
     */
    final static int SCARD_CLASS_VENDOR_DEFINED  = 7;
    /**
     * Interface Device Protocol options 
     */
    final static int SCARD_CLASS_IFD_PROTOCOL    = 8;
    /**
     * ICC State specific definitions 
     */
    final static int SCARD_CLASS_ICC_STATE       = 9;
    /**
     * Performace counters
     */
    final static int SCARD_CLASS_PERF            = 0x7ffe;
    /**
     * System-specific definitions 
     */
    final static int SCARD_CLASS_SYSTEM          = 0x7fff;

    /**
     * SCARD_ATTR_ATR_STRING
     */
    final static int SCARD_ATTR_ATR_STRING = 
            SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, 0x0303);
    /**
     * SCARD_ATTR_CHARACTERISTICS
     */
    final static int SCARD_ATTR_CHARACTERISTICS = 
            SCARD_ATTR_VALUE(SCARD_CLASS_MECHANICAL, 0x0150);
    /**
     * SCARD_ATTR_CURRENT_BWT
     */
    final static int SCARD_ATTR_CURRENT_BWT = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0209);
    /**
     * SCARD_ATTR_CURRENT_CLK
     */
    final static int SCARD_ATTR_CURRENT_CLK = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0202);
    /**
     * SCARD_ATTR_CURRENT_CWT
     */
    final static int SCARD_ATTR_CURRENT_CWT = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x020a);
    /**
     * SCARD_ATTR_CURRENT_D
     */
    final static int SCARD_ATTR_CURRENT_D = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0204);
    /**
     * SCARD_ATTR_CURRENT_EBC_ENCODING
     */
    final static int SCARD_ATTR_CURRENT_EBC_ENCODING = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x020b);
    /**
     * SCARD_ATTR_CURRENT_F
     */
    final static int SCARD_ATTR_CURRENT_F = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0203);
    /**
     * SCARD_ATTR_CURRENT_IFSC
     */
    final static int SCARD_ATTR_CURRENT_IFSC = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0207);
    /**
     * SCARD_ATTR_CURRENT_IFSD
     */
    final static int SCARD_ATTR_CURRENT_IFSD = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0208);
    /**
     * SCARD_ATTR_CURRENT_N
     */
    final static int SCARD_ATTR_CURRENT_N = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0205);
    /**
     * SCARD_ATTR_CURRENT_PROTOCOL_TYPE
     */
    final static int SCARD_ATTR_CURRENT_PROTOCOL_TYPE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0201);
    /**
     * SCARD_ATTR_CURRENT_W
     */
    final static int SCARD_ATTR_CURRENT_W = 
            SCARD_ATTR_VALUE(SCARD_CLASS_IFD_PROTOCOL, 0x0206);
    /**
     * SCARD_ATTR_DEFAULT_CLK
     */
    final static int SCARD_ATTR_DEFAULT_CLK = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0121);
    /**
     * SCARD_ATTR_DEFAULT_DATA_RATE
     */
    final static int SCARD_ATTR_DEFAULT_DATA_RATE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0123);
    /**
     * SCARD_ATTR_DEVICE_FRIENDLY_NAME
     */
    final static int SCARD_ATTR_DEVICE_FRIENDLY_NAME = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0003);
    /**
     * SCARD_ATTR_DEVICE_IN_USE
     */
    final static int SCARD_ATTR_DEVICE_IN_USE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0002);
    /**
     * SCARD_ATTR_DEVICE_SYSTEM_NAME
     */
    final static int SCARD_ATTR_DEVICE_SYSTEM_NAME = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0004);
    /**
     * SCARD_ATTR_DEVICE_UNIT
     */
    final static int SCARD_ATTR_DEVICE_UNIT = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0001);
    /**
     * SCARD_ATTR_ICC_INTERFACE_STATUS
     */
    final static int SCARD_ATTR_ICC_INTERFACE_STATUS = 
            SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, 0x0301);
    /**
     * SCARD_ATTR_ICC_PRESENCE
     */
    final static int SCARD_ATTR_ICC_PRESENCE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, 0x0300);
    /**
     * SCARD_ATTR_ICC_TYPE_PER_ATR
     */
    final static int SCARD_ATTR_ICC_TYPE_PER_ATR = 
            SCARD_ATTR_VALUE(SCARD_CLASS_ICC_STATE, 0x0304);
    /**
     * SCARD_ATTR_MAX_CLK
     */
    final static int SCARD_ATTR_MAX_CLK = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0122);
    /**
     * SCARD_ATTR_MAX_DATA_RATE
     */
    final static int SCARD_ATTR_MAX_DATA_RATE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0124);
    /**
     * SCARD_ATTR_MAX_IFSD
     */
    final static int SCARD_ATTR_MAX_IFSD = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0125);
    /**
     * SCARD_ATTR_POWER_MGMT_SUPPORT
     */
    final static int SCARD_ATTR_POWER_MGMT_SUPPORT = 
            SCARD_ATTR_VALUE(SCARD_CLASS_POWER_MGMT, 0x0131);
    /**
     * SCARD_ATTR_PROTOCOL_TYPES
     */
    final static int SCARD_ATTR_PROTOCOL_TYPES = 
            SCARD_ATTR_VALUE(SCARD_CLASS_PROTOCOL, 0x0120);
    /**
     * SCARD_ATTR_VENDOR_IFD_SERIAL_NO
     */
    final static int SCARD_ATTR_VENDOR_IFD_SERIAL_NO = 
            SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, 0x0103);
    /**
     * SCARD_ATTR_VENDOR_IFD_TYPE
     */
    final static int SCARD_ATTR_VENDOR_IFD_TYPE = 
            SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, 0x0101);
    /**
     * SCARD_ATTR_VENDOR_IFD_VERSION
     */
    final static int SCARD_ATTR_VENDOR_IFD_VERSION = 
            SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, 0x0102);
    /**
     * SCARD_ATTR_VENDOR_NAME
     */
    final static int SCARD_ATTR_VENDOR_NAME = 
            SCARD_ATTR_VALUE(SCARD_CLASS_VENDOR_INFO, 0x0100);
/**
     * SCARD_ATTR_SUPRESS_T1_IFS_REQUEST
     */
    final static int SCARD_ATTR_SUPRESS_T1_IFS_REQUEST = 
            SCARD_ATTR_VALUE(SCARD_CLASS_SYSTEM, 0x0007);

    /**
     * Returns the SCARD ATTR VALUE for a class and a tag.
     * @param iClass the class.
     * @param iTag the tag.
     * @return the SCARD ATTR VALUE for a class and a tag.
     */
    private static int SCARD_ATTR_VALUE(int iClass, int iTag)
    {
        return (iClass << 16) | iTag;
    }
}
