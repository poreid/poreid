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
package org.poreid.dialogs.pindialogs.modifypin;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.poreid.config.POReIDConfig;
import org.poreid.dialogs.DialogEventListener;
import org.poreid.dialogs.pindialogs.PinEntryCancelledException;

/**
 *
 * @author POReID
 */
public class ModifyPinDialogController {
    private final String pinLabel;
    private final int pinMinLength;
    private final int pinMaxLength;
    private final Locale locale;
    private boolean cancelled;
    private ByteBuffer newPin;
    private ByteBuffer currentPin;
    
    private ModifyPinDialogController(String pinLabel, int pinMinLength, int pinMaxLength, Locale locale) {
        this.pinLabel = pinLabel;
        this.pinMinLength = pinMinLength;
        this.pinMaxLength = pinMaxLength;
        this.locale = locale;
        try {
            if (null != UIManager.getLookAndFeel()) {
                UIManager.setLookAndFeel(POReIDConfig.LAF);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo de alteração de pin");
        }
    }
    
    
    public static ModifyPinDialogController getInstance(String pinLabel, int pinMinLength, int pinMaxLength, Locale locale){
        return new ModifyPinDialogController(pinLabel, pinMinLength, pinMaxLength, locale);
    }
    
    public ByteBuffer[] modifyPin() throws PinEntryCancelledException {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    ModifyPinDialog dialog = new ModifyPinDialog(pinLabel, pinMinLength, pinMaxLength, locale, listener);
                    dialog.setVisible(true);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo de alteração de pin");
        }
        
        if (cancelled) {
            throw new PinEntryCancelledException("Modificação do "+pinLabel+" cancelada.");
        }
        
        return new ByteBuffer[]{currentPin,newPin};
    }
   
    
    private DialogEventListener<ByteBuffer> listener = new DialogEventListener<ByteBuffer>() {

        @Override
        public void onCancel() {
            ModifyPinDialogController.this.cancelled = true;
        }

        @Override
        public void onDiagloclosed() {
            ModifyPinDialogController.this.cancelled = true;
        }

        @Override
        public void onContinue(ByteBuffer... data) {
            if (data.length > 1 && null != data[0] && null != data[1]) {
                ModifyPinDialogController.this.currentPin = data[0];
                ModifyPinDialogController.this.newPin = data[1];
            }
        }
    };
}
