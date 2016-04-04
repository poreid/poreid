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

import org.poreid.pcscforjava.PCSCResource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.JOptionPane;

/**
 * Platform specific code and constants
 *
 * @since   1.6
 * @author  Matthieu Leromain
 */

class PlatformPCSC {

    static final Throwable initException;
    
    PlatformPCSC() {
        // empty
    }

    static {
        initException = loadLibrary();
    }
    
    /**
     * Allows to define the name of the operating system on which the program is
     * running.
     * @return String operating system name.
     */
    private static String getPlatformName()
    {
        return(System.getProperty("os.name"));
    }

    /**
     * Allows to define if the program is running on a Linux operating system.
     * @return boolean true if plateform Linux.
     */
    private static boolean isPlatformLinux()
    {
        return(getPlatformName().contains("Linux"));
    }

    /**
     * Allows to define if the program is running on a Mac operating system.
     * @return boolean true if plateform Mac.
     */
    private static boolean isPlatformMac()
    {
        return(getPlatformName().contains("Mac"));
    }

    /**
     * Allows to define if the program is running on a Windows operating system.
     * @return boolean true if plateform Windows.
     */
    private static boolean isPlatformWindows()
    {
        return(getPlatformName().contains("Windows"));
    }

    /**
     * Allows to define if the program is running on a 64 bits operating system.
     * @return true if there is an 64 bits operating system or false otherwise.
     */
    private static boolean isPlatform64Bits()
    {
        // X86_64 ; amd64 ...
        return(System.getProperty("os.arch").indexOf("64") != -1);
    }
    
    /**
     * Allows to load a JNI
     * @param sName the name of the JNI.
     * @return true if the library is correctly loaded false otherwise.
     */
    private static boolean loadPCSCLibrary (String sName)    
    {
        boolean _bSuccess;
        String _sPrefix = "";
        String _sExtension = "";
        String _sFinalName = "";

        try {
            if(isPlatformMac())
            {
                _sPrefix = "lib";
                _sExtension = ".dylib";
            }
            else if(isPlatformLinux())
            {
                _sPrefix = "lib";
                _sExtension = ".so";
            }
            else if(isPlatformWindows())
                _sExtension = ".dll";
            else
                throw new Exception();
            
            if(!isPlatformMac())
            {
                sName += (isPlatform64Bits()) ? "64" : "32";
                sName += "bits";
            }
            else
            {
                sName += "Univ";
            }
            
            _sFinalName = _sPrefix + sName + _sExtension;

            deleteAllTemporariesFiles(_sFinalName, _sExtension);
            
            // Retrieve the temporary DLL
            File _theDll = createTemporaryFile(_sFinalName, _sExtension);
            // Load the DLL from the filesystem
            System.load(_theDll.getAbsolutePath());
            
            _theDll.deleteOnExit();
        } catch (Exception _e) 
        {
            // The library is not available
            JOptionPane.showMessageDialog(null, "The library: " + _sFinalName +
                    " was not found!\n\n"
                    + "There is some possible reasons for "
                    + "this:\n"
                    + "\tThe platform may be not supported by the PCSC4Java - "
                    + "framework.\n"
                    + "\tThe library has been deleted in the PCSC4Java -"
                    + "framework file.\n\n"
                    + "The consequence is that the PCSC service will not be "
                    + "work from your application on this platform.\n\n"
                    + "If you have any doubt please contact the application "
                    + "developer.",
                    "Error: PCSC4Java - framework -> Library not found!",
                    JOptionPane.ERROR_MESSAGE);
            
            PCSCResource.setLibraryName(_sFinalName + "###");
            
            return false;
        }
        
        PCSCResource.setLibraryName(_sFinalName);
        
        //System.out.println("lib " + _sFinalName + " loaded");
        return true;
	}
    
    /**
     * Deletes all the temporaries files of PCSC4Java which would be always 
     * present.
     * @param sName the name of the temporaries files.
     * @param sExtension the extension of the temporaries files.
     */
    private static void deleteAllTemporariesFiles(String sName, 
            String sExtension)
    {
        String  _sPrefix = sName.replaceAll(sExtension, "");
        File    _tmp;
        File    _folderTmp = new File(System.getProperty("java.io.tmpdir"));
        String[] _files = _folderTmp.list();
        int      _i = 0;
        
        while(_i < _files.length)
        {
            if(_files[_i].contains(_sPrefix))
            {
                _tmp = new File(System.getProperty("java.io.tmpdir") + 
                        File.separator + _files[_i]);
                _tmp.delete();
            }
            _i++;
        }
    }
    
    /**
     * Function which allows to copy a jar file to the filesystem.
     * @param sName the name of the jar file.
     * @param sExtension the extension of the jar file.
     * @return the temporary File.
     * @throws java.io.IOException If an IO exception occurs.
     */
    private static File createTemporaryFile(String sName, String sExtension)
    {
        InputStream _is = null;
        try {
            URL _url = PlatformPCSC.class.getResource(sName);
            _is = _url.openStream();
            /* Define the destination file */
            File _theDll = File.createTempFile(sName.replace(sExtension, ""), 
                    sExtension);
            
            _theDll.deleteOnExit();
            /* Open the destination file */
            FileOutputStream _fos = new FileOutputStream(_theDll);
            /* Copy the DLL from the JAR to the filesystem */
            byte[] _array = new byte[1024*4];
            for (int _i = _is.read(_array); _i != -1; _i = _is.read(_array)) {
                _fos.write(_array, 0, _i);
            }
            /* Close all streams */
            _fos.close();
            _is.close();
            
            return _theDll;
        } catch (IOException ex) {
            return null;
        }
    }
    
    /**
     * Loads the library.
     * @return null if success otherwise an exception.
     */
    private static Throwable loadLibrary() {
        try {
            //AccessController.doPrivileged(new LoadLibraryAction("PCSC"));
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run()
                {
                    boolean _bLoad = loadPCSCLibrary("PCSC4Java");
                    return null;
                }
            });
            
            return null;
        } catch (Throwable e) {
            // The library is not available
            JOptionPane.showMessageDialog(null, "The library: " +
                    " was not found!\n\n"
                    + e.getMessage(),
                    "Error: PCSC4Java - framework -> Library not found!",
                    JOptionPane.ERROR_MESSAGE);
            
            return e;
        }
    }
   
    /**
     * Converts an unsigned byte to integer.
     * @param b the byte.
     * @return the unsigned int.
     */
    public static int unsignedByteToInt(byte b) 
    {
        return (int) b & 0xFF;
    }

    /**
     * Undefined protocol.
     */
    final static int SCARD_PROTOCOL_UNDEFINED   =  0x0000;
    /**
     * T = 0 protocol.
     */
    final static int SCARD_PROTOCOL_T0     =  0x0001;
    /**
     * T = 1 protocol.
     */    
    final static int SCARD_PROTOCOL_T1     =  0x0002;
    /**
     * T = 0 or T = 1 protocol.
     */    
    final static int SCARD_PROTOCOL_Tx = 
            (SCARD_PROTOCOL_T0 | SCARD_PROTOCOL_T1);
        
    
    // PCSC constants defined differently under Windows and MUSCLE
    
    /**
     * Windows
     */
    
    /**
     * Raw protocol.
     */
    final static int  SCARD_PROTOCOL_RAW    =  0x10000;
    
    /**
     * Unknown card.
     */
    final static int SCARD_UNKNOWN         =  0x0000;
    /**
     * Absent card.
     */
    final static int SCARD_ABSENT          =  0x0001;
    /**
     * Present card.
     */
    final static int SCARD_PRESENT         =  0x0002;
    /**
     * Swallowed card.
     */
    final static int SCARD_SWALLOWED       =  0x0003;
    /**
     * Powered card.
     */
    final static int SCARD_POWERED         =  0x0004;
    /**
     * Negotiable card.
     */
    final static int SCARD_NEGOTIABLE      =  0x0005;
    /**
     * Specific card.
     */
    final static int SCARD_SPECIFIC        =  0x0006;
    
    /**
     * MUSCLE
     */
    
    /**
     * Raw protocol.
     */
    final static int SCARD_PROTOCOL_RAW_MUSCLE  =  0x0004;
    
    /**
     * Unknown card.
     */
    final static int SCARD_UNKNOWN_MUSCLE       =  0x0001;
    /**
     * Absent card.
     */
    final static int SCARD_ABSENT_MUSCLE        =  0x0002;
    /**
     * Present card.
     */
    final static int SCARD_PRESENT_MUSCLE       =  0x0004; 
    /**
     * Swallowed card.
     */
    final static int SCARD_SWALLOWED_MUSCLE     =  0x0008; 
    /**
     * Powered card.
     */
    final static int SCARD_POWERED_MUSCLE       =  0x0010; 
    /**
     * Negotiable card.
     */
    final static int SCARD_NEGOTIABLE_MUSCLE    =  0x0020; 
    /**
     * Specific card.
     */
    final static int SCARD_SPECIFIC_MUSCLE      =  0x0040;
}
