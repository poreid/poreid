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

package org.poreid.dialogs.dialog;

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
public class DialogController {
    private String title;
    private String message;
    private Locale locale;
    private boolean error;
    private Semaphore semaphore = null;    
    private Dialog dialog;
    
    private DialogController(String title, String message, Locale locale, boolean error) {
        try {            
            Util.setLookAndFeel();
            
            this.title = title;
            this.message = message;
            this.locale = locale;
            this.error = error;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo.", ex);
        }
    }
    
  
    public static DialogController getInstance(String title, String message, Locale locale, boolean error){
        return new DialogController(title, message, locale, error);
    }
    
    
    public void displayDialog(){ //TODO: fica por aqui até resolver a modificação de pin
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Dialog dialog = new Dialog(title, message, locale, error);
                    dialog.setVisible(true);
                    dialog.requestFocusInWindow();
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo.", ex);
        }
    }
    
    public void displayDialog(Date date) {
        if (POReIDConfig.isTimedInteractionEnabled()) {
            long timeout = Util.getDateDiff(date, new Date(), TimeUnit.SECONDS);
            timeout = (timeout > POReIDConfig.timedInteractionPeriod() ? 0 : POReIDConfig.timedInteractionPeriod() - timeout);
            semaphore = new Semaphore(1);
            
            try {
                semaphore.acquire();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new Dialog(title, message, locale, error, listener);
                        dialog.setVisible(true);
                        dialog.requestFocusInWindow();
                    }
                });
                if (!semaphore.tryAcquire(timeout, TimeUnit.SECONDS)) {
                    dialog.dispose();
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException("Não foi possivel criar a janela de dialogo.", ex);
            }            
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new Dialog(title, message, locale, error);
                        dialog.setVisible(true);
                        dialog.requestFocusInWindow();
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                throw new RuntimeException("Não foi possivel criar a janela de dialogo.", ex);
            }
        }
    }
    
    
    private void releaseSemaphore() {
        if (null != semaphore) {
            semaphore.release();
        }
    }
    
    
    private DialogEventListener<Void> listener = new DialogEventListener<Void>() {
        
        @Override
        public final void onDiagloclosed() {            
            releaseSemaphore();
        }

        @Override
        public void onCancel() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onContinue(Void... data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
       
    };
}
