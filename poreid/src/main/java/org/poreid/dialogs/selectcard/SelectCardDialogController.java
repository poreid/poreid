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

package org.poreid.dialogs.selectcard;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import org.poreid.dialogs.DialogEventListener;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.swing.UnsupportedLookAndFeelException;
import org.poreid.common.Util;
import org.poreid.config.POReIDConfig;

/**
 *
 * @author POReID
 * @param <T> Classe que implemente a interface POReIDSmartCard
 */
public class SelectCardDialogController<T>{
    private boolean cancelled;
    private T selected;
    private List<T> cardList;
    private SelectCardDialog<T> dialog = null;
    private Locale locale;
    private Semaphore semaphore = null;
    
    
    private SelectCardDialogController(final List<T> cardList, Locale locale){  
        try {
            this.cardList = cardList;
            this.locale = locale;
            Util.setLookAndFeel();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("Não foi possivel criar a janela de dialogo para seleção de cartão");
        }
    }
    
    
    public static <T> SelectCardDialogController<T> getInstance(List<T> cardList, Locale locale){
        return new SelectCardDialogController<>(cardList, locale);
    }
    
   
    public T selectCard(Date date) throws CanceledSelectionException{       
        if (POReIDConfig.isTimedInteractionEnabled()) {
            long timeout = Util.getDateDiff(date, new Date(), TimeUnit.SECONDS);
            timeout = (timeout > POReIDConfig.timedInteractionPeriod() ? 0 : POReIDConfig.timedInteractionPeriod() - timeout);
            semaphore = new Semaphore(1);
            try {
                semaphore.acquire();
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new SelectCardDialog<>(cardList, locale, listener);
                        dialog.setVisible(true);
                    }
                });
                if (!semaphore.tryAcquire(timeout, TimeUnit.SECONDS)) {
                    dialog.dispose();
                    cancelled = true;
                }
            } catch (InterruptedException ex) {
                throw new CanceledSelectionException(ex.getMessage());
            }
        } else {
            try {
                javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new SelectCardDialog<>(cardList, locale, listener);
                        dialog.setVisible(true);
                    }
                }); 
            } catch (InterruptedException | InvocationTargetException ex) {
                throw new CanceledSelectionException(ex.getMessage());
            }
        }
                        
        if (cancelled){
            throw new CanceledSelectionException("Operação de seleção de cartão cancelada");
        }
        
        return selected;
    }
      
    
    private void releaseSemaphore() {
        if (null != semaphore) {
            semaphore.release();
        }
    }
    
    
    private DialogEventListener<T> listener = new DialogEventListener<T>() {

        @Override
        public final void onCancel() {
            SelectCardDialogController.this.cancelled = true;
            releaseSemaphore();
        }

        @Override
        public final void onDiagloclosed() {
            SelectCardDialogController.this.cancelled = true;
            releaseSemaphore();
        }

        @SafeVarargs
        @Override
        public final void onContinue(T... data) {
            SelectCardDialogController.this.selected = data.length > 0 && null!= data[0] ? data[0] : null;
            releaseSemaphore();
        }
    };
}
