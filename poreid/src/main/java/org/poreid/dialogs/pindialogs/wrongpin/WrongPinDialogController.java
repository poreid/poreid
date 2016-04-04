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

package org.poreid.dialogs.pindialogs.wrongpin;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import org.poreid.common.Util;
import org.poreid.config.POReIDConfig;
import org.poreid.dialogs.DialogEventListener;

/**
 *
 * @author POReID
 */
public class WrongPinDialogController {
    private boolean cancelled;
    private Semaphore semaphore = null;
    private String pinLabel;
    private int pinTriesLeft;
    private Locale locale;
    private WrongPinDialog dialog;
    
    
    private WrongPinDialogController(String pinLabel, int pinTriesLeft, Locale locale) {
        try {
            Util.setLookAndFeel();
            this.pinLabel = pinLabel;
            this.pinTriesLeft = pinTriesLeft;
            this.locale = locale;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo de informação de pin errado");
        }
    }
    
  
    public static WrongPinDialogController getInstance(String pinLabel, int pinTriesLeft, Locale locale){
        return new WrongPinDialogController(pinLabel, pinTriesLeft, locale);
    }
    
    
    public boolean displayWrongPinDialog(Date date) {                
        if (POReIDConfig.isTimedInteractionEnabled()) {
            long timeout = Util.getDateDiff(date, new Date(), TimeUnit.SECONDS);          
            timeout = (timeout > POReIDConfig.timedInteractionPeriod() ? 0 : POReIDConfig.timedInteractionPeriod() - timeout);
            semaphore = new Semaphore(1);
            
            try {
                semaphore.acquire();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new WrongPinDialog(pinLabel, pinTriesLeft, locale, listener);
                        dialog.setVisible(true);
                        dialog.requestFocusInWindow();
                    }
                });
                if (!semaphore.tryAcquire(timeout, TimeUnit.SECONDS)) {
                    dialog.dispose();
                    cancelled = true;
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException("Não foi possivel criar a janela de dialogo de requisição de pin");
            }
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new WrongPinDialog(pinLabel, pinTriesLeft, locale, listener);
                        dialog.setVisible(true);
                        dialog.requestFocusInWindow();
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                throw new RuntimeException("Não foi possivel criar a janela de dialogo de informação de pin errado");
            }
        }
        
        return cancelled;
    }
    
    private void releaseSemaphore() {
        if (null != semaphore) {
            semaphore.release();
        }
    }
    
    
    private DialogEventListener<Void> listener = new DialogEventListener<Void>() {

        @Override
        public final void onCancel() {
            WrongPinDialogController.this.cancelled = true;
            releaseSemaphore();
        }

        @Override
        public final void onDiagloclosed() {
            WrongPinDialogController.this.cancelled = true;
            releaseSemaphore();
        }

        @SafeVarargs
        @Override
        public final void onContinue(Void... data) {
            WrongPinDialogController.this.cancelled = false;
            releaseSemaphore();
        }   
    };
}
