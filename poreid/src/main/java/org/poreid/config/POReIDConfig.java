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

package org.poreid.config;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.poreid.CacheStatus;
import org.xml.sax.SAXException;

/**
 *
 * @author POReID
 */
public class POReIDConfig {
    public static final String LAF = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
    public static final String LAF_SHORT_NAME = "Nimbus";
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
    public static final String IMAGE_ERROR_LOCATION = "/org/poreid/images/erro.png";
    public static final String IMAGE_WARNING_LOCATION = "/org/poreid/images/aviso.png";
    public static final String IMAGE_SIGNATURE_LOCATION = "/org/poreid/images/assinatura.png";
    public static final String BACKGROUND_SIGNATURE_LOCATION = "/org/poreid/images/fundo-assinatura.png";
    public static final String BACKGROUND_SMALL_SIGNATURE_LOCATION = "/org/poreid/images/fundo-assinatura-min.png";
    public static final String IMAGE_AUTHENTICATION_LOCATION = "/org/poreid/images/autenticacao.png";
    public static final String BACKGROUND_AUTHENTICATION_LOCATION = "/org/poreid/images/fundo-autenticacao.png";
    public static final String BACKGROUND_SMALL_AUTHENTICATION_LOCATION = "/org/poreid/images/fundo-autenticacao-min.png";
    public static final int NO_CACHE_THRESHOLD = 0;
    private static final String I18N_BUNDLE_LOCATION = "org.poreid.i18n.";
    private static final String XML_SCHEMA = "/org/poreid/config/schema/poreid.config.xsd";
    private static final String CONFIGURACAO = "/org/poreid/config/xml/poreid.config.xml";
    private static Configuration config;
    private static final int version = 0x02;
    
    
    public static synchronized void init(ConfigurationOverrider configurationOverrider) {
        if(config !=null) {
            return;
        }
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(POReIDConfig.class.getResource(XML_SCHEMA));
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
            Unmarshaller u = jaxbContext.createUnmarshaller();
            u.setSchema(schema);
            u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
            config = (Configuration) u.unmarshal(POReIDConfig.class.getResource(CONFIGURACAO));
            if (configurationOverrider != null) {
                configurationOverrider.override(config);
            }
        } catch (JAXBException | SAXException ex) {
            Logger.getLogger(POReIDConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Configuration getConfig() {
        if(config == null) {
            init(null);
        }
        return config;
    }
    
    public static int getPOReIDVersion(){
        return version;
    }
    
    
     public static ResourceBundle getBundle(String simpleName, Locale locale){
        return ResourceBundle.getBundle(I18N_BUNDLE_LOCATION + simpleName, locale);
    }
    
    public static String getSmartCardImplementingClassName(String atr){
        String implementingClass = null;
        
        if (getConfig().getSupportedSmartCards().containsKey(atr)){
            implementingClass = getConfig().getSupportedSmartCards().get(atr).getImplementingClass();
        }
        
        return implementingClass;
    }
    
    
    public static CacheStatus getSmartCardCacheStatus(String atr){
        CacheStatus cacheStatus = null;
        
        
        if (getConfig().getSupportedSmartCards().containsKey(atr)){
            cacheStatus = new CacheStatus(getConfig().getSupportedSmartCards().get(atr).isCacheEnabled(), getConfig().getSupportedSmartCards().get(atr).getValidity());
        }
        
        return cacheStatus;
    }
    
    
    public static java.util.Locale getDefaultLocale(){
        return getConfig().getLocale();
    }
    
    
    private static String getUniqueReaderID(String readerName){
        String uniqueName = getConfig().getSmartCardPinPadReaders().getAliases().get(readerName);
        
        if (null == uniqueName){
            uniqueName = getConfig().getSmartCardPinPadReaders().getAliases().get(POReIDConfig.GENERIC_READER);
        }
        
        return uniqueName;
    }
    
    
    public static String getSmartCardReaderImplementingClassName(String readerName) {
        String implementingClass = null;
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            if (getConfig().getSmartCardPinPadReaders().getSmartCardReaders().containsKey(uniqueName)) {
                implementingClass = getConfig().getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getImplementingClass();
            }
        }

        return implementingClass;
    }
    
    
    public static boolean getVerifyPinSupport(String readerName, String implementingClass) {
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            return getConfig().getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedOses().get(detectOS().value()).isVerify() && getPinPadVerifyPinSupportedSmardCards(readerName,implementingClass);
        }
        
        return true;
    }

    
    public static boolean getModifyPinSupport(String readerName, String implementingClass) {
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            return getConfig().getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedOses().get(detectOS().value()).isModify() && getPinPadModifyPinSupportedSmardCards(readerName,implementingClass);
        }
        
        return true;
    }

    
    public static boolean getOSInjectPinSupport(String readerName) {
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            return getConfig().getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedOses().get(detectOS().value()).isInject();
        }
        
        return true;
    }
    
    
    public static boolean isExternalPinCachePermitted(){
        return getConfig().isExternalPinCachePermitted();
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
            if (getConfig().getSmartCardPinPadReaders().getSmartCardReaders().containsKey(uniqueName) && getConfig().getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedSmartCards().containsKey(implementingClass)){
                return getConfig().getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedSmartCards().get(implementingClass).isVerify();
            }
        }
    
        return true;
    }
    
    
    private static boolean getPinPadModifyPinSupportedSmardCards(String readerName, String implementingClass){
        String uniqueName;

        if (null != (uniqueName = getUniqueReaderID(readerName))) {
            if (getConfig().getSmartCardPinPadReaders().getSmartCardReaders().containsKey(uniqueName) && getConfig().getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedSmartCards().containsKey(implementingClass)){
                return getConfig().getSmartCardPinPadReaders().getSmartCardReaders().get(uniqueName).getSupportedSmartCards().get(implementingClass).isModify();
            }
        }
    
        return true;
    }
    
    
    public static boolean isTimedInteractionEnabled(){
        return getConfig().getTimedInteraction().isEnabled();
    }
    
    
    public static int timedInteractionPeriod() {
        return getConfig().getTimedInteraction().isEnabled() ? getConfig().getTimedInteraction().getPeriod() : 0;
    }
    
    public static int getCacheThreshold(){
        return getConfig().getcacheThreshold();
    }
}
