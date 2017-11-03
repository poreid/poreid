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

package org.poreid.dialogs.pindialogs.verifypin;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.poreid.common.Util;
import org.poreid.config.POReIDConfig;
import org.poreid.dialogs.DialogEventListener;
import org.poreid.dialogs.pindialogs.MyDocument;

/**
 *
 * @author POReID
 */

class VerifyPinDialog extends javax.swing.JDialog {
    private String infoMessage;
    private final String pinLabel;    
    private Image image;
    private final int pinMinLength;
    private final int pinMaxLength;
    private DialogEventListener<ByteBuffer> listener;
    private final ResourceBundle bundle;
    
    /**
     * Creates new form VerifyPinDialog
     */
    public VerifyPinDialog(String pinLabel, byte[] pinIcon, int pinMinLength, int pinMaxLength, Locale locale, DialogEventListener<ByteBuffer> listener){
        super();
        this.pinLabel = pinLabel;
        try {
            image = ImageIO.read(new ByteArrayInputStream(pinIcon));
        } catch (IOException ex) {
            /* não acontece nada! */
        }
        this.pinMinLength = pinMinLength;
        this.pinMaxLength = pinMaxLength;
        this.listener = listener;
        bundle = POReIDConfig.getBundle(VerifyPinDialog.class.getSimpleName(),locale);
        initComponents();
        
        setIconImages(Util.getIconImages());
        this.setTitle(MessageFormat.format(bundle.getString("dialog.title"),pinLabel));
        this.getAccessibleContext().setAccessibleDescription(MessageFormat.format(bundle.getString("dialog.description"),pinLabel));
        
        
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                VerifyPinDialog.this.listener.onDiagloclosed();
            }

            @Override
            public void windowOpened(WindowEvent e) {                
                pin.requestFocus();
            }
        });
        
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar");
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "aceitar");
        this.getRootPane().getActionMap().put("cancelar", new CancelAbstractAction());
        this.getRootPane().getActionMap().put("aceitar", new OKAbstractAction());
    }
    
    
    public VerifyPinDialog(String pinLabel, byte[] pinIcon, int pinMinLength, int pinMaxLength, Locale locale, DialogEventListener<ByteBuffer> listener, String infoMsg){
        super();
        this.pinLabel = pinLabel;
        try {
            image = ImageIO.read(new ByteArrayInputStream(pinIcon));
        } catch (IOException ex) {
            /* não acontece nada! */
        }
        this.pinMinLength = pinMinLength;
        this.pinMaxLength = pinMaxLength;
        this.listener = listener;
        this.infoMessage = infoMsg;
        bundle = POReIDConfig.getBundle(VerifyPinDialog.class.getSimpleName(),locale);
        initComponents();
        
        setIconImages(Util.getIconImages());
        this.setTitle(MessageFormat.format(bundle.getString("dialog.title"),pinLabel));
        this.getAccessibleContext().setAccessibleDescription(MessageFormat.format(bundle.getString("dialog.description"),pinLabel));
        
        
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                VerifyPinDialog.this.listener.onDiagloclosed();
            }

            @Override
            public void windowOpened(WindowEvent e) {                
                pin.requestFocus();
            }
        });
        
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar");
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "aceitar");
        this.getRootPane().getActionMap().put("cancelar", new CancelAbstractAction());
        this.getRootPane().getActionMap().put("aceitar", new OKAbstractAction());
    }
            
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        background = new javax.swing.JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        jPanel2 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                //super.paintComponent(g);
                if (null!=infoMessage && !infoMessage.isEmpty()){
                    Dimension arcs = new Dimension(20,20);
                    int width = getWidth();
                    int height = getHeight();
                    Graphics2D graphics = (Graphics2D) g;
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.setColor(getBackground());
                    graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);//paint background
                }
            }
        };
        lblInfoMessage = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        pin = new javax.swing.JPasswordField(pinMaxLength);
        labelPin = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setIconImage(null);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        background.setPreferredSize(new java.awt.Dimension(410, 262));

        jPanel2.setBackground(new java.awt.Color(84, 199, 252, 170));
        jPanel2.setOpaque(null!=infoMessage && !infoMessage.isEmpty());

        if (null != infoMessage && !infoMessage.isEmpty()){
            lblInfoMessage.setBackground(new java.awt.Color(255, 255, 255));
            lblInfoMessage.setText("<html><body style='width: 280px'>"+infoMessage);
            lblInfoMessage.setVerticalAlignment(javax.swing.SwingConstants.TOP);
            lblInfoMessage.setOpaque(false);
        } else {
            lblInfoMessage.setVisible(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblInfoMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblInfoMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(172, 69));

        btnCancel.setFont(btnCancel.getFont().deriveFont(btnCancel.getFont().getSize()-1f));
        btnCancel.setText(bundle.getString("cancel.button"));
        btnCancel.setMaximumSize(new java.awt.Dimension(118, 30));
        btnCancel.setMinimumSize(new java.awt.Dimension(118, 30));
        btnCancel.setPreferredSize(new java.awt.Dimension(118, 30));
        btnCancel.getAccessibleContext().setAccessibleDescription(bundle.getString("cancel.button.description"));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOK.setFont(btnOK.getFont().deriveFont(btnOK.getFont().getSize()-1f));
        btnOK.setText(bundle.getString("ok.button"));
        btnOK.getAccessibleContext().setAccessibleDescription(MessageFormat.format(bundle.getString("ok.button.description"),pinLabel));
        btnOK.setEnabled(false);
        btnOK.setMaximumSize(new java.awt.Dimension(118, 30));
        btnOK.setMinimumSize(new java.awt.Dimension(118, 30));
        btnOK.setPreferredSize(new java.awt.Dimension(118, 30));
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        jPanel4.setOpaque(false);
        jPanel4.setVerifyInputWhenFocusTarget(false);
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        pin.setDocument(new MyDocument(pinMaxLength));
        pin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pin.setToolTipText(MessageFormat.format(bundle.getString("password.tooltip"),pinLabel));
        pin.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                pinCaretUpdate(evt);
            }
        });
        jPanel4.add(pin);

        labelPin.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        labelPin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPin.setText("<html><b>"+pinLabel+"</b></html>");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(labelPin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancel, btnOK});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(labelPin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 21, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCancel, btnOK});

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(background);
        background.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundLayout.createSequentialGroup()
                .addContainerGap(218, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addGap(9, 9, 9))
        );
        backgroundLayout.setVerticalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void pinCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_pinCaretUpdate

        if (pin.getPassword().length > pinMinLength - 1){
            btnOK.setEnabled(true);
        } else {
            btnOK.setEnabled(false);
        }
    }//GEN-LAST:event_pinCaretUpdate

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        listener.onContinue(StandardCharsets.UTF_8.encode(CharBuffer.wrap(pin.getPassword())));
        this.dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        listener.onCancel();
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel background;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelPin;
    private javax.swing.JLabel lblInfoMessage;
    private javax.swing.JPasswordField pin;
    // End of variables declaration//GEN-END:variables

    private final class OKAbstractAction extends AbstractAction {

        OKAbstractAction() {
            super();
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            if (btnOK.isEnabled()){
                btnOK.doClick();
            }
        }
    }
    
    private final class CancelAbstractAction extends AbstractAction {

        CancelAbstractAction() {
            super();
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            if (btnCancel.isEnabled()){
                btnCancel.doClick();
            }
        }
    }
}
