/*
 * Copyright (c) 2012, Redbilled.fr. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.poreid.security.pcscforjava;

import org.poreid.pcscforjava.CardException;
import org.poreid.pcscforjava.CardTerminals;
import static org.poreid.security.pcscforjava.PCSCDefines.*;
import static org.poreid.security.pcscforjava.PCSC.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which manages the Plug & Play class Thread.
 *
 * @since   1.6
 * @author  Matthieu Leromain
 */
public class PCSCPnPThread implements Runnable {
    
    /**
     * Indicates if we stop or continue the execution of the thread.
     */
    private static boolean          m_bStop = true;
    /**
     * The list of {@link CardTerminals CardTerminals}.
     */
    private List<CardTerminals>     m_cardTerminals;
    /**
     * The Plug & Play context id.
     */
    private long                    m_lContextId;
    /**
     * The Plug & Play thread instance.
     */    
    private static PCSCPnPThread    m_instance;
    /**
     * The real thread.
     */
    private static Thread           m_thread;
    
    /**
     * Private constructor: singleton mode.
     * @param cardTerminals the list of {@link CardTerminals CardTerminals}.
     */
    private PCSCPnPThread(CardTerminals cardTerminals)
    {
        this.m_cardTerminals = new ArrayList<CardTerminals>();
        this.m_cardTerminals.add(cardTerminals);
        try { this.m_lContextId = SCardEstablishContext(SCARD_SCOPE_SYSTEM); }
        catch(PCSCException ex){}
    }
    
    /**
     * Returns an instance of the Plug & Play class Thread.
     * @param cardTerminals the list of CardTerminals.
     * @return an instance of the Plug & Play class Thread.
     */
    synchronized public static PCSCPnPThread getInstance(CardTerminals cardTerminals)
    {
        if(null == m_instance)
            m_instance = new PCSCPnPThread(cardTerminals);
        
        return m_instance;
    }
    
    /**
     * Start the Plug & Play therad.
     * @return the Plug & Play thread started.
     */
    public Thread start()
    {
        if(m_thread == null)
        {
            m_bStop = false;
            m_thread = new Thread(this, "PCSCPnPThread");
            m_thread.setDaemon(true);
            m_thread.setPriority(Thread.MIN_PRIORITY);
            m_thread.start();
            try { Thread.sleep(1000); } catch (InterruptedException ex) {}
        }
        
        return m_thread;
    }
    
    /**
     * Adds an observer to the Plug & Play class Thread.
     * @param cardTerminals the new observer.
     */
    public void addObserver(CardTerminals cardTerminals)
    {
        boolean _bFound = false;

        for(int _i = 0; _i < this.m_cardTerminals.size(); _i++)
        {
            if(this.m_cardTerminals.get(_i).equals(cardTerminals))
            {
                _bFound = true;
                break;
            }
        }

        if(!_bFound)
            this.m_cardTerminals.add(cardTerminals);
    }
    
    /**
     * Remove an observer of the Plug & Play class Thread.
     * @param cardTerminals the observer to remove.
     */
    public void removeObserver(CardTerminals cardTerminals)
    {
        boolean _bFound = false;

        for(int _i = 0; _i < this.m_cardTerminals.size(); _i++)
        {
            if(this.m_cardTerminals.get(_i).equals(cardTerminals))
            {
                _bFound = true;
                break;
            }
        }

        if(_bFound)
            this.m_cardTerminals.remove(cardTerminals);
    }
    
    /**
     * The thread running method.
     */
    @Override
    public void run() {
        boolean _bEvent = false;
        boolean _bThereIsProblem = false;

        while(!m_bStop)
        {
            try
            {
                if(this.m_lContextId == 0)
                {
                    this.m_lContextId = SCardEstablishContext(SCARD_SCOPE_SYSTEM);
                    _bThereIsProblem = true;
                }
                                    
                
                if(this.m_lContextId != 0)
                {
                    if(_bThereIsProblem)
                    {
                        _bThereIsProblem = false;
                        _bEvent = true;
                    }
                    
                    if(!_bEvent)
                    {
                            _bEvent = SCardPlugAndPlay(m_lContextId, 500);
                    }
                    
                    
                    if(_bEvent)
                    {
                        // A Plug & Play event occurs.
                        for(int _i = 0; _i < this.m_cardTerminals.size(); _i++)
                        {
                            try {
                                this.m_cardTerminals.get(_i).updateCardTerminalsListByEvent();
                            } catch (CardException ex) {
                                System.err.println("PCSCPnPThread run error " 
                                        + ex.getMessage());
                            }
                        }
                        
                        _bEvent = false;
                    }
                }
            }
            catch(PCSCException ex) {}

            try {
                if(System.getProperty("os.name").contains("Windows"))
                    Thread.sleep(50);
                else
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                this.stop();
            }
        }
        
        m_instance = null;
        m_thread = null;
    }
    
    /**
     * Signal an event to the observers.
     */
    public void signalEvent()
    {
        for(int _i = 0; _i < this.m_cardTerminals.size(); _i++)
        {
            try {
                this.m_cardTerminals.get(_i).updateCardTerminalsListByEvent();
            } catch (CardException ex) {
                System.err.println("PCSCPnPThread signalEvent Error " 
                        + ex.getMessage());
            }
        }
    }
    
    /**
     * Stop the Plug & Play thread.
     */
    public void stop()
    {
        m_bStop = true;
    }

    /**
     * Returns true if a Plug & Play event occurs.
     * @param lContextId Handle that identifies the resource manager context. 
     * The resource manager context can be set by a previous call to 
     * SCardEstablishContext.
     * @param lTimeout the timeout of the Plug & Play detection.
     * @return true if a Plug & Play event occurs.
     * @throws PCSCException if a PCSC exception occurs.
     */
    private native boolean SCardPlugAndPlay(long lContextId, long lTimeout)
            throws PCSCException;
}
