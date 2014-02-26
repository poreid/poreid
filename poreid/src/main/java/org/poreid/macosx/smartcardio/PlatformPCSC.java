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
package org.poreid.macosx.smartcardio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 *
 * @author POReID
 */
public class PlatformPCSC {
    private static final String PCSC_JNI_LIBRARY_NAME = "/libj2pcsc.dylib";
    private static final String PCSC_FRAMEWORK = "/System/Library/Frameworks/PCSC.framework/Versions/Current/PCSC";
 
    public static final int SCARD_PROTOCOL_T0  = 0x0001;
    public static final int SCARD_PROTOCOL_T1  = 0x0002;
    public static final int SCARD_PROTOCOL_RAW = 0x0004;
    public static final int SCARD_UNKNOWN      = 0x0001;
    public static final int SCARD_ABSENT       = 0x0002;
    public static final int SCARD_PRESENT      = 0x0004;
    public static final int SCARD_SWALLOWED    = 0x0008;
    public static final int SCARD_POWERED      = 0x0010;
    public static final int SCARD_NEGOTIABLE   = 0x0020;
    public static final int SCARD_SPECIFIC     = 0x0040;

    public static final Exception initException;

    static {
        initException = AccessController.doPrivileged(new PrivilegedAction<Exception>() {
            @Override
            public Exception run() {
                try {
                    try (InputStream is = PlatformPCSC.class.getResourceAsStream(PCSC_JNI_LIBRARY_NAME)) {

                        if (null == is) {
                            return new FileNotFoundException(PCSC_JNI_LIBRARY_NAME + " not found.");
                        }

                        String[] fileName = PCSC_JNI_LIBRARY_NAME.split("\\.");
                        File tempFile = File.createTempFile(fileName[0], fileName[1]);
                        tempFile.deleteOnExit();

                        try (OutputStream os = new FileOutputStream(tempFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;

                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }

                            System.load(tempFile.getAbsolutePath());
                            initialize(PCSC_FRAMEWORK);
                        }
                    }
                    return null;
                } catch (IOException ex) {
                    return ex;
                }
            }
        });
    }

   
    private static native void initialize(String libraryName);
}
