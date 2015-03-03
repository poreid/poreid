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
    
    /* CBEFF - Para futura referência - NISTIR 6529-A pag. 26, iso/iec 7816-11 pag. 20
    TAG                                                                     Length                  Value
    7f61    - Biometric Information Group Template                          (base 256)              BIT group template   
    02      - Número de BITs no grupo                                       1                       1
    7f60    - Biometric Information Template #1                             (base 256)              Biometric Information Template
    A1      - Biometric Header Template (BHT) in compliance with CBEFF      tamanho do header       BHT
    81      - Biometric type                                                1-3                     02      (Facial features)
    82      - Biometric subtype                                             1                       0       (No information given)
    87      - Format owner of the biometric verification data www.ibia.org  2                       0101    (ISO/IEC JTC 1 SC 37-Biometrics)
    88      - Format type of biometric verification data                    2                       0501    (Face Image Format)
    */
    private void parse(byte[] data) {
        int offset;
        
        firf = new FaceImageRecordFormat(Arrays.copyOf(data, 34), Arrays.copyOfRange(data, 34, 48), Arrays.copyOfRange(data, 48, 80));
        if (0x7f == data[0] && 0x61 == data[1] && 0x82 == (int) (data[2] & 0xff)) { // Long form: Application 97 (0x61)
            offset = (int) (data[3] & 0xff) * 256 + (int) (data[4] & 0xff) + 4;     // base 256 + deslocamento            
        } else {
            for (offset = data.length; offset > 80; offset--) {
                if ((byte) 0xff == data[offset - 1] && (byte) 0xd9 == data[offset]) {
                    break;
                }
            }
        }

        photo = Arrays.copyOfRange(data, 80, offset + 1);
        isdataLoaded = true;
    }
}
