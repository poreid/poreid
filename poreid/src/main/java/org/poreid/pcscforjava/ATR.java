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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * A Smart Card's answer-to-reset bytes. A Card's ATR object can be obtained
 * by calling {@linkplain Card#getATR}.<br />
 * This class analyzes the various elements of the ATR but does not indicate
 * if the ATR is valid or not (because of some smart card norms can impact this: 
 * ISO, EMV ...).
 *
 * <p>Instances of this class are immutable. Where data is passed in or out
 * via byte arrays, defensive cloning is performed.
 *
 * @see Card#getATR
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  JSR 268 Expert Group
 * @author  Matthieu Leromain
 */
public final class ATR implements java.io.Serializable {
    
    private static final long serialVersionUID = 6695383790847736493L;
    
    /**
     * Defines the F values defined by ISO 7816-3.
     * The RFU values are defined by 0.
     */
    private static final int       aiF[] = { 372, 372, 558, 744, 1116, 1488, 
        1860, 0, 0, 512, 768, 1024, 1536, 2048, 0, 0};
    
    /**
     * Defines the max frequency defined by ISO 7816-3.
     * The RFU values are defined by 0.
     */
    private static final double    afFMax[] = { 4, 5, 6, 8, 12, 16, 20, 0, 0, 5,
        7.5, 10, 15, 20, 0, 0};
    
    /**
     * Defines the D values defined by ISO 7816-3.
     * The RFU values are defined by 0.
     */
    private static final int       aiD[] = { 0, 1, 2, 4, 8, 16, 32, 64, 12, 20, 
        0, 0, 0, 0, 0, 0};
    
    /**
     * Contains the ATR bytes array.
     */
    private byte[]          atr;
    
    /**
     * Contains the list of TAx after the analyze of the ATR.
     */
    private ArrayList<Byte> aBTai;
    
    /**
     * Contains the list of TBx after the analyze of the ATR.
     */
    private ArrayList<Byte> aBTbi;
    
    /**
     * Contains the list of TCx after the analyze of the ATR.
     */
    private ArrayList<Byte> aBTci;
    
    /**
     * Contains the list of TDx after the analyze of the ATR.
     */
    private ArrayList<Byte> aBTdi;
    
    /**
     * Contains the list of supported protocols after the analyze of the ATR.
     */
    private ArrayList<Byte> aBProtocol;
    
    /**
     * Contains the list of supported types of transmission defined by the
     * array below.
     */
    private ArrayList<String> asTaType;
    
    /**
-     * Contains the list of types of transmission defined by ISO 7816-3.
     */
    private static final String  m_psT[] = 
        {"Half-duplex transmission of characters.",
        "Half-duplex transmission of blocks.", "RFU of full-duplex operations.",
        "RFU of full-duplex operations.", "Enhanced half-duplex transmission " +
        "of characters.", "RFU by ISO/IEC JTC 1/SC 17.",
        "RFU by ISO/IEC JTC 1/SC 17.", "RFU by ISO/IEC JTC 1/SC 17.",
        "RFU by ISO/IEC JTC 1/SC 17.", "RFU by ISO/IEC JTC 1/SC 17.",
        "RFU by ISO/IEC JTC 1/SC 17.", "RFU by ISO/IEC JTC 1/SC 17.",
        "RFU by ISO/IEC JTC 1/SC 17.", "RFU by ISO/IEC JTC 1/SC 17.",
        "Transmission protocols not standardized by ISO/IEC JTC 1/SC 17.",
        "Does not refer to a transmission protocol, but only qualifies global" +
        "interface bytes."};

    /**
     * Contains the convention.<br />
     * It can be:<br />
     * "Direct" for a direct convention (ATR starts with 0x3B)<br />
     * "Inverse" for an inverse convention (ATR starts with 0x3F)
     */
    private String          sConvention;
    
    /**
     * CI is used to determine the voltage class of the ICC.
     */
    private byte            BCi;
    
    /**
     * Clock is used to indicate whether the card supports clock stop (!= 0x00)
     * or not (= 0x00) and, when supported, which state is preferred on the
     * electrical circuit CLK when the clock is stopped.
     */
    private byte            BClock;
    
    /**
     * Indicates the voltage class.<br />
     * Class A is 5V => 0x01.<br />
     * Class B is 3V => 0x02.<br />
     * Class C is 1.8V => 0x04.<br />
     * A mix of flags is also possible.
     */
    private byte            BVoltageClass;
    
    /**
     * The smart card clock frequency in Hz.
     */
    private int             iClockCardFrequency;
    
    /**
     * Indicates if the smart card is in negotiable mode or not.
     */
    private boolean         bIsNegotiable;
    
    /**
     * Indicates if the smart card is able to change from specific to
     * negotiable mode or not.
     */
    private boolean         bIsAbleToChangeSpecificNegotiableMode;
    
    /**
     * Indicates if the transmission parameters are implicitly known by the
     * terminal or not.
     */
    private boolean         bAreTransmissionParametersImplicitlyKnownByTerminal;
    
    /**
     * Index used to parse the ATR.
     */
    private int             iIndex;
        
    /**
     * Contains the Ts (first byte) of the ATR.
     */
    private byte            BTs;
    
    /** 
     * Contains the T0 (second byte) of the ATR.
     */
    private byte            BT0;
    
    /**
     *  TA1
     *  The elements below are determined by TA1.
     */
    
    /**
     * Fi is used to determine the value of F, the clock rate conversion factor,
     * which may be used to modify the frequency of the clock provided by the
     * terminal subsequent to the ATR.
     */ 
    private byte            BFi;
    
    /**
     * Di is used to determine the value of D, the bit rate adjustment factor,
     * which may be used to adjust the bit duration used subsequent to the ATR.
     */ 
    private byte            BDi;
    
    /**
     *  TB1<br />
     *  The elements below are determined by TB1.
     */
    
    /**
     * PI1 is used to determine the value of the programming voltage required by 
     * the ICC.<br />
     * If PI1 = 0 then VPP is not connected in the ICC.
     */
    private byte            BPi1;
    
    /**
     * II is used to determine the maximum programming current, Ipp, required
     * by the ICC. Not used if PI1 = 0.
     */ 
    private byte            Bii;
    
    /**
     *  TC1<br />
     *  The element below is determined by TC1.
     */
    
    /**
     * N is used to indicate the extra guardtime that shall be aded to the
     * minimum duration between the leading edges of the start bits of two 
     * consecutives characters for subsequent exchanges from the terminal to the
     * ICC.
     */ 
    private byte            BN;
    
    /**
     *  TB2<br />
     *  The element below is determined by TB2.
     */
    
    /**
     * PI2 is used to determine the value of programming 
     * voltage P required by the ICC. When present it overrides the value 
     * indicated by PI1 returned in TB1.
     */ 
    private byte            BPi2;
    
    /**
     *  TC2<br />
     *  The elements below are determined by TC2.
     */
    
    /**
     * WI is used to determine the maximum interval between the leading edge 
     * of the start bit of any character sent by the ICC and the leading edge 
     * of the start bit of the previous character sent either by the ICC or the 
     * terminal (the work waiting time).
     */ 
    private byte            BWi = 10;
    
    /**
     * The work waiting time is given by 960 * D * WI.
     */ 
    private long            lWorkWaitingTime;
    
    /**
     *  TA3<br />
     *  The element below is determined by TA3.
     */
    
    /**
     * IFSI is used to determine the IFSC, and specifies the maximum length of
     * the information field (INF) of blocks that can be received by the card.
     */ 
    private byte            BIfsi;
    
    /**
     *  TB3<br />
     *  The elements below are determined by TB3.
     */
    
    /**
     * BWI is used to calculate the BWT
     */ 
    private byte            BBwi;
    
    /**
     * CWI is used to calculate the CWT
     */ 
    private byte            BCwi;
    
    /**
     * TC3 Indicates the block error detection for T = 1 protocol.
     */
    private byte            BBlockErrorDetection;
    
    /**
     * Contains the index of the first historical byte.
     */
    private transient int   startHistorical;
    
    /**
     * Contains the number of historical bytes.
     */
    private transient int   nHistorical;
    
    /**
     * TCK is used to check the integrity of the data sent in the ATR.
     * The byte is not mandatory.
     */
    private byte            BTck;
    
    /**
     * If the TCK is present or not.
     */
    private boolean         m_bTck = false;
    
    /**
     * Contains an analyze of the historical bytes.\n
     * Each row of the array gives two columns: the byte treated and the 
     * result description of the byte.
     */
    private Object[][]      m_ppoHistoricalBytesAnalyze;
    
    /**
     * Initialize the default values of the ATR class.
     * @param iClockCard the smart card clock frequency.
     */
    private void initDefault(int iClockCard)
    {
        aBTai = new ArrayList();
        aBTbi = new ArrayList();
        aBTci = new ArrayList();
        aBTdi = new ArrayList();
        
        aBProtocol = new ArrayList();
        asTaType = new ArrayList();
        
        bIsNegotiable = false;
        
        BVoltageClass = 0x01;
        
        iClockCardFrequency = iClockCard;
                
        iIndex = 2; // Start after TS and T0
    }
    
    /**
     * Constructs an ATR from a byte array.<br />
     * Analyzes the data of the ATR.<br /><br />
     * This constructor is based on a default smart card frequency of 4MHz.
     *
     * @param atr the byte array containing the answer-to-reset bytes
     * @throws NullPointerException if <code>atr</code> is null
     * @deprecated
     */
    public ATR(byte[] atr)
    {
        // If not specified the smart card clock frequency is 4MHz
        initDefault(4000000);
        
        this.atr = atr.clone();
        parse();
    }
    
    
    /**
     * Constructs an ATR from a byte array and a smart card clock frequency.<br />
     * Analyzes the data of the ATR.
     *
     * @param atr the byte array containing the answer-to-reset bytes
     * @param iClockCard the smart card clock frequency
     * @throws NullPointerException if <code>atr</code> is null
     */
    public ATR(byte[] atr, int iClockCard) {
        
        initDefault(iClockCard);
        
        this.atr = atr.clone();
        parse();
    }
    
    /**
     * Analyze the Ts and T0 to determine the convention and the number of
     * historical bytes.
     * @return false if the ATR must be rejected.<br />
     * true if all information are determined.
     */
    private boolean analyze_ts_t0()
    {
        switch(atr[0])
        {
            case 0x3B:
                sConvention = "Direct";
                break;
                
            case 0x3F:
                sConvention = "Inverse";
                break;
            
            // Reject ICC returning ATR with TS different from 3F and 3B
            default:
                return false;
        }
        
        BTs = atr[0];
        BT0 = atr[1];
        
        nHistorical = BT0 & 0x0F;
        
        return true;
    }
    
    /**
     * Analyze the TX1 elements.
     * @return false if the analyze of the ATR is finalized.<br />
     * true if the analyze must continue.
     */
    private boolean analyze_tx1()
    {
        // TA1
        if((BT0 & 0x10) != 0)
        {
            bIsNegotiable = true;
            BFi = (byte) ((atr[iIndex] & 0xF0) >> 4);
            BDi = (byte) (atr[iIndex] & 0x0F);
            aBTai.add(atr[iIndex]);
            iIndex++;
        }
        else
        {
            // If TA1 absent the FiDi couple is equal to 0x11 (default value)
            BFi = 0x01;
            BDi = 0x01;
            aBTai.add(null);
        }
        
        // TB1
        if((BT0 & 0x20) != 0)
        {
            aBTbi.add(atr[iIndex]);
            BPi1 = (byte) (atr[iIndex] & 0x1F);
            Bii = (byte) (atr[iIndex] & 0x60);
            iIndex++;
        }
        else
        {
            BPi1 = 0x00;
            Bii = 0x00;
            aBTbi.add(null);
        }
               
        // TC1
        if((BT0 & 0x40) != 0)
        {
            aBTci.add(atr[iIndex]);
            BN = atr[iIndex];
            iIndex++;
        }
        else
        {
            BN = 0; // Indicates 12 etus if T = 0 and 11 etus if T = 1.
            aBTci.add(null);
        }
        
        // TD1
        if((BT0 & 0x80) != 0)
        {
            aBTdi.add(atr[iIndex]);
            aBProtocol.add((byte)(atr[iIndex] & 0x0F));
            iIndex++;
        }
        else
        {
            aBTdi.add(null);
            // If TD1 not present it is finished for the analyze.
            // It remains only historical bytes and TCK if present.
            startHistorical = iIndex;
            return false;
        }
        
        return true;
    }
    
    /**
     * Analyze the TX2 elements.
     * @return false if the analyze of the ATR is finalized.<br />
     * true if the analyze must continue.
     */
    private boolean analyze_tx2()
    {
        int _iIndexForTDi = aBTdi.size() - 1;
        
        // TA2
        // The presence or absence of TA2 indicates whether the ICC will operate 
        // in specific mode or negotiable mode respectively following the ATR.
        if((aBTdi.get(_iIndexForTDi) & 0x10) != 0)
        {
            bIsNegotiable = false;
            
            if((atr[iIndex] & 0x80) != 0x00)
                bIsAbleToChangeSpecificNegotiableMode = true;
            else
                bIsAbleToChangeSpecificNegotiableMode = false;
            
            // The transmission parameters are defined by the interface characters
            // if b5 is set to 0, or are implicitly known by the terminal if
            // b5 is set to 1.
            if((atr[iIndex] & 0x10) != 0x00)
                bAreTransmissionParametersImplicitlyKnownByTerminal = true;
            else
                bAreTransmissionParametersImplicitlyKnownByTerminal = false;
            
            aBTai.add(atr[iIndex]);
            
            asTaType.add(m_psT[atr[iIndex] & 0x0F]);
            
            iIndex++;
        }
        else
        {
            // If TA2 absent
            // Be sure that the negotiable mode is true
            bIsNegotiable = true;
            aBTai.add(null);
        }
        
        // TB2
        if((aBTdi.get(_iIndexForTDi) & 0x20) != 0)
        {
            BPi2 = atr[iIndex];
            aBTbi.add(atr[iIndex]);
            
            iIndex++;
        }
        else
        {
            BPi1 = 0x00;
            Bii = 0x00;
            aBTbi.add(null);
        }
               
        // TC2
        // Specific to T = 0
        if((aBTdi.get(_iIndexForTDi) & 0x40) != 0)
        {
            aBTci.add(atr[iIndex]);
            BWi = atr[iIndex];
            iIndex++;
        }
        else
        {
            BWi = 0x0A;
            aBTci.add(null);
        }
        
        lWorkWaitingTime = 960 * BDi * BWi;
        
        // TD2
        if((aBTdi.get(_iIndexForTDi) & 0x80) != 0)
        {
            aBTdi.add(atr[iIndex]);
            aBProtocol.add((byte)(atr[iIndex] & 0x0F));
            iIndex++;
        }
        else
        {
            aBTdi.add(null);
            // If TD2 not present it is finished for the analyze.
            // It remains only historical bytes and TCK if present.
            startHistorical = iIndex;
            return false;
        }
        
        return true;
    }
    
    /**
     * Analyze the TX3 elements.
     * @return false if the analyze of the ATR is finalized.<br />
     * true if the analyze must continue.
     */
    private boolean analyze_tx3()
    {
        int _iIndexForTDi = aBTdi.size() - 1;
        
        // TA3
        if((aBTdi.get(_iIndexForTDi) & 0x10) != 0)
        {
            // If TD2 returns T=1 TA3 returns the information field size integer
            // for the ICC (IFSI).
            if((aBTdi.get(_iIndexForTDi) & 0x0F) == 0x01)
            {
                BIfsi = atr[iIndex];
            }
            else if((aBTdi.get(_iIndexForTDi) & 0x0F) == 0x0F)
            {
                BIfsi = atr[iIndex];
                BCi = (byte) (BIfsi & 0x3F);
                BClock = (byte) (BIfsi & 0xC0);
            }
            
            BVoltageClass = (byte) (BIfsi & 0x3F);
            
            asTaType.add(m_psT[atr[iIndex] & 0x0F]);
            
            aBTai.add(atr[iIndex]);
            iIndex++;
        }
        else
        {
            // If TA3 absent
            aBTai.add(null);
        }
        
        // TB3
        if((aBTdi.get(_iIndexForTDi) & 0x20) != 0)
        {
            // If TD2 returns T=1 TB3 indicates the values of the CWI and the WI 
            // used to calucalte the CWT and BWT respectively.
            if((aBTdi.get(_iIndexForTDi) & 0x0F) == 0x01)
            {
                BBwi = (byte)((atr[iIndex] & 0xF0) >> 4);
                BCwi = (byte)(atr[iIndex] & 0x0F);
            }
            aBTbi.add(atr[iIndex]);
            iIndex++;
        }
        else
        {
            aBTbi.add(null);
        }
               
        // TC3
        if((aBTdi.get(_iIndexForTDi) & 0x40) != 0)
        {
            // If TD2 returns T=1 TC3 indicates the type of block error 
            // detection code to be used.
            if((aBTdi.get(_iIndexForTDi) & 0x0F) == 0x01)
            {
                BBlockErrorDetection = (byte)(atr[iIndex] & 0x01);
            }
            
            aBTci.add(atr[iIndex]);
            iIndex++;
        }
        else
        {
            BBlockErrorDetection = 0x00;
            aBTci.add(null);
        }
        
        // TD3
        if((aBTdi.get(_iIndexForTDi) & 0x80) != 0)
        {
            aBTdi.add(atr[iIndex]);
            aBProtocol.add((byte)(atr[iIndex] & 0x0F));
            iIndex++;
        }
        else
        {
            aBTdi.add(null);
            // If TD2 not present it is finished for the analyze.
            // It remains only historical bytes and TCK if present.
            startHistorical = iIndex;
            return false;
        }
        
        return true;
    }
    
    /**
     * Analyze the TXi elements.
     * @return false if the analyze of the ATR is finalized.<br />
     * true if the analyze must continue.
     */
    private boolean analyze_txi()
    {
        do
        {
            int _iIndexForTDi = aBTdi.size() - 1;

            // TAi
            if((aBTdi.get(_iIndexForTDi) & 0x10) != 0)
            {
                if((aBTdi.get(_iIndexForTDi) & 0x0F) == 0x0F)
                {
                    BCi = (byte) (atr[iIndex] & 0x3F);
                    BClock = (byte) (atr[iIndex] & 0xC0);
                }

                aBTai.add(atr[iIndex]);
                
                asTaType.add(m_psT[atr[iIndex] & 0x0F]);
                
                if(aBProtocol.get(aBProtocol.size()-1) == 0x0F)
                    BVoltageClass = (byte) (atr[iIndex] & 0x3F);
                
                iIndex++;
            }
            else
            {
                aBTai.add(null);
            }

            // TBi
            if((aBTdi.get(_iIndexForTDi) & 0x20) != 0)
            {
                aBTbi.add(atr[iIndex]);
                iIndex++;
            }
            else
            {
                aBTbi.add(null);
            }

            // TCi
            if((aBTdi.get(_iIndexForTDi) & 0x40) != 0)
            {
                aBTci.add(atr[iIndex]);
                iIndex++;
            }
            else
            {
                aBTci.add(null);
            }

            // TDi
            if((aBTdi.get(_iIndexForTDi) & 0x80) != 0)
            {
                aBTdi.add(atr[iIndex]);
                aBProtocol.add((byte)(atr[iIndex] & 0x0F));
                iIndex++;
            }
            else
            {
                aBTdi.add(null);
                // If TD2 not present it is finished for the analyze.
                // It remains only historical bytes and TCK if present.
                startHistorical = iIndex;
                return false;
            }
        }
        while(true);
    }

    /**
     * Parse the ATR to analyze it.
     */
    private void parse() 
    {
        // An ATR must contain at least TS and T0 which are mandatory bytes
        if(atr.length < 2) 
            return;
        
        // TS must be 0x3B or 0x3F T0 indicates the values of TX1
        if(analyze_ts_t0() == false)
            return;
        
        lWorkWaitingTime = 960 * BWi;
        
        // Analyze TA1 TB1 TC1 TD1
        if(analyze_tx1() == false)
        {
            iIndex += nHistorical;
            
            // There is a TCK
            if(iIndex < atr.length)
            {
                BTck = atr[iIndex];
                m_bTck = true;
            }
            
            return;
        }
        
        // Analyze TA2 TB2 TC2 TD2
        if(analyze_tx2() == false)
        {
            iIndex += nHistorical;
            
            // There is a TCK
            if(iIndex < atr.length)
            {
                BTck = atr[iIndex];
                m_bTck = true;
            }
            
            return;
        }
        
        // Analyze TA3 TB3 TC3 TD3
        if(analyze_tx3() == false)
        {
            iIndex += nHistorical;
            
            // There is a TCK
            if(iIndex < atr.length)
            {
                BTck = atr[iIndex];
                m_bTck = true;
            }
                        
            return;
        }
        
        // Analyze TAi TBi TCi TDi
        if(analyze_txi() == false)
        {
            iIndex += nHistorical;
            
            // There is a TCK
            if(iIndex < atr.length)
            {
                BTck = atr[iIndex];
                m_bTck = true;
            }
            
            return;
        }
    }
    
    /**
     * Returns a copy of the bytes in this ATR.
     *
     * @return a copy of the bytes in this ATR.
     */
    public byte[] getBytes() {
        return atr.clone();
    }

    /**
     * Returns the convention of the smart card.
     * 
     * @return "Direct" if the smart card is in direct convention.<br />
     *         "Inverse" if the smart card is in inverse convention.
     */
    public String getConvention()
    {
        return sConvention;
    }
    
    /**
     * Returns the work waiting time (WWT) of the smart card.
     * 
     * @return the work waiting time (WWT) of the smart card.
     */
    public long getWorkWaitingTime()
    {
        return lWorkWaitingTime;
    }
    
    /**
     * Returns if the smart card baudrate is negotiable or not.
     * 
     * @return true if the smart card baudrate is negotiable.<br />
     * false if the smart card baudrate is not negotiable.
     */
    public boolean isNegotiableMode()
    {
        return bIsNegotiable;
    }
    
    /**
     * Returns if the smart card is able to change from specific mode to 
     * negotiable mode.
     * 
     * @return false if the smart card is not able.<br />
     * true if the smart card is able or if the smart card is already
     * in negotiable mode.
     */
    public boolean isAbleToChangeFromSpecificToNegociableMode()
    {
        if(bIsNegotiable == false)
            return bIsAbleToChangeSpecificNegotiableMode;
        
        // Not applicable already in negotiable mode.
        return true;
    }
    
    /**
     * Returns if the smart card transmission parameters are implicitly known by
     * the terminal or not.
     * 
     * @return false if the smart card transmission parameters are not implicitly
     * knwon by the terminal.<br />
     * true if the smart card transmission parameters are implicitly
     * knwon by the terminal.
     */
    public boolean areTransmissionParametersImplicitlyKnownByTerminal()
    {
        return bAreTransmissionParametersImplicitlyKnownByTerminal;
    }
    
    /**
     * Returns the list of TA.<br />
     * If a TA index is absent of the ATR then the element object will be null.
     * <br /><br />
     * E.g: the ATR contains TA1 TA3 but not TA2 then the list is equals to:
     * TA1, null, TA3.
     * 
     * @return the list of TA.
     */
    public ArrayList<Byte>  getListOfTA()
    {
        return aBTai;
    }
    
    /**
     * Returns the list of TB.<br />
     * If a TB index is absent of the ATR then the element object will be null.
     * <br /><br />
     * E.g: the ATR contains TB1 TB3 but not TB2 then the list is equals to:
     * TB1, null, TB3.
     * 
     * @return the list of TB.
     */
    public ArrayList<Byte>  getListOfTB()
    {
        return aBTbi;
    }
    
    /**
     * Returns the list of TC.<br />
     * If a TC index is absent of the ATR then the element object will be null.
     * <br /><br />
     * E.g: the ATR contains TC1 TC3 but not TC2 then the list is equals to:
     * TC1, null, TC3.
     * 
     * @return the list of TC.
     */
    public ArrayList<Byte>  getListOfTC()
    {
        return aBTci;
    }
    
    /**
     * Returns the list of TD.<br />
     * If a TD index is absent of the ATR then the element object will be null.
     * <br /><br />
     * E.g: the ATR contains TD1 TD3 but not TD2 then the list is equals to:
     * TD1, null, TD3.
     * 
     * @return the list of TD.
     */
    public ArrayList<Byte>  getListOfTD()
    {
        return aBTdi;
    }
    
    /**
     * Returns the list of protocols.<br />
     * A protocol can be 0 (T = 0), 1 (T = 1), 0x0F (specific protocol).
     * 
     * @return the list of protocols.
     */
    public ArrayList<Byte> getListOfProtocols()
    {
        return aBProtocol;
    }
    
    /**
     * Returns the list of transmission types.
     * 
     * @return the list of transmission types.
     */
    public ArrayList<String> getListOfTaTypes()
    {
        return asTaType;
    }
    
    /**
     * Returns the II.<br />
     * II is used to determine the maximum programming current, Ipp, required
     * by the ICC. Not used if PI1 = 0.
     * 
     * @return the II.
     */
    public byte getII()
    {
        return Bii;
    }
     
    /**
     * Returns the WI.<br />
     * WI is used to determine the maximum interval between the leading edge 
     * of the start bit of any character sent by the ICC and the leading edge 
     * of the start bit of the previous character sent either by the ICC or the 
     * terminal (the work waiting time).
     * 
     * @return the WI.
     */
    public byte getWI()
    {
        return BWi;
    }
    
    /**
     * Returns the TS.
     * 
     * @return the TS.
     */
    public byte getTS()
    {
        return BTs;
    }

    /**
     * Returns the TCK.<br />
     * TCK is used to check the integrity of the data sent in the ATR.<br />
     * This byte is not mandatory in the ATR.
     * 
     * @return the TCK.
     */
    public byte getTCK()
    {
        return BTck;
    }

    /**
     * Returns the T0.
     * 
     * @return the T0.
     */
    public byte getT0()
    {
        return BT0;
    }

    /**
     * Returns the PI2.<br />
     * PI2 is used to determine the value of programming 
     * voltage P required by the ICC. When present it overrides the value 
     * indicated by PI1 returned in TB1.
     * 
     * @return the PI2.
     */
    public byte getPI2()
    {
        return BPi2;
    }
    
    /**
     * Returns the PI1.<br />
     * PI1 is used to determine the value of the programming voltage required by 
     * the ICC.<br />
     * If PI1 = 0 then VPP is not connected in the ICC.
     * 
     * @return the PI1.
     */
    public byte getPI1()
    {
        return BPi1;
    }

    /**
     * Returns the N.<br />
     * N is used to indicate the extra guardtime that shall be aded to the
     * minimum duration between the leading edges of the start bits of two 
     * consecutives characters for subsequent exchanges from the terminal to the
     * ICC.
     * 
     * @return the N.
     */
    public byte getN()
    {
        return BN;
    }

    /**
     * Returns the IFSI.<br />
     * IFSI is used to determine the IFSC, and specifies the maximum length of
     * the information field (INF) of blocks that can be received by the card.
     * 
     * @return the IFSI.
     */
    public byte getIFSI()
    {
        return BIfsi;
    }

    /**
     * Returns the Fi.<br />
     * Fi is used to determine the value of F, the clock rate conversion factor,
     * which may be used to modify the frequency of the clock provided by the
     * terminal subsequent to the ATR.
     * 
     * @return the Fi.
     */
    public byte getFI()
    {
        return BFi;
    }

    /**
     * Returns the Di.<br />
     * Di is used to determine the value of D, the bit rate adjustment factor,
     * which may be used to adjust the bit duration used subsequent to the ATR.
     * 
     * @return the Di.
     */
    public byte getDI()
    {
        return BDi;
    }

    /**
     * Returns the CWI.<br />
     * CWI is used to calculate the CWT.
     * 
     * @return the CWI.
     */
    public byte getCWI()
    {
        return BCwi;
    }
          
    /**
     * Returns the BWI.<br />
     * BWI is used to calculate the BWT.
     * 
     * @return the BWI.
     */
    public byte getBWI()
    {
        return BBwi;
    }
    
    /**
     * Returns the block error detection for T = 1 protocol.<br />
     * TC3 Indicates the block error detection for T = 1 protocol.<br /><br />
     * <ul><li>0x00 => for Longitudinal Redundancy Code.</li><br />
     * <li>0x01 => for Cyclic Redundancy Code.</li></ul>
     * 
     * @return the block error detection for T = 1 protocol.
     */
    public byte getBlockErrorDetection()
    {
        return BBlockErrorDetection;
    }

    /**
     * Returns the clock support.<br />
     * Clock is used to indicate whether the card supports clock stop (!= 0x00)
     * or not (= 0x00) and, when supported, which state is preferred on the
     * electrical circuit CLK when the clock is stopped.
     * 
     * @return the clock support.
     */
    public byte getClock()
    {
        return BClock;
    }
    
    /**
     * Returns the current F in function of the TA1.
     * 
     * @return the current F in function of the TA1.
     */
    public int getF()
    {
        return aiF[BFi];
    }
    
    /**
     * Returns the current D in function of the TA1.
     * 
     * @return the current D in function of the TA1.
     */
    public int getD()
    {
        return aiD[BDi];
    }
    
    /**
     * Returns the max possible smart card frequency in function of the TA1.
     * 
     * @return the max possible smart card frequency in function of the TA1.
     */
    public double getClkFMax()
    {
        return afFMax[BFi];
    }
    
    /**
     * Returns the smart card clock frequency.
     * 
     * @return the smart card clock frequency.
     */
    public double getClkF()
    {
        return iClockCardFrequency;
    }
    
    /**
     * Returns the default baudrate of the smart card.
     * 
     * @return the default baudrate of the smart card.
     */
    public double getDefaultBaudRate()
    {
        return Math.round(1 / (372 / 1 * 1.0 / getClkF()));
    }
    
    /**
     * Returns the current baudrate of the smart card.
     * 
     * @return the current baudrate of the smart card.
     */
    public double getBaudRate()
    {
        return Math.round((1 / (((double)getF() / (double)getD()) * ((1.0 / getClkF())))));
    }
    
    /**
     * Returns the BWT.<br />
     * The block waiting time is equal to: 11 etus + 2^BWI * 960 * Fi * Di / F etus.
     * 
     * @return the BWT.
     */
    public double getBlockWaitingTime()
    {
        int _iBWI = (getBWI() < 0) ? (256 + getBWI()) : getBWI();
        return 11 +  ((Math.pow(2, _iBWI) * 960 * getF() * getD()) / getClkF());
    }
    
    /**
     * Returns the CWT.<br />
     * The character waiting time is equal to: 11 etus + 2^CWI etus.
     * 
     * @return the CWT.
     */
    public double getCharacterWaitingTime()
    {
        int _iCWI = (getCWI() < 0) ? (256 + getCWI()) : getCWI();
        return 11 + Math.pow(2, _iCWI);
    }
    
    private int byteToInt(byte aByte)
    {
        if(aByte < 0)
            return 256 + aByte;
        
        return aByte;
    }
    
    /**
     * Returns the EGT.<br />
     * The extra guard time is equal to: 12 etus + Fi / Di * (N / F) etus.<br />
     * The calcul is protocol depending.
     * @param iProtocol The protocol to analyze.
     * @return the EGT.
     */
    public double getExtraGuardTime(int iProtocol)
    {
        double _dR;
        double _dN = byteToInt(getN());
        
        if(_dN == 255)
        {
            if(isSupportedProtocol(iProtocol))
            {
                if(iProtocol == 0)
                    return 12;
                else
                    return 11;
            }
            else
                return 0;
        }
        
        if(isSupportedProtocol(0x0F))
        {
            _dR = getF() / getD();
        }
        else
        {
            _dR = getF() / getD();
        }
        
        return 12 + _dR * (_dN / getClkF());
    }
    
    /**
     * Indicates if a protocol is supported by the smart card or not.
     * @param iProtocol the protocol.<br />
     * It can be:<br /><br />
     * <ul><li>0 for T = 0 protocol</li>
     * <li>1 for T = 1 protocol</li>
     * <li>0x0F for specific protocol</li></ul>
     * 
     * @return if the protocol is supported by the smart card or not.
     */
    public boolean isSupportedProtocol(int iProtocol)
    {
        int _i = 0;
        
        if((iProtocol != 0) && (iProtocol != 1) && (iProtocol != 0x0F))
            return false;
        
        while(_i < aBProtocol.size())
        {
            if(aBProtocol.get(_i) == iProtocol)
                return true;
            _i++;
        }
        
        if((aBProtocol.isEmpty()) && (iProtocol == 0))
            return true;
        
        return false;
    }
    
    /**
     * Returns the voltage class.<br />
     * Class A is 5V => 0x01.<br />
     * Class B is 3V => 0x02.<br />
     * Class C is 1.8V => 0x04.<br />
     * A mix of flags is also possible.
     * 
     * @return the voltage class.
     */
    public byte getVoltageClass()
    {
        return BVoltageClass;
    }
    
    /**
     * Returns a copy of the historical bytes in this ATR.
     * If this ATR does not contain historical bytes, an array of length
     * zero is returned.
     *
     * @return a copy of the historical bytes in this ATR.
     */
    public byte[] getHistoricalBytes() {
        byte[] b = new byte[nHistorical];
        System.arraycopy(atr, startHistorical, b, 0, nHistorical);
        return b;
    }
    
    /**
     * Indicates if the TA with the index is present or not.
     * @param iIndex the index of the TA requested. 1 for TA1. TA0 does not exist.
     *
     * @return if the TA with the index is present or not.
     */
    public boolean isTaPresent(int iIndex)
    {
        if((this.aBTai != null) && (this.aBTai.get(iIndex-1) != null))
            return true;
            
        return false;
    }
    
    /**
     * Indicates if the TB with the index is present or not.
     * @param iIndex the index of the TB requested. 1 for TB1. TB0 does not exist.
     *
     * @return if the TB with the index is present or not.
     */
    public boolean isTbPresent(int iIndex)
    {
        if((this.aBTbi != null) && (this.aBTbi.get(iIndex-1) != null))
            return true;
            
        return false;
    }
    
    /**
     * Indicates if the TC with the index is present or not.
     * @param iIndex the index of the TC requested. 1 for TC1. TC0 does not exist.
     *
     * @return if the TC with the index is present or not.
     */
    public boolean isTcPresent(int iIndex)
    {
        if((this.aBTci != null) && (this.aBTci.get(iIndex-1) != null))
            return true;
            
        return false;
    }
    
    /**
     * Indicates if the TD with the index is present or not.
     * @param iIndex the index of the TD requested. 1 for TD1. TD0 does not exist.
     *
     * @return if the TD with the index is present or not.
     */
    public boolean isTdPresent(int iIndex)
    {
        if((this.aBTdi != null) && (this.aBTdi.get(iIndex-1) != null))
            return true;
            
        return false;
    }

    /**
     * Returns a string representation of this ATR.
     *
     * @return a String representation of this ATR.
     */
    public String toString() {
        StringWriter _sw;
        PrintWriter _pw;
        _sw = new StringWriter(50);
        _pw = new PrintWriter(_sw);

        for(int _m = 0; _m< atr.length; _m++)
            _pw.printf("%02X", atr[_m]);

        return "ATR: " + atr.length + " bytes => " + _sw.toString();
    }
    
    /**
     * Compares the specified object with this ATR for equality.
     * Returns true if the given object is also an ATR and its bytes are
     * identical to the bytes in this ATR.
     *
     * @param obj the object to be compared for equality with this ATR
     * @return true if the specified object is equal to this ATR
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ATR == false) {
            return false;
        }
        ATR other = (ATR)obj;
        return Arrays.equals(this.atr, other.atr);
    }

    /**
     * Returns the hash code value for this ATR.
     *
     * @return the hash code value for this ATR.
     */
    public int hashCode() {
        return Arrays.hashCode(atr);
    }
    
    /**
     * Returns if the ATR has a TCK or not.
     * 
     * @return if the ATR has a TCK or not.
     */
    public boolean hasTCK() {
        return this.m_bTck;
    }
    
    /**
     * Returns the analyze of the historical bytes.
     * @return the analyze of the historical bytes.
     */
    public Object[][] getHistoricalBytesAnalyze()
    {
        analyzeHistoricalBytes();
        return m_ppoHistoricalBytesAnalyze;
    }
    
    // Life cycle status byte, page 21 of ISO 7816-4
    private String getLCS(byte BLcs)
    {
        if((BLcs & 0xF0) > 0)
            return "Proprietary";
        
        switch(BLcs & 0x0F)
        {
            case 0x00:
                return "No information given";
            
            case 0x01:
                return "Creation state";
                
            case 0x03:
                return "Initialisation state";
                
            case 0x04:
            case 0x06:
                return "Operational state (deactivated)";
                
            case 0x05:
            case 0x07:
                return "Operation state (activated)";
                
            case 0x0C:
            case 0x0D:
            case 0x0E:
            case 0x0F:
                return "Termination state";
                
            default:
                return "Unknown";
        }
    }
    
    private int splitStringToAnalyzeHistorical(String sResult, int j)
    {
        String [] _psArray = sResult.split("\n");
        
        for(int _i = 0; _i < _psArray.length; _i++)
        {
            m_ppoHistoricalBytesAnalyze[j][1] = _psArray[_i];
            j++;
        }
        
        return --j;
    }
    
    // Card service data byte, page 59 of ISO 7816-4
    private int getCs(byte BCs, int j)
    {
        String  _sResult = "";
        int     _iTmp = 0;
        
        if((BCs & ((byte)0x80)) != 0)
            _sResult += "Application selection: by full DF name\n";
        
        if((BCs & ((byte)0x40)) != 0)
            _sResult += "Application selection: by partial DF name\n";
        
        if((BCs & ((byte)0x20)) != 0)
            _sResult += "BER-TLV data objects available in EF.DIR\n";
        
        if((BCs & ((byte)0x10)) != 0)
            _sResult += "BER-TLV data objects available in EF.ATR\n";        
        
        if((BCs & ((byte)0x08)) != 0)
            _iTmp += 4;
        
        if((BCs & ((byte)0x04)) != 0)
            _iTmp += 2;
        
        if((BCs & ((byte)0x02)) != 0)
            _iTmp += 1;
        
        _sResult += "EF.DIR and EF.ATR access services: ";
        
        switch(_iTmp)
        {
            case 4:
                _sResult += "by READ BINARY command\n";
                break;
                
            case 0:
                _sResult += "by GET RECORD(s) command\n";
                break;
                
            case 2:
                _sResult += "by GET DATA command\n";
                break;
                
            default:
                _sResult += "reverved for future use\n";
                break;
        }
        
        if((BCs & ((byte)0x01)) != 0)
            _sResult += "Card without MF\n";
        else
            _sResult += "Card with MF\n";
        
        return splitStringToAnalyzeHistorical(_sResult, j);
    }
    
    // First software function table (selection methods),
    // page 60 of ISO 7816-4
    private int getSm(byte BSm, int j)
    {
        String  _sResult = "";
                
        if((BSm & ((byte)0x80)) != 0)
            _sResult += "DF selection by full DF name\n";
        
        if((BSm & ((byte)0x40)) != 0)
            _sResult += "DF selection by partial DF name\n";
        
        if((BSm & ((byte)0x20)) != 0)
            _sResult += "DF selection by path\n";
        
        if((BSm & ((byte)0x10)) != 0)
            _sResult += "DF selection by file identifier\n";
        
        if((BSm & ((byte)0x08)) != 0)
            _sResult += "Implicit DF selection\n";
        
        if((BSm & ((byte)0x04)) != 0)
            _sResult += "Short EF identifier supported\n";
        
        if((BSm & ((byte)0x02)) != 0)
            _sResult += "Record number supported\n";
        
        if((BSm & ((byte)0x01)) != 0)
            _sResult += "Record identifier supported\n";
                         
        return splitStringToAnalyzeHistorical(_sResult, j);               
    }
    
    // Second software function table (data coding byte),
    // page 60 of ISO 7816-4
    private int getDc(byte BDc, int j)
    {
        String  _sResult = "";
        int     _iTmp = 0;
        
        if((BDc & ((byte)0x80)) != 0)
            _sResult += "EF of TLV structure supported\n";
        
        _sResult += "Behavoiur of write functions: ";
        
        if((BDc & ((byte)0x40)) != 0)
            _iTmp +=  2;
        if((BDc & ((byte)0x20)) != 0)
            _iTmp += 1;
        
        switch(_iTmp)
        {
            case 0:
                _sResult += "one-time write\n";
                break;
                
            case 1:
                _sResult += "proprietary\n";
                break;
                
            case 2:
                _sResult += "write OR\n";
                break;
                
            case 3:
                _sResult += "write AND\n";
                break;
                
            default:
                break;
        }
        
        _sResult += "Value 'FF' for the first byte of BER-TLV tag fields: ";
        
        if((BDc & ((byte)0x10)) != 0)
            _sResult += "invalid\n";
        else
            _sResult += "valid\n";
        
        _sResult += "Data Unit in quartets: ";
        
        _iTmp = 0;
        
        if((BDc & ((byte)0x08)) != 0)
            _iTmp += 8; 
        if((BDc & ((byte)0x04)) != 0)
            _iTmp += 4;
        if((BDc & ((byte)0x02)) != 0)
            _iTmp += 2;
        if((BDc & ((byte)0x01)) != 0)
            _iTmp += 1;
        
        _sResult += String.valueOf(Math.pow(2, _iTmp)) + "\n";
        
        return splitStringToAnalyzeHistorical(_sResult, j);
    }
    
    // Third software function table (command chaining,
    // length fields and logical channels), page 61 of ISO 7816-4
    private int getCc(byte BCc, int j)
    {
        String  _sResult = "";
        int     _iTmp = 0;
        
        if((BCc & ((byte)0x80)) != 0)
            _sResult += "Command chaining\n";
        
        if((BCc & ((byte)0x40)) != 0)
            _sResult += "Extended Lc and Le fields\n";
        
        if((BCc & ((byte)0x20)) != 0)
            _sResult += "RFU (should not happen)\n";
        
        _sResult += "Logical channel number assignment: ";
        
        if((BCc & ((byte)0x10)) != 0)
            _iTmp +=  2;
        if((BCc & ((byte)0x08)) != 0)
            _iTmp += 1;
        
        switch(_iTmp)
        {
            case 0:
                _sResult += "No logical channel\n";
                break;
                
            case 1:
                _sResult += "by the interface device\n";
                break;
                
            case 2:
                _sResult += "by the card\n";
                break;
                
            case 3:
                _sResult += "by the interface device and card\n";
                break;
                
            default:
                break;
        }
        
        _iTmp = 0;
        
        if((BCc & ((byte)0x04)) != 0)
            _iTmp += 4;
        if((BCc & ((byte)0x02)) != 0)
            _iTmp += 2;
        if((BCc & ((byte)0x01)) != 0)
            _iTmp += 1;
        
        _sResult += "Maximum numbre of logical channels: " + _iTmp + "\n";
        
        return splitStringToAnalyzeHistorical(_sResult, j);
    }
        
    private int analyzeTLV(int j, int iEnd)
    {
        int     _i = 1;
        byte[]  _tmppByte;
        
        while(_i + iEnd < nHistorical)
        {
            int     _j = 0;
            byte    _BTag = (byte) (((byte) (atr[startHistorical + _i] & 0xF0)) >> 4);
            int     _iLength = (atr[startHistorical + _i] & 0x0F);
            
            m_ppoHistoricalBytesAnalyze[++j][0] = byteToString(
                    atr[startHistorical + _i]);
            m_ppoHistoricalBytesAnalyze[j][1] = "Tag: " + byteToString(_BTag) + 
                    " - Length: " + _iLength + " - ";

            switch(_BTag)
            {
                case 0x01:
                    m_ppoHistoricalBytesAnalyze[j][1] += " (Country Code, "
                            + "ISO 3166-1)";
                    
                    ++j;
                    
                    m_ppoHistoricalBytesAnalyze[j][0] = "";
                    for(_j = 0; _j < _iLength-_i; _j++)
                    {
                        m_ppoHistoricalBytesAnalyze[j][0] += 
                                byteToString(atr[startHistorical + _i + _j + 1]);
                    }
                    
                    break;

                case 0x02:
                    m_ppoHistoricalBytesAnalyze[j][1] += " (Issuer "
                            + "Identification Number, "
                            + "ISO 7812-1)";
                    
                    ++j;
                    
                    m_ppoHistoricalBytesAnalyze[j][0] = "";
                    for(_j = 0; _j < _iLength-_i; _j++)
                    {
                        m_ppoHistoricalBytesAnalyze[j][0] += 
                                byteToString(atr[startHistorical + _i + _j + 1]);
                    }

                    break;

                case 0x03:
                    m_ppoHistoricalBytesAnalyze[j][1] += " (Card Service Data "
                            + "Byte)";
                    
                    m_ppoHistoricalBytesAnalyze[++j][0] = 
                                byteToString(atr[startHistorical + _i + 1]);
                    
                    j = getCs(atr[startHistorical + _i + 1], j);
                    break;

                case 0x04:
                    m_ppoHistoricalBytesAnalyze[j][1] += " (Initial Access Data)";
                    
                    ++j;
                    
                    m_ppoHistoricalBytesAnalyze[j][0] = "";
                    for(_j = 0; _j < _iLength-_i; _j++)
                    {
                        m_ppoHistoricalBytesAnalyze[j][0] += 
                                byteToString(atr[startHistorical + _i + _j + 1]);
                    }

                    break;

                case 0x05:
                    m_ppoHistoricalBytesAnalyze[j][1] += " (Card Issuer's Data)";
                    
                    ++j;
                    
                    m_ppoHistoricalBytesAnalyze[j][0] = "";
                    for(_j = 0; _j < _iLength-_i; _j++)
                    {
                        m_ppoHistoricalBytesAnalyze[j][0] += 
                                byteToString(atr[startHistorical + _i + _j + 1]);
                    }
                    break;

                case 0x06:
                    m_ppoHistoricalBytesAnalyze[j][1] += " (Pre-Issuing Data)";
                    
                    ++j;
                    
                    m_ppoHistoricalBytesAnalyze[j][0] = "";
                    for(_j = 0; _j < _iLength-_i; _j++)
                    {
                        m_ppoHistoricalBytesAnalyze[j][0] += 
                                byteToString(atr[startHistorical + _i + _j + 1]);
                    }

                    break;

                case 0x07:
                    m_ppoHistoricalBytesAnalyze[j][1] += " (Card Capabilities)";
                    
                    ++j;
                    
                    switch(_iLength)
                    {
                        case 0x01:
                            m_ppoHistoricalBytesAnalyze[j][0] = byteToString(
                                    atr[startHistorical + _i + 1]);
                            m_ppoHistoricalBytesAnalyze[j][1] = "Selection "
                                    + "Methods";
                            
                            j++;
                                    
                            j = getSm(atr[startHistorical + _i + 1], j);
                            break;

                        case 0x02:
                            m_ppoHistoricalBytesAnalyze[j][0] = byteToString(
                                    atr[startHistorical + _i + 1]);
                            m_ppoHistoricalBytesAnalyze[j][1] = "Selection "
                                    + "Methods";
                            
                            j++;
                                    
                            j = getSm(atr[startHistorical + _i + 1], j);
                            
                            j++;
                            
                            m_ppoHistoricalBytesAnalyze[j][0] = byteToString(
                                    atr[startHistorical + _i + 2]);
                            m_ppoHistoricalBytesAnalyze[j][1] = "Data Coding "
                                    + "Byte";
                            
                            j++;
                            
                            j = getDc(atr[startHistorical + _i + 2], j);
                            break;

                        case 0x03:
                            m_ppoHistoricalBytesAnalyze[j][0] = byteToString(
                                    atr[startHistorical + _i + 1]);
                            m_ppoHistoricalBytesAnalyze[j][1] = "Selection "
                                    + "Methods";
                            
                            j++;
                                    
                            j = getSm(atr[startHistorical + _i + 1], j);
                            
                            j++;
                            
                            m_ppoHistoricalBytesAnalyze[j][0] = byteToString(
                                    atr[startHistorical + _i + 2]);
                            m_ppoHistoricalBytesAnalyze[j][1] = "Data Coding "
                                    + "Byte";
                            
                            j++;
                            
                            j = getDc(atr[startHistorical + _i + 2], j);
                            
                            j++;
                            
                            m_ppoHistoricalBytesAnalyze[j][0] = byteToString(
                                    atr[startHistorical + _i + 3]);
                            m_ppoHistoricalBytesAnalyze[j][1] = "Command "
                                    + "Chaining, Length Fields and Logical "
                                    + "Channels";
                            
                            j++;
                            
                            j = getCc(atr[startHistorical + _i + 3], j);
                            break;

                        default:
                            break;                                
                    }
                    break;

                case 0x08:
                    m_ppoHistoricalBytesAnalyze[j][1] += " (Status Indicator)";
                                        
                    switch(_iLength)
                    {
                        case 0x01:
                            m_ppoHistoricalBytesAnalyze[++j][0] = byteToString(
                            atr[nHistorical + startHistorical - 3]);
                            m_ppoHistoricalBytesAnalyze[j][1] = "LCS (Life "
                                    + "Card Cycle)";
                            break;

                        case 0x02:
                            _tmppByte = new byte[2];

                            _tmppByte[0] = atr[nHistorical + startHistorical 
                                    - 2];
                            _tmppByte[1] = atr[nHistorical + startHistorical 
                                    - 1];

                            m_ppoHistoricalBytesAnalyze[++j][0] = 
                                    pByteToString(_tmppByte);
                            m_ppoHistoricalBytesAnalyze[j][1] = "SW (Status "
                                    + "Word)";
                            break;

                        case 0x03:
                            m_ppoHistoricalBytesAnalyze[++j][0] = byteToString(
                            atr[nHistorical + startHistorical - 3]);
                            m_ppoHistoricalBytesAnalyze[j][1] = "LCS (Life "
                                    + "Card Cycle)";

                            m_ppoHistoricalBytesAnalyze[++j][0] = "";
                            m_ppoHistoricalBytesAnalyze[j][1] = getLCS(
                                    atr[nHistorical + 
                                    startHistorical - 3]);

                            _tmppByte = new byte[2];

                            _tmppByte[0] = atr[nHistorical + startHistorical 
                                    - 2];
                            _tmppByte[1] = atr[nHistorical + startHistorical 
                                    - 1];

                            m_ppoHistoricalBytesAnalyze[+j][0] = 
                                    pByteToString(_tmppByte);
                            m_ppoHistoricalBytesAnalyze[j][1] = "SW (Status "
                                    + "Word)";
                            break;

                        default:
                            break;
                    }
                    break;

                case 0x0F:
                    m_ppoHistoricalBytesAnalyze[j][1] +=  " (Application "
                            + "Identifier)\n";
                    
                    ++j;
                    
                    m_ppoHistoricalBytesAnalyze[j][0] = "";
                    for(_j = 0; _j < _iLength-_i; _j++)
                    {
                        m_ppoHistoricalBytesAnalyze[j][0] += 
                                byteToString(atr[startHistorical + _i + _j + 1]);
                    }

                    break;

                default:
                    m_ppoHistoricalBytesAnalyze[j][1] +=  " (Unknown)\n";
                    
                    ++j;

                    m_ppoHistoricalBytesAnalyze[j][0] = "";
                    for(_j = 0; _j < _iLength-_i; _j++)
                    {
                        m_ppoHistoricalBytesAnalyze[j][0] += 
                                byteToString(atr[startHistorical + _i + _j + 1]);
                    }

                    break;
            }
            
            _i += _iLength + 1;
        }
        
        return j;
    }
    
    private String pByteToString(byte[] command)
    {
        if(command == null)
            return "";
        
        StringWriter _sw;
        PrintWriter _pw;
        _sw = new StringWriter(50);
        _pw = new PrintWriter(_sw);

        for(int _m=0; _m<command.length; _m++)
            _pw.printf("%02X", command[_m]);

        return (_sw.toString());
    }
    
    private String byteToString(byte bByte)
    {
        byte[] _pByte = new byte[1];
        _pByte[0] = bByte;
        return pByteToString(_pByte);
    }
    
    private void analyzeHistoricalBytes()
    {
        int     _j = 0;  
        byte[]  _tmppByte;
        
        m_ppoHistoricalBytesAnalyze = new Object[200][2];
        
        if(nHistorical == 0)
            return;
        
        m_ppoHistoricalBytesAnalyze[_j][0] = byteToString(atr[startHistorical]);
        m_ppoHistoricalBytesAnalyze[_j][1] = "Category indicator byte: ";
        
        switch(atr[startHistorical])
        {
            case 0x00:
                m_ppoHistoricalBytesAnalyze[_j][1] += 
                        " (compact TLV data object)\n";
                
                _j = analyzeTLV(_j, 3);  
                
                _tmppByte = new byte[3];
                
                _tmppByte[0] = atr[nHistorical + startHistorical - 3];
                _tmppByte[1] = atr[nHistorical + startHistorical - 2];
                _tmppByte[2] = atr[nHistorical + startHistorical - 1];
                                
                m_ppoHistoricalBytesAnalyze[++_j][0] = pByteToString(_tmppByte);
                m_ppoHistoricalBytesAnalyze[_j][1] = "Mandatory status "
                        + "indicator (3 last bytes)";
                
                m_ppoHistoricalBytesAnalyze[++_j][0] = byteToString(
                        atr[nHistorical + startHistorical - 3]);
                m_ppoHistoricalBytesAnalyze[_j][1] = "LCS (Life Card Cycle)";
                
                m_ppoHistoricalBytesAnalyze[++_j][0] = "";
                m_ppoHistoricalBytesAnalyze[_j][1] = getLCS(atr[nHistorical + 
                        startHistorical - 3]);
                
                _tmppByte = new byte[2];
                
                _tmppByte[0] = atr[nHistorical + startHistorical - 2];
                _tmppByte[1] = atr[nHistorical + startHistorical - 1];
                
                m_ppoHistoricalBytesAnalyze[++_j][0] = pByteToString(
                        _tmppByte);
                m_ppoHistoricalBytesAnalyze[_j][1] = "SW (Status Word)";
                break;

            case (byte)0x80:
                m_ppoHistoricalBytesAnalyze[_j][1] += 
                        " (compact TLV data object)\n";
                _j = analyzeTLV(_j, 0);
                break;

            case 0x10:
                m_ppoHistoricalBytesAnalyze[++_j][0] = byteToString(
                        atr[startHistorical + 1]);
                m_ppoHistoricalBytesAnalyze[_j][1] = "DIR data reference"; 
                break;

            case (byte)0x81:
            case (byte)0x82:
            case (byte)0x83:
            case (byte)0x84:
            case (byte)0x85:
            case (byte)0x86:
            case (byte)0x87:
            case (byte)0x88:
            case (byte)0x89:
            case (byte)0x8A:
            case (byte)0x8B:
            case (byte)0x8C:
            case (byte)0x8D:
            case (byte)0x8E:
            case (byte)0x8F:    
                m_ppoHistoricalBytesAnalyze[_j][1] += " (RFU)\n";
                break;

            default:
                m_ppoHistoricalBytesAnalyze[_j][1] += " (proprietary format)\n";
                break;
        }  
        
        Object[][] _finalObj = new Object[_j+1][2];
        
        int _i = 0;
            
        while(_i <= _j)
        {
            _finalObj[_i][0] = m_ppoHistoricalBytesAnalyze[_i][0];
            _finalObj[_i][1] = m_ppoHistoricalBytesAnalyze[_i][1];
            
            _i++;
        }
        
        m_ppoHistoricalBytesAnalyze = _finalObj;
    }
}
