/*
 * The MIT License
 *
 * Copyright 2014, 2015, 2016 Rui Martinho (rmartinho@gmail.com), António Braz (antoniocbraz@gmail.com)
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
package org.poreid.dialogs.pindialogs.otpfeedback;

import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import org.poreid.common.Util;

/**
 *
 * @author POReID
 */
public class OTPFeedbackDialogController {
    private String pinLabel;
    private Locale locale;
    private OTPFeedbackDialog dialog;
    
    private OTPFeedbackDialogController(String pinLabel, Locale locale) {
        try {
            Util.setLookAndFeel();
            this.pinLabel = pinLabel;
            this.locale = locale;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo de progresso relativa à alteração do pin.", ex);
        }
    }
    
  
    public static OTPFeedbackDialogController getInstance(String pinLabel, Locale locale){
        return new OTPFeedbackDialogController(pinLabel, locale);
    }
    
    
    public void displayOTPFeedbackDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog = new OTPFeedbackDialog(pinLabel, locale);
                dialog.setVisible(true);
                dialog.requestFocusInWindow();
            }
        });

    }
    
    
    public void updateState() {
        if (null != dialog) {
            dialog.updateState();
        }
    }
    
    
    public void closeDialog(){
        dialog.dispose();
        dialog = null;
    }
}

