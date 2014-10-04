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
 * Metadados da foto 
 * @author POReID
 */
public final class FaceImageRecordFormat {
    private final byte[] cbeff;
    private final byte[] facialRecHdr;
    private final byte[] facialRecordData;
    
    
    protected FaceImageRecordFormat(byte[] cbeff, byte[] facialRecHdr, byte[] facialRecordData){
        this.cbeff = cbeff;     
        this.facialRecHdr = facialRecHdr;
        this.facialRecordData = facialRecordData;
    }
    
    /**
     * Retorna o atributo CBEFF header (Common Biometric Exchange Formats Framework)
     * @return CBEFF header
     */
    public byte[] getCbeff() {
        return Arrays.copyOf(cbeff, cbeff.length);
    }

    /**
     * Retorna o atributo Facial Record Header - [format identifier (4 bytes), version no (4 bytes), length of record (4 bytes), number of face images (2 bytes)] 
     * @return Facial Record Header
     */
    public byte[] getFacialRecordHeader() {
        return Arrays.copyOf(facialRecHdr, facialRecHdr.length);
    }

    
    /**
     * Retorna o atributo Facial Record Data - ISO 19794-5:2005(E) specifies that the Facial Record Data contains Facial Information, Feature point(s), Image Information and Image Data
     * @return Facial Record Data
     */
    public byte[] getFacialRecordData() {
        return Arrays.copyOf(facialRecordData, facialRecordData.length);
    }
}
