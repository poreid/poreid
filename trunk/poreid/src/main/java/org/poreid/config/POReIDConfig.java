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

package org.poreid.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author POReID
 */
public class POReIDConfig {   
    public static final String LAF = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
    public static final String GENERIC_READER = "generic reader";
    public static final String POREID = "POReID";
    public static final String RSA = "RSA";
    public static final String DIGITAL_SIGNATURE = "Signature";
    public static final String NONE = "NONE";
    public static final String SUPPORTED_KEY_CLASSES = "SupportedKeyClasses";
    public static final String KEYSTORE = "KeyStore";
    public static final String KEY_MANAGER_FACTORY = "KeyManagerFactory";
    public static final String AUTENTICACAO = "Autenticacao";
    public static final String ASSINATURA = "Assinatura";
    public static final String cacheDirectory = ".poreidcache";
    public static final String cacheLocation = System.getProperty("user.home") + System.getProperty("file.separator") + cacheDirectory + System.getProperty("file.separator");
    public static final String AUTHORIZED_INVOCATION = "org.poreid.cc.OTP";
    private static final String XML_SCHEMA = "/poreid.config.xsd";
    private static final String CONFIGURACAO = "/poreid.config.xml";
    private static Configuration config;
    private static final int version = 0x01;
    
    
    static {
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(POReIDConfig.class.getResource(XML_SCHEMA));
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
            Unmarshaller u = jaxbContext.createUnmarshaller();
            u.setSchema(schema);
            u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
            config = (Configuration) u.unmarshal(POReIDConfig.class.getResource(CONFIGURACAO));
        } catch (JAXBException | SAXException ex) {
            Logger.getLogger(POReIDConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static int getPOReIDVersion(){
        return version;
    }
    
    public static String getSmartCardImplementingClassName(String atr){
        String implementingClass = null;
        
        if (config.getSupportedSmartCards().containsKey(atr)){
            implementingClass = config.getSupportedSmartCards().get(atr).getImplementingClass();
        }
        
        return implementingClass;
    }
    
    
    public static boolean getSmartCardCacheStatus(String atr){
        boolean cacheStatus = false;
        
        if (config.getSupportedSmartCards().containsKey(atr)){
            cacheStatus = config.getSupportedSmartCards().get(atr).isCacheEnabled();
        }
        
        return cacheStatus;
    }
    
    
    public static java.util.Locale getDefaultLocale(){
        return config.getLocale();
    }
    
    
    private static String getUniqueReaderID(String readerName){
        String uniqueName = config.getSmartCardPinPadReaders().getAliases().get(readerName);
        
        if (null == uniqueName){
            uniqueName = config.getSmartCardPinPadReaders().getAliases().get(POReIDConfig.GENERIC_READER); 
        }
        
        return uniqueName;
    }
    
    
    public static String getSmartCardReaderImplementingClassName(String readerName) {
        String implementingClass = null;
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            if (config.getSmartCardPinPadReaders().getSmartCardReaders().containsKey(uniqueName)) {
                implementingClass = config.getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getImplementingClass();
            }
        }

        return implementingClass;
    }
    
    
    public static boolean getVerifyPinSupport(String readerName, String implementingClass) {
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            return config.getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedOses().get(detectOS().value()).isVerify() & getPinPadVerifyPinSupportedSmardCards(readerName,implementingClass);
        }
        
        return true;
    }

    
    public static boolean getModifyPinSupport(String readerName, String implementingClass) {
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            return config.getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedOses().get(detectOS().value()).isModify() & getPinPadModifyPinSupportedSmardCards(readerName,implementingClass);
        }
        
        return true;
    }

    
    public static boolean getOSInjectPinSupport(String readerName) {
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            return config.getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedOses().get(detectOS().value()).isInject();
        }
        
        return true;
    }
    
    
    public static boolean isExternalPinCachePermitted(){
        return config.isExternalPinCachePermitted();
    }
    
    
    private static OsType detectOS() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return OsType.WINDOWS;
        }
        if (os.contains("mac")) {
            return OsType.MAC;
        }
        if (os.contains("nux")) {
            return OsType.LINUX;
        }
        
        throw new RuntimeException("Não foi possivel identificar o sistema operativo.");
    }
    
    
    private static boolean getPinPadVerifyPinSupportedSmardCards(String readerName, String implementingClass){
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            if (config.getSmartCardPinPadReaders().getSmartCardReaders().containsKey(uniqueName) && config.getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedSmartCards().containsKey(implementingClass)){
                return config.getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedSmartCards().get(implementingClass).isVerify();
            }
        }
    
        return true;
    }
    
    
    private static boolean getPinPadModifyPinSupportedSmardCards(String readerName, String implementingClass){
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            if (config.getSmartCardPinPadReaders().getSmartCardReaders().containsKey(uniqueName) && config.getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedSmartCards().containsKey(implementingClass)){
                return config.getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedSmartCards().get(implementingClass).isModify();
            }
        }
    
        return true;
    }
}
