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
package org.poreid;

import java.io.IOException;
import java.io.InputStream;
import org.poreid.common.Util;

/**
 *
 * @author POReID
 */
public class Pin {
    private String label = null;
    private final String iconPath;
    private final String backgroundPath;
    private final String backgroundSmallPath;
    private final byte reference;
    private byte keyReference;
    private String keyPath = null;
    private final byte padChar;
    private final byte pinLength;
    private final byte maxLength;

    /**
     * Cria uma instância com os parametros indicados
     * @param label descrição do pin (Pin de autenticação / Pin de assinatura)
     * @param length Tamanho do pin
     * @param maxLength Tamanho máximo do pin
     * @param iconPath Localização do icone associado ao pin
     * @param backgroundPath Localização do fundo a utilizar na janela de diálogo
     * @param backgroundSmallPath Localização do fundo a utilizar na janela de diálogo sem mensagem informativa
     * @param reference Referência interna do pin
     * @param keyReference Referência interna da chave
     * @param padChar Caracter de "pad" utilizado
     */
    public Pin(String label, byte length, byte maxLength, String iconPath, String backgroundPath, String backgroundSmallPath, byte reference, byte keyReference, byte padChar) {
        this.label = label;
        this.iconPath = iconPath;
        this.backgroundPath = backgroundPath;
        this.backgroundSmallPath = backgroundSmallPath;
        this.reference = reference;
        this.keyReference = keyReference;
        this.padChar = padChar;
        this.pinLength = length;
        this.maxLength = maxLength;
    }

    /**
     * Cria uma instância com os parametros indicados
     * @param label descrição do pin (Pin de autenticação / Pin de assinatura)
     * @param length Tamanho do pin
     * @param maxLength Tamanho máximo do pin
     * @param iconPath Localização do icone associado ao pin
     * @param backgroundPath Localização do fundo a utilizar na janela de diálogo
     * @param backgroundSmallPath Localização do fundo a utilizar na janela de diálogo sem mensagem informativa
     * @param reference Referência interna do pin
     * @param keyPath Caminho da chave
     * @param padChar Caracter de "pad" utilizado
     */
    public Pin(String label, byte length, byte maxLength, String iconPath, String backgroundPath, String backgroundSmallPath, byte reference, String keyPath, byte padChar) {
        this.label = label;
        this.iconPath = iconPath;
        this.backgroundPath = backgroundPath;
        this.backgroundSmallPath = backgroundSmallPath;
        this.reference = reference;
        this.keyPath = keyPath;
        this.padChar = padChar;
        this.pinLength = length;
        this.maxLength = maxLength;
    }

    /**
     * Cria uma instância com os parametros indicados
     * @param label descrição do pin (Pin de autenticação / Pin de assinatura)
     * @param length Tamanho do pin
     * @param maxLength Tamanho máximo do pin
     * @param iconPath Localização do icone associado ao pin
     * @param backgroundPath Localização do fundo a utilizar na janela de diálogo
     * @param backgroundSmallPath Localização do fundo a utilizar na janela de diálogo sem mensagem informativa
     * @param reference Referência interna do pin
     * @param keyReference Referência interna da chave
     * @param keyPath Caminho da chave
     * @param padChar Caracter de "pad" utilizado
     */
    public Pin(String label, byte length, byte maxLength, String iconPath, String backgroundPath, String backgroundSmallPath, byte reference, byte keyReference, String keyPath, byte padChar) {
        this.label = label;
        this.iconPath = iconPath;
        this.backgroundPath = backgroundPath;
        this.backgroundSmallPath = backgroundSmallPath;
        this.reference = reference;
        this.keyReference = keyReference;
        this.keyPath = keyPath;
        this.padChar = padChar;
        this.pinLength = length;
        this.maxLength = maxLength;
    }

    /**
     * Retorna a label associada ao PIN
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Retorna a reference associada ao PIN
     * @return reference
     */
    public byte getReference() {
        return reference;
    }

    /**
     * Retorna a key reference associada ao PIN
     * @return key reference
     */
    public byte getKeyReference() {
        return keyReference;
    }

    /**
     * Retorna a key path associada ao PIN
     * @return key path
     */
    public String getKeyPath() {
        return keyPath;
    }

    /**
     * Retorna o caracter de enchimento
     * @return caracter de enchimento
     */
    public byte getPadChar() {
        return padChar;
    }

    /**
     * Retorna o icone associado ao PIN
     * @return icone do PIN
     */
    public byte[] getIcon(){
        try (InputStream input = Pin.class.getResourceAsStream(iconPath)){
            return Util.toByteArray(input);
        } catch (IOException ex) {
            return new byte[0];
        }
    }
    
    
    public byte[] getBackground(){
        try (InputStream input = Pin.class.getResourceAsStream(backgroundPath)){
            return Util.toByteArray(input);
        } catch (IOException ex) {
            return new byte[0];
        }
    }
    
    public byte[] getSmallBackground(){
        try (InputStream input = Pin.class.getResourceAsStream(backgroundSmallPath)){
            return Util.toByteArray(input);
        } catch (IOException ex) {
            return new byte[0];
        }
    }

    /**
     * Retorna o tamanho minimo do PIN
     * @return tamanho minimo
     */
    public int getMinLength() {
        return pinLength;
    }

    /**
     * Retorna o tamanho máximo do PIN
     * @return tamanho máximo
     */
    public int getMaxLength() {
        return maxLength;
    }
}