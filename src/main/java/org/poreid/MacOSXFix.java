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

import java.security.Security;
import java.util.Locale;
import java.util.ResourceBundle;
import org.poreid.config.POReIDConfig;
import org.poreid.dialogs.dialog.DialogController;
import org.poreid.macosx.smartcardio.MacOSXCustomPCSCProvider;


/**
 *
 * @author POReID
 */
final class MacOSXFix {
    private static final String JRE_OS_NAME = "os.name";
    private static final String TARGET_OS_NAME = "Mac OS X";
    private static final String JRE_VERSION = "1.8";
    
    
    protected static boolean usePOReIDPCSCProvider(){
         if (System.getProperty(JRE_OS_NAME).equalsIgnoreCase(TARGET_OS_NAME)) {
             if (!System.getProperty("java.version").startsWith(JRE_VERSION)){
                 Locale locale = POReIDConfig.getDefaultLocale();
                 ResourceBundle bundle = ResourceBundle.getBundle(MacOSXFix.class.getSimpleName(),locale);
                 DialogController.getInstance(bundle.getString("warning.jre.title"), bundle.getString("warning.jre.message"), locale, false).displayDialog();
             }
             useProvider();
             return true;
         }
         return false;
    }
    
    
    private static void useProvider() {
        Security.addProvider(new MacOSXCustomPCSCProvider());
    }
}
