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

package org.poreid.dialogs.pindialogs.verifypin;

import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.poreid.config.POReIDConfig;
import org.poreid.dialogs.DialogEventListener;
import org.poreid.dialogs.pindialogs.PinEntryCancelledException;
import org.poreid.dialogs.pindialogs.PinTimeoutException;

/**
 *
 * @author POReID
 */
public class VerifyPinDialogController{
    /**
     * @param args the command line arguments
     */
   
    private Semaphore semaphore;
    private boolean cancelled;
    private ByteBuffer pinCode;
    private int timeOut;
    private byte[] pinIcon;
    private String pinLabel;
    private int pinMaxLength;
    private int pinMinLength;
    private Locale locale;
    private VerifyPinDialog dialog = null;
    
    
    private VerifyPinDialogController(int timeOut, String pinLabel, byte[] pinICon, int pinMinLength, int pinMaxLength, Locale locale) {
        this.timeOut = timeOut;
        this.pinIcon = pinICon;
        this.pinLabel = pinLabel;
        this.pinMinLength = pinMinLength;
        this.pinMaxLength = pinMaxLength;
        this.locale = locale;
        try {
            if (null != UIManager.getLookAndFeel()) {
                UIManager.setLookAndFeel(POReIDConfig.LAF);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo de requisição de pin");
        }
    }
    
    
    public static VerifyPinDialogController getInstance(int timeOut, String pinLabel, byte[] pinICon, int pinMinLength, int pinMaxLength, Locale locale){
        return new VerifyPinDialogController(timeOut, pinLabel, pinICon, pinMinLength, pinMaxLength, locale);
    }
    
    
    public byte[] askForPin() throws PinEntryCancelledException, PinTimeoutException {
        semaphore = new Semaphore(1);
        
        try {
            semaphore.acquire();
            createDialog();
            semaphore.tryAcquire(timeOut, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo de requisição de pin");
        }

        if (cancelled) {
            throw new PinEntryCancelledException("Introdução do "+pinLabel+" cancelada.");
        }

        if (null == pinCode) {
            dialog.dispose();
            throw new PinTimeoutException(pinLabel+ "não foi inserido dentro do tempo regular. Tempo regular = "+timeOut+ "segundos.");
        }

        return pinCode.array();
    }
    
       
    private void createDialog(){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                dialog = new VerifyPinDialog(pinLabel, pinIcon, pinMinLength, pinMaxLength, locale, VerifyPinDialogController.this.listener);
                dialog.setVisible(true);
            }
        });
    }
     
    
    private DialogEventListener<ByteBuffer> listener = new DialogEventListener<ByteBuffer>() {
        @Override
        public void onCancel() {
            VerifyPinDialogController.this.cancelled = true;
            VerifyPinDialogController.this.semaphore.release();
        }

        @Override
        public void onDiagloclosed() {
            VerifyPinDialogController.this.cancelled = true;
            VerifyPinDialogController.this.semaphore.release();
        }

        @Override
        public void onContinue(ByteBuffer... data) {
            VerifyPinDialogController.this.pinCode = data.length > 0 && null!= data[0] ? data[0] : null;
            VerifyPinDialogController.this.semaphore.release();
        }
    };
}
