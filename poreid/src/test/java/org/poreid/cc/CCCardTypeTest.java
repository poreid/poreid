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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.poreid.CardFactory;
import org.poreid.PkAlias;

/**
 *
 * @author POReID
 */
public class CCCardTypeTest {
    static CitizenCard instance;
    
    public CCCardTypeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception{
        instance = CardFactory.getCard(false);
    }
    
    @AfterClass
    public static void tearDownClass() {
        instance = null;
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /*
    @Test
    public void readIDData() throws Exception {
        System.out.println("readIDData");

        CitizenCardIdAttributes ccia = instance.getID();
        ccia.getAccidentalIndications();
        ccia.getCitizenFullName();
        ccia.getCivilianIdNumber();
        ccia.getCountry();
        ccia.getDateOfBirth();
        ccia.getDocumentNumber();
        ccia.getDocumentNumberPAN();
        ccia.getDocumentType();
        ccia.getDocumentVersion();
        ccia.getGender();
        ccia.getGivenNameFather();
        ccia.getGivenNameMother();
        ccia.getHealthNo();
        ccia.getHeigh();
        ccia.getIssuingEntity();
        ccia.getLocalofRequest();
        ccia.getMrz1();
        ccia.getMrz2();
        ccia.getMrz3();
        ccia.getName();
        ccia.getNationality();
        ccia.getSocialSecurityNo();
        ccia.getSurname();
        ccia.getSurnameFather();
        ccia.getSurnameMother();
        ccia.getTaxNo();
        ccia.getValidityBeginDate();
        ccia.getValidityEndDate();
        
        ccia.getRawData();
    }
    
    
    @Test
    public void readPhoto() throws Exception {
        System.out.println("readPhoto");
        
        byte[] b = instance.getPhoto().clone();
        Assert.assertArrayEquals(b, b );
    }
    
    
    @Test
    public void readSOD() throws Exception {
        System.out.println("readSOD");
        
        byte[] b = instance.getSOD().clone();
        Assert.assertArrayEquals(b, b );
    }
    
    
    @Test
    public void readPersonalNotes() throws Exception {
        System.out.println("readPersonalNotes");
        
        byte[] b = instance.readPersonalNotes().clone();
        Assert.assertArrayEquals(b, b );
    }
    
    
    @Test
    public void verifySignaturePIN() throws Exception {
        System.out.println("verifySignaturePIN");
        
        instance.verifyPin(instance.getCardSpecificReferences().getCryptoReferences(PkAlias.ASSINATURA), "1485".getBytes());
    }
    
    
    @Test
    public void verifyAuthenticationPIN() throws Exception {
        System.out.println("verifyAuthenticationPIN");
        
       instance.verifyPin(instance.getCardSpecificReferences().getCryptoReferences(PkAlias.AUTENTICACAO), "1485".getBytes());
    }
    
    
    @Test
    public void verifyAddressPIN() throws Exception {
        System.out.println("verifyAddressPIN");
        
        instance.verifyPin(instance.getCardSpecificReferences().getAddressPin(), "0000".getBytes());
    }
    
    
    @Test
    public void readAddressData() throws Exception {
        System.out.println("readAddressData");
        instance.verifyPin(instance.getCardSpecificReferences().getAddressPin(), "0000".getBytes());
        CitizenCardAddressAttributes ccaa = instance.getAddress();
        
        ccaa.getAbbreviatedBuildingType();
        ccaa.getAbbreviatedStreetType();
        ccaa.getBuildingType();
        ccaa.getCivilParish();
        ccaa.getCivilParishCode();
        ccaa.getCountry();
        ccaa.getDistrict();
        ccaa.getDistrictCode();
        ccaa.getDoorNo();
        ccaa.getFloor();
        ccaa.getGenAddressNo();
        ccaa.getLocality();
        ccaa.getMunicipality();
        ccaa.getMunicipalityCode();
        ccaa.getPlace();
        ccaa.getPostalLocality();
        ccaa.getSide();
        ccaa.getStreetName();
        ccaa.getStreetType();
        ccaa.getType();
        ccaa.getZip3();
        ccaa.getZip4();
        ccaa.getRawData();   
    }
    
    
    @Test
    public void authentication() throws Exception {
        System.out.println("authentication");
        byte[] hash = new byte[]{0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9};
        MessageDigest md = MessageDigest.getInstance("SHA-1"); 
        
        instance.sign(hash, "1485".getBytes(), md.getAlgorithm(), PkAlias.AUTENTICACAO, RSAPaddingSchemes.PKCS1);
    }
    
    
    @Test
    public void digitalsignature() throws Exception {
        System.out.println("authentication");
        byte[] hash = new byte[]{0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9};
        MessageDigest md = MessageDigest.getInstance("SHA-1"); 
       
        instance.sign(hash, "1485".getBytes(), md.getAlgorithm(), PkAlias.ASSINATURA, RSAPaddingSchemes.PKCS1);
    }
    
    
    @Test
    
    public void testProvider() throws Exception{
        Security.addProvider(new POReIDProvider());
        final KeyStore ks = KeyStore.getInstance("POReID");
        ks.load(null);

        final Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign((PrivateKey) ks.getKey("Autenticacao", null));
        signature.update("olá".getBytes());

        signature.sign();
    }
    
    */
    @Test
    public void modifySignaturePIN() throws Exception{
        System.out.println("modifySignaturePIN");
        instance.ModifyPin(instance.getCardSpecificReferences().getCryptoReferences(PkAlias.ASSINATURA));
    }
    
    @Test
    public void modifyAddressPIN() throws Exception{
        System.out.println("modifyAddressPIN");
        instance.ModifyPin(instance.getCardSpecificReferences().getAddressPin());
    }
    
    @Test
    public void modifyAuthenticationPIN() throws Exception{
        System.out.println("modifyAuthenticationPIN");
        instance.ModifyPin(instance.getCardSpecificReferences().getCryptoReferences(PkAlias.AUTENTICACAO));
    }
    
    @Test
    public void verifyAuthenticationPIN() throws Exception{
        System.out.println("verifyAuthenticationPIN");
        instance.verifyPin(instance.getCardSpecificReferences().getCryptoReferences(PkAlias.AUTENTICACAO), null);        
    }
    
    @Test
    public void verifySignaturePIN() throws Exception{
        System.out.println("verifyAuthenticationPIN");
        instance.verifyPin(instance.getCardSpecificReferences().getCryptoReferences(PkAlias.ASSINATURA), null);        
    }
    
    @Test
    public void verifyAddressPIN() throws Exception{
        System.out.println("verifyAuthenticationPIN");
        instance.verifyPin(instance.getCardSpecificReferences().getAddressPin(), null);        
    }
}

