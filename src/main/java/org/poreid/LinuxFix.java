/*
 * The MIT License
 *
 * Copyright 2014 Rui Martinho (rmartinho@gmail.com), António Braz (antoniocbraz@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.poreid;

import java.io.File;

/**
 *
 * @author POReID
 */
 final class LinuxFix {
    private static final String PCSC_LIBRARY_NAME = "pcsclite";
    private static final int PCSC_LIBRARY_SO_NUMBER = 1;
    private static final String LINUX_MULTIARCH_GNU_TRIPLET_i386 = "i386-linux-gnu";                            // Debian & Ubuntu based distros
    private static final String LINUX_MULTIARCH_GNU_TRIPLET_x86_64 = "x86_64-linux-gnu";                        // Debian & Ubuntu based distros
    private static final String MULTIARCH_i386_LIBRARY_PATH = "/lib/" + LINUX_MULTIARCH_GNU_TRIPLET_i386;       // Debian & Ubuntu based distros
    private static final String MULTIARCH_x86_64_LIBRARY_PATH = "/lib/" + LINUX_MULTIARCH_GNU_TRIPLET_x86_64;   // Debian & Ubuntu based distros
    private static final String SMARTCARDIO_LIBRARY_PATH = "sun.security.smartcardio.library";
    private static final String JAVA_LIBRARY_PATH = "java.library.path";
    private static final String JRE_BIT_ARCHITECTURE = "os.arch";
    private static final String JRE_OS_NAME = "os.name";
    private static final String JRE_32_BIT_LINUX = "i386";
    private static final String JRE_64_BIT_LINUX = "amd64";
    private static final String TARGET_OS_NAME = "Linux";
    
    protected static void locateLibpcsclite() {
        if (System.getProperty(JRE_OS_NAME).equalsIgnoreCase(TARGET_OS_NAME)) {
            String bitArch = System.getProperty(JRE_BIT_ARCHITECTURE);
            String javaLibraryPath = System.getProperty(JAVA_LIBRARY_PATH);
            String pcscLibraryName = System.mapLibraryName(PCSC_LIBRARY_NAME) + "." + PCSC_LIBRARY_SO_NUMBER;
            String addPath = "";
            
            if (new File(MULTIARCH_i386_LIBRARY_PATH).isDirectory() && bitArch.equalsIgnoreCase(JRE_32_BIT_LINUX)) {
                    addPath = File.pathSeparator + MULTIARCH_i386_LIBRARY_PATH;
            }
            
            if (new File(MULTIARCH_x86_64_LIBRARY_PATH).isDirectory()) {
                if (bitArch.equalsIgnoreCase(JRE_64_BIT_LINUX)) {
                    addPath = File.pathSeparator + MULTIARCH_x86_64_LIBRARY_PATH;
                } else {
                    addPath = File.pathSeparator + MULTIARCH_i386_LIBRARY_PATH;
                }
            }
                       
            javaLibraryPath += addPath;
                       
            for (String libraryPath : javaLibraryPath.split(File.pathSeparator)) {
                final File library = new File(libraryPath, pcscLibraryName);
                if (library.exists()) {
                    System.setProperty(SMARTCARDIO_LIBRARY_PATH, library.getAbsolutePath());
                    return;
                }
            }
        }
    }
}