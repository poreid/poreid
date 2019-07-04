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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author POReID
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"supportedSmartCards", "smartCardReaders", "locale", "externalPinCache", "timedInteraction", "cacheThreshold"})
public class Configuration {
    @XmlJavaTypeAdapter(POReIDSupportedSmarCardsMapAdapter.class)
    @XmlElement(name = "poreid-supported-smartcards", required = true)
    private Map<String, POReIDSupportedSmartCardProperties> supportedSmartCards = new HashMap<>();
   
    @XmlElement(name = "smartcard-pinpad-readers", required = true)
    private SmartCardPinPadReaders smartCardReaders;
    
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @XmlElement(name = "default-locale", required = true)
    private Locale locale;
    
    @XmlJavaTypeAdapter(PinCacheAdapter.class)
    @XmlElement(name = "allow-external-pin-caching", required = true)
    private Boolean externalPinCache;
    
    @XmlElement(name = "timed-interaction", required = true)
    private TimedInteraction timedInteraction;
    
    @XmlElement(name = "cache-threshold")
    private int cacheThreshold; 

    
    public Map<String, POReIDSupportedSmartCardProperties> getSupportedSmartCards() {
        return supportedSmartCards;
    }

    
    public void setSupportedSmartCards(HashMap<String, POReIDSupportedSmartCardProperties> supportedSmarCards) {
        this.supportedSmartCards = supportedSmarCards;
    }

    
    public SmartCardPinPadReaders getSmartCardPinPadReaders() {
        return smartCardReaders;
    }

    
    public void setSmartCardPinPadReaders(SmartCardPinPadReaders pinpadReaders) {
        this.smartCardReaders = pinpadReaders;
    }

    
    public Locale getLocale() {
        return locale;
    }

    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    
    public boolean isExternalPinCachePermitted(){
        return (null == externalPinCache) ? false : externalPinCache;
    }
    
    
    public void setExternalPinCache(boolean status){
        this.externalPinCache = status;
    }
    

    public TimedInteraction getTimedInteraction() {
        return timedInteraction;
    }

     
    public void setTimedInteraction(TimedInteraction timedInteraction) {
        this.timedInteraction = timedInteraction;
    }
    
    
    public int getcacheThreshold(){
        return cacheThreshold;
    }
    
    
    public void setcacheThreshold(int cacheThreshold){
        this.cacheThreshold = cacheThreshold;
    }
}
