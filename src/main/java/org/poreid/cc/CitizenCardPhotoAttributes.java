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
package org.poreid.cc;

import java.util.Arrays;

/**
 * Metadados e foto do cidadão
 * @author POReID
 */
public final class CitizenCardPhotoAttributes {
    private boolean isdataLoaded = false;
    private FaceImageRecordFormat firf;
    private byte[] photo;
    private final byte[] data;
    
    
    protected CitizenCardPhotoAttributes(byte[] data){
        this.data = data;
    }
    
    /**
     * Retorna metadados da imagem do cidadão - descritos na ISO/IEC 19794-5
     * @return metadados da imagem do cidadão
     */
    public FaceImageRecordFormat getFaceImageRecordFormat() {
        checkNLoad();
        return firf;
    }
    
    /**
     * Retorna a o foto do cidadão em formato jpeg2000
     * @return foto do cidadão
     */
    public byte[] getPhoto() {
        checkNLoad();
        return Arrays.copyOf(photo, photo.length);
    }
    
    /**
     * Retorna atributos e foto do cidadão tal como lidos do cartão
     * @return atributos e foto do cidadão
     */
    public byte[] getRawData(){
        return Arrays.copyOf(data, data.length);
    }
    
    
    private void checkNLoad(){
        if (!isdataLoaded){
            parse(data);
        }
    }
   
    
    private void parse(byte[] data){
        int i;
        firf = new FaceImageRecordFormat(Arrays.copyOf(data, 34), Arrays.copyOfRange(data, 34, 48), Arrays.copyOfRange(data, 48, 80));  
        for(i = data.length; i>80; i--){
            if ((byte)0xff==data[i-1] && (byte)0xd9 == data[i]){
                break;
            }
        }   
        photo = Arrays.copyOfRange(data, 80, i+1);
        isdataLoaded = true;
    }  
}
