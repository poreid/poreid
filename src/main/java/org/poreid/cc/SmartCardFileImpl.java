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
package org.poreid.cc;

import org.poreid.Pin;
import org.poreid.SmartCardFile;

/**
 *
 * @author POReID
 */
final class SmartCardFileImpl implements SmartCardFile{
    private String fileId;
    private boolean isCacheable;
    private int offset = -1;
    private int lenght = -1;
    private String suffix = null;
    private boolean isUpdateable;
    private int diffOffset = -1;
    private int diffLenght = -1;
    private Pin pin;
    private int size=-1;
    
    
    protected SmartCardFileImpl(String fileId, boolean isCacheable, Pin pin){
        this.fileId = fileId;
        this.isCacheable = isCacheable;
        this.pin = pin;
    }
    
    protected SmartCardFileImpl(String fileId, int maximumSize, boolean isCacheable, Pin pin){
        this.fileId = fileId;
        this.isCacheable = isCacheable;
        this.pin = pin;
        this.size = maximumSize;
    }
    
    protected SmartCardFileImpl(String fileId, boolean isCacheable){
        this.fileId = fileId;
        this.isCacheable = isCacheable;
    }
   
    
    protected SmartCardFileImpl(String fileId, boolean isCacheable, int offset, int lenght, String suffix){
        this(fileId,isCacheable);
        this.offset = offset;
        this.lenght = lenght;
        this.suffix = suffix;
    }
    
    
    protected SmartCardFileImpl(String fileId, boolean isCacheable, String suffix){
        this(fileId,isCacheable);
        this.suffix = suffix; 
    }
    
    
    protected SmartCardFileImpl(String fileId, boolean isCacheable, int offset, String suffix){
        this(fileId,isCacheable);
        this.suffix = suffix;
        this.offset = offset;
    }
    
    
    protected SmartCardFileImpl(String fileId, boolean isCacheable, int diffOffset, int diffLenght){
        this(fileId,isCacheable);
        this.diffOffset = diffOffset;
        this.diffLenght = diffLenght;
        this.isUpdateable = true;
    }
    
    @Override
    public String getFileId(){
        return fileId;
    }
    
    
    @Override
    public boolean isCacheable(){
        return isCacheable;
    }
    
    
    @Override
    public int getOffset(){
        return offset;
    }
    
    
    @Override
    public int getLenght(){
        return lenght;
    }
    
    
    @Override
    public String getSuffix(){
        return suffix;
    }
    
    
    @Override
    public boolean isUpdateable(){
        return isUpdateable;
    }
    
    
    @Override
    public int getDiffLenght(){
        return diffLenght;
    }
    
    
    @Override
    public int getDiffOffset(){
        return diffOffset;
    } 

    @Override
    public Pin getPin() {
        return pin;
    }

    @Override
    public int getMaximumSize() {
        return size;
    }
}
