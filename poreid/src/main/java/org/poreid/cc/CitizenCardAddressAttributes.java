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
 * Contem todos os atributos da morada do cidadão
 * @author POReID
 */
public class CitizenCardAddressAttributes {
    private boolean isdataLoaded = false;
    private static final String NACIONAL = "N";
    
    private String type;
    private String country;
    private String districtCode;
    private String district;
    private String municipalityCode;
    private String municipality;
    private String civilParishCode;
    private String civilParish;
    private String abbreviatedStreetType;
    private String streetType;
    private String streetName;
    private String abbreviatedBuildingType;
    private String buildingType;
    private String doorNo;
    private String floor;
    private String side;
    private String place;
    private String locality;
    private String zip4;
    private String zip3;
    private String postalLocality;
    private String GenAddressNo;
    private String countryDescriptionF;
    private String addressF;
    private String cityF;
    private String regionF;
    private String localityF;
    private String postalCodeF;
    private String GenAddressNoF;
    private final byte[] data;
    private byte[] digest;
    
    

    /**
     * C
     * @param data conteúdo do ficheiro da morada
     */
    protected CitizenCardAddressAttributes(byte[] data){
        this.data = data;
    }

    /**
     * Retorna código da morada
     * @return código da morada
     */
    public String getGenAddressNo(){
        checkNLoad();
        return GenAddressNo;
    }

    /**
     * Retorna a abreviatura do tipo de edificio
     * @return abreviatura do tipo de edificio
     */
    public String getAbbreviatedBuildingType(){
        checkNLoad();
        return abbreviatedBuildingType;
    }

    /**
     * Retorna a abreviatura do tipo de via
     * @return abreviatua do tipo de via
     */
    public String getAbbreviatedStreetType(){
        checkNLoad();
        return abbreviatedStreetType;
    }

    /**
     * Retorna do tipo de edificio
     * @return tipo de edificio
     */
    public String getBuildingType(){
        checkNLoad();
        return buildingType;
    }

    /**
     * Retorna o nome da freguesia
     * @return nome da freguesia
     */
    public String getCivilParish(){
        checkNLoad();
        return civilParish;
    }

    /**
     * Retorna o código da freguesia
     * @return código da freguesia
     */
    public String getCivilParishCode(){
        checkNLoad();
        return civilParishCode;
    }

    /**
     * Retorna o código do país em ISO 3166-2
     * @return código do país em ISO 3166-2
     */
    public String getCountry(){
        checkNLoad();
        return country;
    }

    /**
     * Retorna o nome do distrito
     * @return nome do distrito
     */
    public String getDistrict(){
        checkNLoad();
        return district;
    }

    /**
     * Retorna o código do distrito
     * @return código do distrito
     */
    public String getDistrictCode(){
        checkNLoad();
        return districtCode;
    }

    /**
     * Retorna o número de porta
     * @return número de porta
     */
    public String getDoorNo(){
        checkNLoad();
        return doorNo;
    }

    /**
     * Retorna o piso/andar
     * @return piso/andar
     */
    public String getFloor(){
        checkNLoad();
        return floor;
    }

    /**
     * Retorna o nome da localidade
     * @return nome da localidade
     */
    public String getLocality(){
        checkNLoad();
        return locality;
    }

    /**
     * Retorna o nome do município
     * @return nome do município
     */
    public String getMunicipality(){
        checkNLoad();
        return municipality;
    }

    /**
     * Retorna do código do município
     * @return código do município
     */
    public String getMunicipalityCode(){
        checkNLoad();
        return municipalityCode;
    }

    /**
     * Retorna o nome do lugar
     * @return nome do lugar
     */
    public String getPlace(){
        checkNLoad();
        return place;
    }

    /**
     * Retorna a localidade postal
     * @return localidade postal
     */
    public String getPostalLocality(){
        checkNLoad();
        return postalLocality;
    }

    /**
     * Retorna o lado
     * @return lado
     */
    public String getSide(){
        checkNLoad();
        return side;
    }

    /**
     * Retorna o nome da rua
     * @return nome da rua
     */
    public String getStreetName(){
        checkNLoad();
        return streetName;
    }

    /**
     * Retorna o tipo de via
     * @return tipo de via
     */
    public String getStreetType(){
        checkNLoad();
        return streetType;
    }

    /**
     * Retorna o tipo de morada (nacional ou estrangeira). Diferentes métodos devem ser utilizados para acesso aos dados.
     * @return true se a morada for nacional, false se for estrangeira
     */
    public boolean isAddressNational(){
        checkNLoad();
        return type.equalsIgnoreCase(NACIONAL);
    }

    /**
     * Retorna a porção constituída por três digitos do código postal
     * @return três digitos do código postal
     */
    public String getZip3(){
        checkNLoad();
        return zip3;
    }

    /**
     * Retorna a porção constituída por quatro digitos do código postal
     * @return quatro digitos do código postal
     */
    public String getZip4(){
        checkNLoad();
        return zip4;
    }

    /**
     * Retorna a morada tal como lida do cartão
     * @return morada do cidadão 
     */
    public byte[] getRawData(){
        return Arrays.copyOf(data, data.length);
    }
    
    /**
     * Retorna designação do país da morada estrangeira
     * @return designação do país
     */
    public String getCountryDescriptionF() {
        checkNLoad();
        return countryDescriptionF;
    }

    /**
     * Retorna o atributo endereço da morada estrangeira
     * @return atributo endereço da morada estrangeira
     */
    public String getForeignAddress() {
        checkNLoad();
        return addressF;
    }

    /**
     * Retorna o atributo cidade da morada estrangeira
     * @return atributo cidade da morada estrangeira
     */
    public String getForeignCity() {
        checkNLoad();
        return cityF;
    }

    /**
     * Retorna o atributo região da morada estrangeira
     * @return atributo região da morada estrangeira
     */
    public String getForeignRegion() {
        checkNLoad();
        return regionF;
    }

    /**
     * Retorna o atributo localidade da morada estrangeira
     * @return atributo localidade da morada estrangeira
     */
    public String getLocalityF() {
        checkNLoad();
        return localityF;
    }

    /**
     * Retorna o atributo código postal da morada estrangeira
     * @return atributo código postal da morada estrangeira
     */
    public String getPostalCodeF() {
        checkNLoad();
        return postalCodeF;
    }

    /**
     * Retorna o atributo código da morada estrangeira
     * @return atributo código da morada estrangeira
     */
    public String getGenAddressNoF() {
        checkNLoad();
        return GenAddressNoF;
    }
    
    /**
     * Retorna o resumo criptográfico da morada
     * @return resumo criptográfico SHA-256
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.UnsupportedEncodingException
     */
    public byte[] generateDigest() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        checkNLoad();
        
        if (null == digest) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            if (type.equals(NACIONAL)) {
                md.update(country.getBytes("utf-8"));
                md.update(districtCode.getBytes("utf-8"));
                md.update(district.getBytes("utf-8"));
                md.update(municipalityCode.getBytes("utf-8"));
                md.update(municipality.getBytes("utf-8"));
                md.update(civilParishCode.getBytes("utf-8"));
                md.update(civilParish.getBytes("utf-8"));
                md.update(abbreviatedStreetType.getBytes("utf-8"));
                md.update(streetType.getBytes("utf-8"));
                md.update(streetName.getBytes("utf-8"));
                md.update(abbreviatedBuildingType.getBytes("utf-8"));
                md.update(buildingType.getBytes("utf-8"));
                md.update(doorNo.getBytes("utf-8"));
                md.update(floor.getBytes("utf-8"));
                md.update(side.getBytes("utf-8"));
                md.update(place.getBytes("utf-8"));
                md.update(locality.getBytes("utf-8"));
                md.update(zip4.getBytes("utf-8"));
                md.update(zip3.getBytes("utf-8"));
                md.update(postalLocality.getBytes("utf-8"));
                md.update(GenAddressNo.getBytes("utf-8"));
            } else {
                md.update(country.getBytes("utf-8"));
                md.update(countryDescriptionF.getBytes("utf-8"));
                md.update(addressF.getBytes("utf-8"));
                md.update(cityF.getBytes("utf-8"));
                md.update(regionF.getBytes("utf-8"));
                md.update(localityF.getBytes("utf-8"));
                md.update(postalCodeF.getBytes("utf-8"));
                md.update(GenAddressNoF.getBytes("utf-8"));
            }
            digest = md.digest();
        }
        
        return digest;
    }
    
    
    private void checkNLoad(){
        if (!isdataLoaded){
            parse(data);
        }
    }
    
    
    private void parse(byte[] data) {
        type = new String(data, 0, 2, StandardCharsets.UTF_8).trim();
        if (type.equals(NACIONAL)) {
            country = new String(data, 2, 4, StandardCharsets.UTF_8).trim();
            districtCode = new String(data, 6, 4, StandardCharsets.UTF_8).trim();
            district = new String(data, 10, 100, StandardCharsets.UTF_8).trim();
            municipalityCode = new String(data, 110, 8, StandardCharsets.UTF_8).trim();
            municipality = new String(data, 118, 100, StandardCharsets.UTF_8).trim();
            civilParishCode = new String(data, 218, 12, StandardCharsets.UTF_8).trim();
            civilParish = new String(data, 230, 100, StandardCharsets.UTF_8).trim();
            abbreviatedStreetType = new String(data, 330, 20, StandardCharsets.UTF_8).trim();
            streetType = new String(data, 350, 100, StandardCharsets.UTF_8).trim();
            streetName = new String(data, 450, 200, StandardCharsets.UTF_8).trim();
            abbreviatedBuildingType = new String(data, 650, 20, StandardCharsets.UTF_8).trim();
            buildingType = new String(data, 670, 100, StandardCharsets.UTF_8).trim();
            doorNo = new String(data, 770, 20, StandardCharsets.UTF_8).trim();
            floor = new String(data, 790, 40, StandardCharsets.UTF_8).trim();
            side = new String(data, 830, 40, StandardCharsets.UTF_8).trim();
            place = new String(data, 870, 100, StandardCharsets.UTF_8).trim();
            locality = new String(data, 970, 100, StandardCharsets.UTF_8).trim();
            zip4 = new String(data, 1070, 8, StandardCharsets.UTF_8).trim();
            zip3 = new String(data, 1078, 6, StandardCharsets.UTF_8).trim();
            postalLocality = new String(data, 1084, 50, StandardCharsets.UTF_8).trim();
            GenAddressNo = new String(data, 1134, 12, StandardCharsets.UTF_8).trim();            
        } else {
            country = new String(data, 2, 4, StandardCharsets.UTF_8).trim();
            countryDescriptionF = new String(data, 6, 100, StandardCharsets.UTF_8).trim();
            addressF = new String(data, 106, 300, StandardCharsets.UTF_8).trim();
            cityF = new String(data, 406, 100, StandardCharsets.UTF_8).trim();
            regionF = new String(data, 506, 100, StandardCharsets.UTF_8).trim();
            localityF = new String(data, 606, 100, StandardCharsets.UTF_8).trim();
            postalCodeF = new String(data, 706, 100, StandardCharsets.UTF_8).trim();
            GenAddressNoF = new String(data, 806, 12, StandardCharsets.UTF_8).trim();
        }
        isdataLoaded = true;
    }
}

