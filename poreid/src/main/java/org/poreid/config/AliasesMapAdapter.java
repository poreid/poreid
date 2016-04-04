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
import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author POReID
 */
public class AliasesMapAdapter extends XmlAdapter<AliasesMapAdapter.AMap, Map<String, String>> {

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AMap {
        @XmlElement(name = "alias-of")
        public List<Content> entry = new ArrayList<>();
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Content {
        
        @XmlAttribute(name="unique-id")
        private String id;
        
        @XmlElement(name="alias") 
        public List<String> entry = new ArrayList<>();
    }
    
    
    @Override
    public Map<String, String> unmarshal(AMap v) throws Exception {
         Map<String, String> map = new HashMap<>();
         for(Content entry : v.entry) {
             for(String st : entry.entry){
                 map.put(st, entry.id);
             }
         }
         
         return map;
    }

    @Override
    public AMap marshal(Map<String, String> v) throws Exception {
        AMap amap = new AMap();
        Map<String, Content> temp = new HashMap<>();
        
        for (String st : v.values()) {
            Content content = new Content();
            content.id = st;
            amap.entry.add(content);
            temp.put(st, content);
        }

        for (Entry<String, String> entry : v.entrySet()) {
            temp.get(entry.getValue()).entry.add(entry.getKey());
        }
        
        return amap;
    }
    
}
