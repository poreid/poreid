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

package org.poreid.dialogs.pindialogs.blockedpin;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.poreid.config.POReIDConfig;

/**
 *
 * @author POReID
 */
public class BlockedPinDialogController {
    private String pinLabel;
    private Locale locale;
    
    private BlockedPinDialogController(String pinLabel, Locale locale) {
        try {
            if (null != UIManager.getLookAndFeel()) {
                UIManager.setLookAndFeel(POReIDConfig.LAF);
            }
            this.pinLabel = pinLabel;
            this.locale = locale;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo de informação de pin bloqueado.", ex);
        }
    }
    
  
    public static BlockedPinDialogController getInstance(String pinLabel, Locale locale){
        return new BlockedPinDialogController(pinLabel, locale);
    }
    
    
    public void displayBlockedPinDialog(){
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    BlockedPinDialog dialog = new BlockedPinDialog(pinLabel, locale);
                    dialog.setVisible(true);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo de informação de pin bloqueado.", ex);
        }
    }
}
