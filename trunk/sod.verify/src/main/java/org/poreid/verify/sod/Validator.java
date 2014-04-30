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
package org.poreid.verify.sod;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 *
 * @author POReID
 */
public class Validator {
    private final KeyStore keystore;
    private SOD sod;
    private CitizenIdentificationAttributes id;
    private CitizenAddressAttributes address;
    private CitizenPhotoAttributes photo;
    
    
    public Validator(KeyStore keystore){
        this.keystore = keystore;
    }
    
    
    public void setSOD(byte[] sod) throws ValidatorException{
        try {
            this.sod =  new SOD(Arrays.copyOfRange(sod, 4, sod.length), keystore);
        } catch (SODException ex) {
            throw new ValidatorException("Formato inválido - SOD", ex);
        }
    }
    
    
    public void setID(byte[] id){
        this.id = new CitizenIdentificationAttributes(id);
    }
    
    
    public void setAddress(byte[] address){
        this.address = new CitizenAddressAttributes(address);
    }
    
    
    public void setPhoto(byte[] photo){        
        this.photo = new CitizenPhotoAttributes(photo);
    }
    
    
    public void validate() throws ValidatorException{
        try {
            if (sod.verify()){
                if (!Arrays.equals(id.getHash(), sod.getCitizenIdentificationHash())){
                    throw new ValidatorException("Resumo da identificação do cidadão não coincide com o resumo no SOD");
                }
                
                if (null != address && !Arrays.equals(address.getHash(), sod.getCitizenAddressHash())){
                    throw new ValidatorException("Resumo da morada do cidadão não coincide com o resumo no SOD");
                }
                
                if (null != photo && !Arrays.equals(photo.getHash(), sod.getCitizenPhoto())){
                    throw new ValidatorException("Resumo da fotografia do cidadão não coincide com o resumo no SOD");
                }
            } else {
                throw new ValidatorException("Não foi possivel validar o SOD");
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | SODException ex) {
            throw new ValidatorException(ex.getMessage(), ex);        
        }
    }
    
    
    public CitizenAddressAttributes getCitizenAddress(){
        return address;
    }
    
    
    public CitizenIdentificationAttributes getCitizenIdentification(){
        return id;
    }
    
    
    public CitizenPhotoAttributes getPhotoAttributes(){
        return photo;
    }
}
