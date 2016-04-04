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

import org.poreid.PkAlias;

/**
 *
 * @author POReID
 */
final class Files {

    public final SmartCardFileImpl ID;
    public final SmartCardFileImpl PHOTO;
    public final SmartCardFileImpl PUBLIC_KEY;
    public final SmartCardFileImpl NOTES;
    public final SmartCardFileImpl ADDRESS;
    public final SmartCardFileImpl SOD;
    public final SmartCardFileImpl EF_5032;
    public final SmartCardFileImpl AODF;
    public final SmartCardFileImpl QualifiedSignatureCertificate;
    public final SmartCardFileImpl QualifiedSignatureSubCACertificate;
    public final SmartCardFileImpl AuthenticationCertificate;
    public final SmartCardFileImpl AuthenticationSubCACertificate;

    protected Files(CardSpecificReferences csr) {
        ID = new SmartCardFileImpl("3F005F00EF02", true, 0, 1372, "_ID");
        PHOTO = new SmartCardFileImpl("3F005F00EF02", true, 1503, "_PHOTO");
        PUBLIC_KEY = new SmartCardFileImpl("3F005F00EF02", true, 1372, 131, "_PK");
        NOTES = new SmartCardFileImpl("3F005F00EF07", 1000, false,csr.getCryptoReferences(PkAlias.AUTENTICACAO));
        ADDRESS = new SmartCardFileImpl("3F005F00EF05", false,csr.getAddressPin());
        SOD = new SmartCardFileImpl("3F005F00EF06", true, 134, 32);
        EF_5032 = new SmartCardFileImpl("3F004F005032", false);
        AODF = new SmartCardFileImpl("3F005F004401", false);
        QualifiedSignatureCertificate = new SmartCardFileImpl("3F005F00EF08", true);
        QualifiedSignatureSubCACertificate = new SmartCardFileImpl("3F005F00EF0F", true);
        AuthenticationCertificate = new SmartCardFileImpl("3F005F00EF09", true);
        AuthenticationSubCACertificate = new SmartCardFileImpl("3F005F00EF10", true);
    }
}
