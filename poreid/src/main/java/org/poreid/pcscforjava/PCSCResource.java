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
 * Class which supplies some resources funcitons for the library.
 *
 * @since   1.6
 * @author  Matthieu Leromain
 */
public class PCSCResource 
{
    final static String     PCSC4JAVA_VERSION = "1.0.2";
    
    /**
     * The library name.
     */
    static String           m_sLibraryName = "";
    
    /**
     * No constructor static class
     */
    private PCSCResource()
    {
        // No constructor static class.
    }
    
    /**
     * Returns the version double of the library.
     * @return the version double of the library.
     */
    public static double getDecVersion()
    {
        String _sTmp = getVersion();
        _sTmp = _sTmp.substring(0, 3);
        double _dTmp = Double.parseDouble(_sTmp);
        return _dTmp;
    }
    
    /**
     * Returns the version string of the library.
     * @return the version string of the library.
     */
    public static String getVersion()
    {
        return PCSC4JAVA_VERSION;
    }
    
    /**
     * Returns the used JNI library name.
     * @return the used JNI library name.
     */
    public static String getLibraryName()
    {
        if(m_sLibraryName.contains("###"))
        {
            m_sLibraryName = m_sLibraryName.replaceAll("###", "");
            return "Impossible to load the library: " + m_sLibraryName;
        }
        return m_sLibraryName;
    }  
    
    /**
     * Sets the used JNI library name.
     * @param sLibraryName the used JNI library name.
     */
    public static void setLibraryName(String sLibraryName)
    {
        if(m_sLibraryName.isEmpty())
            m_sLibraryName = sLibraryName;
    }
    
    /**
     * Returns the Plug & Play status string.
     * @return "Enabled" if the Plug & Play is enabled.<br />
     * "Disabled" if the Plug & Play is disabled.
     */
    public static String getPlugAndPlayStatus()
    {
        try 
        {
            CardTerminals _terms = TerminalFactory.getDefault().terminals();
            
            if(_terms != null)
            {
                if(_terms.isPlugAndPlaySupported())
                    return "Enabled";
                else
                    return "Disabled";
            }
            
            return "Disabled";
        } 
        catch (CardException ex) 
        {
            return "Disabled";
        }
    }
}
