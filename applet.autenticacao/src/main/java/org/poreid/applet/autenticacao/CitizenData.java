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

package org.poreid.applet.autenticacao;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author ruim
 */
public class CitizenData {
    private byte[] sod = null;
    private byte[] id = null;
    private byte[] address = null;
    private byte[] photo = null;
    private byte[] certificate;
    private byte[] signature;

    
    public void setSod(byte[] sod) {
        this.sod = sod;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public void setAddress(byte[] address) {
        this.address = address;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    } 
    
    
    public String getSod(){
        return (null == sod) ? null : DatatypeConverter.printBase64Binary(sod);
    }
    
    public String getID(){
        return (null == id) ? null : DatatypeConverter.printBase64Binary(id);
    }
    
    public String getAddress(){
        return (null == address) ? null : DatatypeConverter.printBase64Binary(address);
    }
    
    public String getPhoto(){
        return (null == photo) ? null : DatatypeConverter.printBase64Binary(photo);
    }
    
    public String getCertificate(){
        return DatatypeConverter.printBase64Binary(certificate);
    }
    
    public String getSignature(){
        return DatatypeConverter.printBase64Binary(signature);
    }
}
