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

package org.poreid.dialogs.selectcard;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.poreid.SmartCardUIData;


/**
 *
 * @author POReID
 * @param <T> Classe que implemente a interface POReIDSmartCard
 */
public class SelectCardListCellRenderer<T extends SmartCardUIData> extends JLabel implements ListCellRenderer<SmartCardUIData> {

    @Override
    public Component getListCellRendererComponent(JList<? extends SmartCardUIData> list, SmartCardUIData value, int index, boolean isSelected, boolean cellHasFocus) {
        setIcon(new ImageIcon(value.getIcon()));
        setText(value.getUIVisibleInfo());
        setToolTipText(value.getTooltip());
        getAccessibleContext().setAccessibleDescription(value.getDescription());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());      
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());   
        }
        
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(isSelected);
        return this;
    } 
}
