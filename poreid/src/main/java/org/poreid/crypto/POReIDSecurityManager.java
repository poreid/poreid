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
package org.poreid.crypto;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author POReID
 */
public class POReIDSecurityManager extends SecurityManager {
    private static final Set<String> PROPERTIES_NOT_AUTHORIZED = new HashSet<>(6);
    private final SecurityManager securityManager;

    static {
        PROPERTIES_NOT_AUTHORIZED.add("clearProviderProperties." + POReIDProvider.NAME);
        PROPERTIES_NOT_AUTHORIZED.add("putProviderProperty." + POReIDProvider.NAME);
        PROPERTIES_NOT_AUTHORIZED.add("removeProviderProperty." + POReIDProvider.NAME);
        PROPERTIES_NOT_AUTHORIZED.add("readDisplayPixels");
        PROPERTIES_NOT_AUTHORIZED.add("setSecurityManager");
        PROPERTIES_NOT_AUTHORIZED.add("setPolicy");
    }

    
    POReIDSecurityManager(final SecurityManager securityManager) {
        super();

        this.securityManager = securityManager;
    }

    
    @Override
    public void checkPermission(final Permission perm) {
        if (PROPERTIES_NOT_AUTHORIZED.contains(perm.getName())) {
            throw new SecurityException("Não é permitida a operação: " + perm);
        }

        if (null!= securityManager){
            securityManager.checkPermission(perm);
        }
    }

    
    @Override
    public void checkMemberAccess(final Class<?> clazz, final int which) {
        if (null!= securityManager){
            securityManager.checkMemberAccess(clazz, which);
        }
    }

    
    @Override
    public void checkPackageDefinition(final String pkg) {
        super.checkPackageDefinition(pkg);
        if (pkg != null && pkg.startsWith("por.eid")) {
            throw new SecurityException("O prefixo por.eid está reservado para utilização no provider " + pkg);
        }

        if (null != securityManager) {
            securityManager.checkPackageDefinition(pkg);
        }
    }

    
    @Override
    public void checkAccept(final String host, final int port) {
        if (null!= securityManager){
            securityManager.checkAccept(host, port);
        }
    }

    
    @Override
    public void checkAccess(final Thread t) {
        super.checkAccess(t);
        if (null!= securityManager){
            securityManager.checkAccess(t);
        }
    }

    
    @Override
    public void checkAccess(final ThreadGroup g) {
        super.checkAccess(g);
        if (null != securityManager) {
            securityManager.checkAccess(g);
        }
    }

    
    @Override
    public void checkAwtEventQueueAccess() {
        if (null != securityManager) {
            securityManager.checkAwtEventQueueAccess();
        }
    }

    
    @Override
    public void checkConnect(final String host, final int port) {
        if (null != securityManager) {
            securityManager.checkConnect(host, port);
        }
    }

    
    @Override
    public void checkConnect(final String host, final int port, final Object context) {
        if (null != securityManager) {
            securityManager.checkConnect(host, port, context);
        }
    }

    
    @Override
    public void checkCreateClassLoader() {
        if (null != securityManager) {
            securityManager.checkCreateClassLoader();
        }
    }

    
    @Override
    public void checkDelete(final String file) {
        if (null != securityManager) {
            securityManager.checkDelete(file);
        }
    }

    
    @Override
    public void checkExec(final String cmd) {
        if (null != securityManager) {
            securityManager.checkExec(cmd);
        }
    }

    
    @Override
    public void checkExit(final int status) {
        if (null != securityManager) {
            securityManager.checkExit(status);
        }
    }

    
    @Override
    public void checkLink(final String lib) {
        if (null != securityManager) {
            securityManager.checkLink(lib);
        }
    }

    
    @Override
    public void checkListen(final int port) {
        if (null != securityManager) {
            securityManager.checkListen(port);
        }
    }

    
    @Override
    public void checkMulticast(final InetAddress maddr) {
        if (null != securityManager) {
            securityManager.checkMulticast(maddr);
        }
    }

    
    @Deprecated
    @Override
    public void checkMulticast(final InetAddress maddr, final byte ttl) {
        if (null != securityManager) {
            securityManager.checkMulticast(maddr, ttl);
        }
    }

    
    @Override
    public void checkPackageAccess(final String pkg) {
        super.checkPackageAccess(pkg);

        if (null != securityManager) {
            securityManager.checkPackageAccess(pkg);
        }
    }

    
    @Override
    public void checkPermission(final Permission perm, final Object context) {
        if (null != securityManager) {
            securityManager.checkPermission(perm, context);
        }
    }

    
    @Override
    public void checkPrintJobAccess() {
        if (null != securityManager) {
            securityManager.checkPrintJobAccess();
        }
    }

    
    @Override
    public void checkPropertiesAccess() {
        if (null != securityManager) {
            securityManager.checkPropertiesAccess();
        }
    }

    
    @Override
    public void checkPropertyAccess(final String key) {
        if (null != securityManager) {
            securityManager.checkPropertyAccess(key);
        }
    }

    
    @Override
    public void checkRead(final FileDescriptor fd) {
        if (null != securityManager) {
            securityManager.checkRead(fd);
        }    
    }

    
    @Override
    public void checkRead(final String file) {
        if (null != securityManager) {
            securityManager.checkRead(file);
        }
    }

    
    @Override
    public void checkRead(final String file, final Object context) {
        securityManager.checkRead(file, context);
    }

    
    @Override
    public void checkSecurityAccess(final String target) {
        if (null != securityManager) {
            securityManager.checkSecurityAccess(target);
        }
    }

    
    @Override
    public void checkSetFactory() {
        if (null != securityManager) {
            securityManager.checkSetFactory();
        }
    }

    
    @Override
    public void checkSystemClipboardAccess() {
        if (null != securityManager) {
            securityManager.checkSystemClipboardAccess();
        }
    }

    
    @Override
    public boolean checkTopLevelWindow(final Object window) {      
        if (null != securityManager) {
            return securityManager.checkTopLevelWindow(window);
        }
        
        return false;
    }

    
    @Override
    public void checkWrite(final FileDescriptor fd) {
        if (null != securityManager) {
            securityManager.checkWrite(fd);
        }
    }

    
    @Override
    public void checkWrite(final String file) {
        if (null != securityManager) {
            securityManager.checkWrite(file);
        }
    }
}