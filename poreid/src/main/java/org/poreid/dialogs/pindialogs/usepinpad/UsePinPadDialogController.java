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

package org.poreid.dialogs.pindialogs.usepinpad;

import java.util.Locale;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import org.poreid.Pin;
import org.poreid.common.Util;

/**
 *
 * @author POReID
 */
public class UsePinPadDialogController {
    private static String infoMsg;
    final private Pin pin;    
    private Locale locale;
    JDialog dialog;
    PinOperation pinOp;
    
    
    private UsePinPadDialogController(PinOperation operacao, Pin pin, Locale locale) {
        try {
            Util.setLookAndFeel();
            this.pin = pin;            
            this.locale = locale;            
            this.pinOp = operacao;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo de requisição de pin");
        }
    }
    
    
    public static UsePinPadDialogController getInstance(PinOperation operacao, Pin pin, Locale locale){
        return new UsePinPadDialogController(operacao, pin, locale);
    }
    
    
    public synchronized void displayVerifyPinPinPadDialog(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (null != infoMsg && !infoMsg.isEmpty()){
                    UsePinPadDialogController.this.dialog = new UsePinPadDialog(pinOp,pin.getLabel(), pin.getBackground(), locale, infoMsg);
                } else {
                    UsePinPadDialogController.this.dialog = new UsePinPadDialogSmall(pinOp,pin.getLabel(), pin.getSmallBackground(), locale);
                }
                UsePinPadDialogController.this.dialog.setAlwaysOnTop(true);
                UsePinPadDialogController.this.dialog.setVisible(true);
                UsePinPadDialogController.this.dialog.requestFocusInWindow();
            }
        });
    }
    
    
    public synchronized void disposeVerifyPinPinPadDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.dispose();
            }
        });
    }
    
    
    public static void setInfoMessage(String infoMsg){
        UsePinPadDialogController.infoMsg = infoMsg;
    }
    
    
    public static void removeInfoMessage(){
        UsePinPadDialogController.infoMsg = null;
    }
}
