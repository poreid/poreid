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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author POReID
 */
public class SupportedSmartCardsMapAdapter extends XmlAdapter<SupportedSmartCardsMapAdapter.SCCAdaptedMap, Map<String, SmartCardSupportedOperations>> {

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SCCAdaptedMap {
        @XmlElement(name="smartcard") 
        public List<SupportedSmartCardsMapAdapter.SCCAEntry> entry = new ArrayList<>();
  
    }
    
     
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SCCAEntry {
        
        @XmlElement(name="implementing-class") 
        public String implementingClass;
        @XmlElement(name="properties") 
        public SmartCardSupportedOperations properties;
    }
    
    @Override
    public Map<String, SmartCardSupportedOperations> unmarshal(SCCAdaptedMap amap) throws Exception {
        Map<String, SmartCardSupportedOperations> map = new HashMap<>();
        for(SCCAEntry entry : amap.entry) {
            map.put(entry.implementingClass, entry.properties);
        }
        return map;
    }

    @Override
    public SCCAdaptedMap marshal(Map<String, SmartCardSupportedOperations> map) throws Exception {
        SCCAdaptedMap adaptedMap = new SCCAdaptedMap();
        for(Map.Entry<String, SmartCardSupportedOperations> mapEntry : map.entrySet()) {
            SCCAEntry entry = new SCCAEntry();
            entry.implementingClass = mapEntry.getKey();
            entry.properties = mapEntry.getValue();
            adaptedMap.entry.add(entry);
        }
        return adaptedMap;
    }
    
}
