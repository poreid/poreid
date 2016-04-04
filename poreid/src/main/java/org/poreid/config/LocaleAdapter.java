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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author POReID
 */
public class LocaleAdapter extends XmlAdapter<LocaleAdapter.Locale, java.util.Locale> {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"language", "country"})
    public static class Locale {
        @XmlAttribute(name = "language")
        public String language;
        @XmlAttribute(name = "country")
        public String country;
        
        public Locale() { };
        
        public Locale(java.util.Locale locale){
            this.language = locale.getLanguage();
            this.country = locale.getCountry();
        }
    }

    @Override
    public java.util.Locale unmarshal(Locale locale) throws Exception {
        return new java.util.Locale(locale.language, locale.country);
    }

    @Override
    public Locale marshal(java.util.Locale locale) throws Exception {
       return new Locale(locale);
    }
}
