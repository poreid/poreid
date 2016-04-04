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
public class POReIDSupportedSmarCardsMapAdapter extends XmlAdapter<POReIDSupportedSmarCardsMapAdapter.SSAdaptedMap, Map<String, POReIDSupportedSmartCardProperties>> {
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SSAdaptedMap {
        @XmlElement(name="smartcard") 
        public List<SSEntry> entry = new ArrayList<>();
  
    }
    
    
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SSEntry {
        
        @XmlElement(name="atr") 
        public String atr;
        @XmlElement(name="properties") 
        public POReIDSupportedSmartCardProperties properties;
    }
    
    
    @Override
    public Map<String, POReIDSupportedSmartCardProperties> unmarshal(SSAdaptedMap amap) throws Exception {
        Map<String, POReIDSupportedSmartCardProperties> map = new HashMap<>();
        for(SSEntry entry : amap.entry) {
            map.put(entry.atr, entry.properties);
        }
        return map;
    }

    
    @Override
    public SSAdaptedMap marshal(Map<String, POReIDSupportedSmartCardProperties> map) throws Exception {
        SSAdaptedMap adaptedMap = new SSAdaptedMap();
        for(Map.Entry<String, POReIDSupportedSmartCardProperties> mapEntry : map.entrySet()) {
            SSEntry entry = new SSEntry();
            entry.atr = mapEntry.getKey();
            entry.properties = mapEntry.getValue();
            adaptedMap.entry.add(entry);
        }
        return adaptedMap;
    }  
}
