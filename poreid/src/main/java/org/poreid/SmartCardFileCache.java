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
package org.poreid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.poreid.config.POReIDConfig;

/**
 * Implementação da cache 
 * @author POReID
 */
public final class SmartCardFileCache {
    private final static Logger LOGGER = Logger.getLogger(SmartCardFileCache.class.getName());
    private final String prefix;
    private final CacheStatus cacheStatus;
    private byte[] keyAES;  // isto não é mais do que uma cortina de fumo, não deixar a cache em locais publicos.
    private byte[] keyHMAC;
    private static final int SHA_256_DIGEST_LEN = 32;
    private static final int AES_KEY_LEN = 16;
    private File location;

    /**
     * Constroi uma instância
     * @param prefix Prefixo utilizado para mapear os cartões com os ficheiros da cache
     * @param cacheStatus Estado da cache
     * @param data Dados utilizados para cifrar a cache
     */
    public SmartCardFileCache(String prefix, CacheStatus cacheStatus, byte[] data){
        this.prefix = prefix;
        
        this.cacheStatus = cacheStatus;
        
        if (SHA_256_DIGEST_LEN == data.length) {
            keyAES = Arrays.copyOfRange(data, 0, AES_KEY_LEN);
            keyHMAC = Arrays.copyOfRange(data, AES_KEY_LEN, data.length);            
            location = new File(POReIDConfig.cacheLocation);
        }
    }

    /**
     * Retorna o nome do ficheiro na cache
     * @param file Ficheiro no smartcard
     * @return nome do ficheiro na cache
     */
    public String getSCFileCacheFileName(SmartCardFile file) {
        return prefix + "_" + file.getFileId() + (null == file.getSuffix() || file.getSuffix().isEmpty() ? "" : file.getSuffix());
    }

    /**
     * Verifica se o ficheiro existe em cache
     * @param fileId Identificador do ficheiro no smartcard
     * @return true se existe em cache, falso se não existe
     */
    public boolean isCached(String fileId) {
        boolean isCached = false;
        
        if (cacheStatus.isEnabled() && location.exists()) {            
            isCached = new File(POReIDConfig.cacheLocation + fileId).exists();           
        }
        
        return isCached;
    }

    /**
     * Compara e retorna o conteúdo do ficheiro em cache
     * @param file Ficheiro no smartcard
     * @param contentToCompare Conteúdo a comparar entre a cache e o ficheiro no smartcard
     * @return ficheiro em cache ou null se a cache não for válida
     */
    public byte[] readNCheckCacheFile(SmartCardFile file, byte[] contentToCompare){
        byte[] contents;

        contents = readCachedFile(getSCFileCacheFileName(file));
        if (null != contents && Arrays.equals(Arrays.copyOfRange(contents, file.getDiffOffset(), file.getDiffOffset() + file.getDiffLenght()), contentToCompare)) {
            return contents;
        }
        return null; /* null - correto */ 
    }

    /**
     * Retorna o conteúdo do ficheiro em cache
     * @param fileId Identificador do ficheiro no smartcard
     * @return conteudo do ficheiro em cache ou null se a cache estiver inativa
     */
    public byte[] readCachedFile(String fileId) {
        byte[] data = new byte[8192];
        byte[] cachedData = null;

        if (cacheStatus.isEnabled()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataInputStream dinput = new DataInputStream(new BufferedInputStream(new FileInputStream(POReIDConfig.cacheLocation + fileId)))) {
                if (dinput.readInt() == POReIDConfig.getPOReIDVersion()) {
                    int count;
                    while ((count = dinput.read(data)) != -1) {
                        baos.write(data, 0, count);
                    }
                    cachedData = decipherCache(baos.toByteArray());                    
                }
            } catch (IOException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ignore) {
            }

            if (null == cachedData) {
                removeCacheFile(fileId);
            }

            return cachedData;
        } else {
            return null; /* null - correto */ 
        }
    }

    /**
     * Escreve conteúdo do ficheiro na cache
     * @param fileId Identificador do ficheiro no smartcard
     * @param contents Conteúdo a escrever no ficheiro de cache
     */
    public void writeCacheFile(String fileId, byte[] contents) {
        if (cacheStatus.isEnabled() && (location.exists() || location.mkdir())) {
            try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(POReIDConfig.cacheLocation + fileId))))) {
                dos.writeInt(POReIDConfig.getPOReIDVersion());
                dos.write(cipherCache(contents));
                dos.flush();
                dos.close();
            } catch (FileNotFoundException ignore) {
            } catch (InvalidAlgorithmParameterException | IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ignore) {
            }
        }
    }
    
    
    private byte[] cipherCache(byte[] contents) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
        Cipher cipher; 
        byte[] ivData;
        IvParameterSpec iv;
        byte[] cipheredContent;
        Mac mac;
        byte[] hmac;
        byte[] bundle;
        byte[] date;
        
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        ivData = new byte[cipher.getBlockSize()];
        SecureRandom.getInstance("SHA1PRNG").nextBytes(ivData);
        iv = new IvParameterSpec(ivData);
        
        date = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(System.currentTimeMillis()).array();                
        
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(this.keyAES, "AES"),iv);
        cipher.update(date);
        cipheredContent = cipher.doFinal(contents);
        
        mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(this.keyHMAC, "HmacSHA1"));
        hmac = mac.doFinal(cipheredContent);
        
        bundle = new byte[ivData.length + hmac.length + cipheredContent.length];
        System.arraycopy(ivData, 0, bundle, 0, ivData.length);
        System.arraycopy(hmac, 0, bundle, ivData.length, hmac.length);
        System.arraycopy(cipheredContent, 0, bundle, ivData.length+hmac.length, cipheredContent.length);
        
        return bundle;
    }
    
    
    private byte[] decipherCache(byte[] bundle) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
        Cipher cipher;
        byte[] ivData;
        IvParameterSpec iv;
        Mac mac;
        byte[] cipheredContent;
        byte[] decipheredContent;
               
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");       
        mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(this.keyHMAC, "HmacSHA1"));
        
        if (bundle.length < cipher.getBlockSize()+mac.getMacLength()){
            return null; /* null - correto */ 
        }
        
        cipheredContent = Arrays.copyOfRange(bundle, cipher.getBlockSize()+mac.getMacLength(),bundle.length);
        if (!Arrays.equals(mac.doFinal(cipheredContent), Arrays.copyOfRange(bundle, cipher.getBlockSize(),cipher.getBlockSize()+mac.getMacLength()))){
            return null;
        }
        
        ivData = Arrays.copyOfRange(bundle, 0, cipher.getBlockSize());
        iv = new IvParameterSpec(ivData);

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.keyAES, "AES"), iv);        
        decipheredContent = cipher.doFinal(cipheredContent);
        
        long cacheDate = ((ByteBuffer) (ByteBuffer.allocate(Long.SIZE / Byte.SIZE).put(Arrays.copyOfRange(decipheredContent, 0, 8)).flip())).getLong();

        return cacheStatus.isValid(cacheDate) ? Arrays.copyOfRange(decipheredContent, 8, decipheredContent.length) : null;        
    }
    
    
    private void removeCacheFile(String fileId) {
        try {
            Files.deleteIfExists(FileSystems.getDefault().getPath(POReIDConfig.cacheLocation, fileId));
        } catch (IOException ignore) {
        }
    }
    
    
    public void enforceCacheThreshold() {                
        if (cacheStatus.isEnabled() && POReIDConfig.getCacheThreshold() != POReIDConfig.NO_CACHE_THRESHOLD) {
            Thread thread = new Thread(new Work(),"enforce cache threshold");
            thread.start();
        }
    }
    
    
    class CacheInfo {
        private final ArrayList<String> fileNames;
        private long millis;
        
        public CacheInfo(String fileName, long millis){
            fileNames = new ArrayList<>();
            fileNames.add(fileName);
            this.millis = millis;
        }
        
        
        public void updateMillis(long millis){
            if (millis < this.millis){
                this.millis = millis;
            }
        }
        
        
        public void addFileName(String fileName){
            fileNames.add(fileName);
        }
        
        public long getMillis(){
            return millis;
        }
        
        public String[] getFileNames(){            
            return fileNames.toArray(new String[0]);
        }                
    }
    
    
    class Work implements Runnable{
        @Override
        public void run() {
            final SortedMap<String, CacheInfo> myMap = new TreeMap<>();
            SortedSet<Map.Entry<String, CacheInfo>> sortedset = new TreeSet<>(
                    new Comparator<Map.Entry<String, CacheInfo>>() {
                        @Override
                        public int compare(Map.Entry<String, CacheInfo> o1, Map.Entry<String, CacheInfo> o2) {
                            if (o1.getValue().getMillis() > o2.getValue().getMillis()) {
                                return -1;
                            }
                            if (o1.getValue().getMillis() < o2.getValue().getMillis()) {
                                return 1;
                            }
                            return 0;
                        }
                    });

            try {
                Files.walkFileTree(Paths.get(POReIDConfig.cacheLocation), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (attrs.isRegularFile()) {
                            String key = file.getFileName().toString().split("_")[0];
                            if (myMap.containsKey(key)) {
                                myMap.get(key).updateMillis(attrs.creationTime().toMillis());
                                myMap.get(key).addFileName(file.getFileName().toString());
                            } else {
                                myMap.put(key, new CacheInfo(file.getFileName().toString(), attrs.creationTime().toMillis()));
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });

                sortedset.addAll(myMap.entrySet());

                if (sortedset.size() > POReIDConfig.getCacheThreshold()) {
                    int ignore = 0;
                    for (Map.Entry<String, CacheInfo> element : sortedset) {
                        if (ignore < POReIDConfig.getCacheThreshold()) {
                            ignore++;
                        } else {
                            for (String fileName : element.getValue().getFileNames()) {                                
                                removeCacheFile(fileName);
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                LOGGER.log(Level.INFO, "Não foi possível expurgar a cache.{0}", ex);
            }
        }                
    }
}
