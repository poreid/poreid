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

import org.poreid.json.JSONObject;

/**
 *
 * @author POReID
 */
class OnlineTransactionParameters {

    private String pan;
    private String panSeqNumber;
    private String cdol1;
    private String atc;
    private String arqc;
    private int counter;

    OnlineTransactionParameters setPan(String pan) {
        this.pan = pan;
        return this;
    }

    OnlineTransactionParameters setPanSeqNumber(String panSeqNumber) {
        this.panSeqNumber = panSeqNumber;
        return this;
    }

    OnlineTransactionParameters setCdol1(String cdol1) {
        this.cdol1 = cdol1;
        return this;
    }

    OnlineTransactionParameters setAtc(String atc) {
        this.atc = atc;
        return this;
    }

    OnlineTransactionParameters setArqc(String arqc) {
        this.arqc = arqc;
        return this;
    }

    OnlineTransactionParameters setCounter(int counter) {
        this.counter = counter;
        return this;
    }

    @Override
    public String toString() {
        JSONObject contents = new JSONObject();
        contents.put("pan", pan);
        contents.put("panseqnumber", panSeqNumber);
        contents.put("cdol1", cdol1);
        contents.put("atc", atc);
        contents.put("arqc", arqc);
        contents.put("counter", counter);
        JSONObject onlineTransactionParameters = new JSONObject();
        onlineTransactionParameters.put("OnlineTransactionParameters", contents);

        return onlineTransactionParameters.toString();
    }
}
