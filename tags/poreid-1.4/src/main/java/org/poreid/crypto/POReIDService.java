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
package org.poreid.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Provider.Service;
import java.util.Map;
import org.poreid.config.POReIDConfig;

/**
 *
 * @author POReID
 */
public class POReIDService extends Service {

    public POReIDService(final Provider provider, final String type, final String algorithm, final String className) {
        super(provider, type, algorithm, className, null, null);
    }

    public POReIDService(final Provider provider, final String type, final String algorithm, final String className, final Map<String, String> attributes) {
        super(provider, type, algorithm, className, null, attributes);
    }

    @Override
    public Object newInstance(final Object constructorParameter) throws NoSuchAlgorithmException {
        if (super.getType().equals(POReIDConfig.DIGITAL_SIGNATURE)) {
            return new POReIDSignature(this.getAlgorithm());
        }
        return super.newInstance(constructorParameter);
    }
}
