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
package org.poreid.cc;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Contem os atributos de identificação do cidadão
 * @author POReID
 */
public class CitizenCardIdAttributes {
    private boolean isdataLoaded = false;
    
    private String issuingEntity;
    private String country;
    private String documentType;
    private String documentNumber;  
    private String documentNumberPAN;
    private String documentVersion;
    private String validityBeginDate;
    private String localofRequest;
    private String validityEndDate;
    private String surname;
    private String name;
    private String gender;
    private String nationality;
    private String dateOfBirth;
    private String heigh;
    private String civilianIdNumber;
    private String surnameMother;
    private String givenNameMother;
    private String surnameFather;
    private String givenNameFather;
    private String taxNo;
    private String socialSecurityNo;
    private String healthNo;
    private String accidentalIndications;
    private String mrz1;
    private String mrz2;
    private String mrz3;
    private final byte[] data;
    private byte[] digest;
    
    
    protected CitizenCardIdAttributes(byte[] data){
        this.data = data;
    }

    /**
     * Retorna o nome completo do cidadão
     * @return nome completo do cidadão
     */
    public String getCitizenFullName(){
        checkNLoad();
        return name + " " + surname;
    }

    /**
     * Retorna as indicações eventuais
     * @return indicações eventuais
     */
    public String getAccidentalIndications(){
        checkNLoad();  
        return accidentalIndications;
    }

    /**
     * Retorna o número de identificação civil (nic)
     * @return número de identificação civil
     */
    public String getCivilianIdNumber(){
        checkNLoad();
        return civilianIdNumber;
    }

    /**
     * Retorna o país no formato ISO 3166-1 alpha-3
     * @return país no formato ISO 3166-1 alpha-3
     */
    public String getCountry(){
        checkNLoad();
        return country;
    }

    /**
     * Retorna a data de nascimento
     * @return data de nascimento
     */
    public String getDateOfBirth(){
        checkNLoad();
        return dateOfBirth;
    }

    /**
     * Retorna o número do documento de identificação (número do cartão de cidadão)
     * @return número do documento de identificação
     */
    public String getDocumentNumber(){
        checkNLoad();
        return documentNumber;
    }

    /**
     * Retorna o Primary Account Number do cartão de cidadão
     * @return Primary Account Number do cartão de cidadão
     */
    public String getDocumentNumberPAN(){
        checkNLoad();
        return documentNumberPAN;
    }

    /**
     * Retorna o tipo de documento (Cartão de Cidadão)
     * @return tipo de documento
     */
    public String getDocumentType(){
        checkNLoad();
        return documentType;
    }

    /**
     * Retorna a versão do cartão de cidadão
     * @return versão do cartão de cidadão
     */
    public String getDocumentVersion(){
        checkNLoad();
        return documentVersion;
    }

    /**
     * Retorna o género
     * @return género
     */
    public String getGender(){
        checkNLoad();
        return gender;
    }

    /**
     * Retorna os nomes do pai do cidadão
     * @return nomes do pai do cidadão
     */
    public String getGivenNameFather(){
        checkNLoad();
        return givenNameFather;
    }

    /**
     * Retorna os nomes da mãe do cidadão
     * @return nomes da mãe do cidadão
     */
    public String getGivenNameMother(){
        checkNLoad();
        return givenNameMother;
    }

    /**
     * Retorna o número de utente do sistema nacional de saúde
     * @return número de utente do sistema nacional de saúde
     */
    public String getHealthNo(){
        checkNLoad();
        return healthNo;
    }

    /**
     * Retorna a altura do cidadão 
     * @return altura do cidadão
     */
    public String getHeigh(){
        checkNLoad();
        return heigh;
    }

    /**
     * Retorna a entidade emissora do documento
     * @return entidade emissora do documento
     */
    public String getIssuingEntity(){
        checkNLoad();
        return issuingEntity;
    }

    /**
     * Retorna o local onde foi efetuado o pedido do documento
     * @return local onde foi efetuado o pedido do documento
     */
    public String getLocalofRequest(){
        checkNLoad();
        return localofRequest;
    }

    /**
     * Retorna a primeira linha da Machine Readable Zone (MRZ)
     * @return primeira linha da Machine Readable Zone (MRZ)
     */
    public String getMrz1(){
        checkNLoad();
        return mrz1;
    }

    /**
     * Retorna a segunda linha da Machine Readable Zone (MRZ)
     * @return segunda linha da Machine Readable Zone (MRZ)
     */
    public String getMrz2(){
        checkNLoad();
        return mrz2;
    }

    /**
     * Retorna a terceira linha da Machine Readable Zone (MRZ)
     * @return terceira linha da Machine Readable Zone (MRZ)
     */
    public String getMrz3(){
        checkNLoad();
        return mrz3;
    }

    /**
     * Retorna os nomes do cidadão
     * @return nomes do cidadão
     */
    public String getName(){
        checkNLoad();
        return name;
    }

    /**
     * Retorna a nacionalidade do cidadão no formato ISO 3166-1 alpha-3
     * @return nacionalidade do cidadão no formato ISO 3166-1 alpha-3
     */
    public String getNationality(){
        checkNLoad();
        return nationality;
    }

    /**
     * Retorna o número de identificação de segurança social
     * @return número de identificação de segurança social
     */
    public String getSocialSecurityNo(){
        checkNLoad();
        return socialSecurityNo;
    }

    /**
     * Retorna os apelidos do cidadão
     * @return apelidos do cidadão
     */
    public String getSurname(){
        checkNLoad();
        return surname;
    }

    /**
     * Retorna os apelidos no nome do pai do cidadão
     * @return apelidos no nome do pai do cidadão
     */
    public String getSurnameFather(){
       checkNLoad();
        return surnameFather;
    }

    /**
     * Retorna os apelidos no nome da mãe do cidadão
     * @return apelidos no nome da mãe do cidadão
     */
    public String getSurnameMother(){
        checkNLoad();
        return surnameMother;
    }

    /**
     * Retorna o número de identificação fiscal
     * @return número de identificação fiscal
     */
    public String getTaxNo(){
        checkNLoad();
        return taxNo;
    }

    /**
     * Retorna a data emissão do documento
     * @return data emissão do documento
     */
    public String getValidityBeginDate(){
        checkNLoad();
        return validityBeginDate;
    }

    /**
     * Retorna a data de fim de validade 
     * @return data de fim de validade
     */
    public String getValidityEndDate(){
        checkNLoad();
        return validityEndDate;
    }

    /**
     * Retorna os dados do cidadão tal como lidos do cartão
     * @return dados do cidadão
     */
    public byte[] getRawData(){
        return Arrays.copyOf(data, data.length);
    }
    
    /**
     * Retorna o resumo criptográfico dos dados do cidadão
     * @return resumo criptográfico SHA-256
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.UnsupportedEncodingException
     */
    public byte[] generateDigest() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        checkNLoad();

        if (null == digest) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            md.update(issuingEntity.getBytes("utf-8"));            
            md.update(country.getBytes("utf-8"));           
            md.update(documentType.getBytes("utf-8"));            
            md.update(documentNumber.getBytes("utf-8"));            
            md.update(documentNumberPAN.getBytes("utf-8"));            
            md.update(documentVersion.getBytes("utf-8"));            
            md.update(validityBeginDate.getBytes("utf-8"));           
            md.update(localofRequest.getBytes("utf-8"));            
            md.update(validityEndDate.getBytes("utf-8"));            
            md.update(surname.getBytes("utf-8"));           
            md.update(name.getBytes("utf-8"));            
            md.update(gender.getBytes("utf-8"));            
            md.update(nationality.getBytes("utf-8"));            
            md.update(dateOfBirth.getBytes("utf-8"));
            md.update(heigh.getBytes("utf-8"));
            md.update(civilianIdNumber.getBytes("utf-8"));
            md.update(surnameMother.getBytes("utf-8"));
            md.update(givenNameMother.getBytes("utf-8"));
            md.update(surnameFather.getBytes("utf-8"));
            md.update(givenNameFather.getBytes("utf-8"));
            md.update(accidentalIndications.getBytes("utf-8"));            
            md.update(taxNo.getBytes("utf-8"));
            md.update(socialSecurityNo.getBytes("utf-8"));
            md.update(healthNo.getBytes("utf-8"));
               
            digest = md.digest();
        }
        
        return digest;
    }
    
    
    private void checkNLoad(){
        if (!isdataLoaded){
            parse(data);
        }
    }
    
    
    private void parse(byte[] data){
        issuingEntity = new String(data, 0, 39, StandardCharsets.UTF_8).trim();
        country = new String(data, 40, 80, StandardCharsets.UTF_8).trim();
        documentType = new String(data, 120, 34, StandardCharsets.UTF_8).trim();
        documentNumber = new String(data, 154, 28, StandardCharsets.UTF_8).trim();
        documentNumberPAN = new String(data, 182, 32, StandardCharsets.UTF_8).trim();
        documentVersion = new String(data, 214, 16, StandardCharsets.UTF_8).trim();
        validityBeginDate = new String(data, 230, 20, StandardCharsets.UTF_8).trim();
        localofRequest = new String(data, 250, 60, StandardCharsets.UTF_8).trim();
        validityEndDate = new String(data, 310, 20, StandardCharsets.UTF_8).trim();
        surname = new String(data, 330, 120, StandardCharsets.UTF_8).trim();
        name = new String(data, 450, 120, StandardCharsets.UTF_8).trim();
        gender = new String(data, 570, 2, StandardCharsets.UTF_8).trim();
        nationality = new String(data, 572, 6, StandardCharsets.UTF_8).trim();
        dateOfBirth = new String(data, 578, 20, StandardCharsets.UTF_8).trim();
        heigh = new String(data, 598, 8, StandardCharsets.UTF_8).trim();
        civilianIdNumber = new String(data, 606, 18, StandardCharsets.UTF_8).trim();
        surnameMother = new String(data, 624, 120, StandardCharsets.UTF_8).trim();
        givenNameMother = new String(data, 744, 120, StandardCharsets.UTF_8).trim();
        surnameFather = new String(data, 864, 120, StandardCharsets.UTF_8).trim();
        givenNameFather = new String(data, 984, 120, StandardCharsets.UTF_8).trim();
        taxNo = new String(data, 1104, 18, StandardCharsets.UTF_8).trim();
        socialSecurityNo = new String(data, 1122, 22, StandardCharsets.UTF_8).trim();
        healthNo = new String(data, 1144, 18, StandardCharsets.UTF_8).trim();
        accidentalIndications = new String(data, 1162, 120, StandardCharsets.UTF_8).trim();
        mrz1 = new String(data, 1282, 30, StandardCharsets.UTF_8).trim();
        mrz2 = new String(data, 1312, 30, StandardCharsets.UTF_8).trim();
        mrz3 = new String(data, 1342, 30, StandardCharsets.UTF_8).trim();
        isdataLoaded = true;
    }
}
