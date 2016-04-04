/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
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

import org.poreid.pcscforjava.CardTerminal;
import org.poreid.pcscforjava.CardTerminals;
import org.poreid.pcscforjava.PCSCErrorValues;
import org.poreid.pcscforjava.CardException;
import org.poreid.pcscforjava.TerminalFactory;
import org.poreid.pcscforjava.CardTerminalsEvent;
import java.util.*;
import java.lang.ref.*;

import static org.poreid.pcscforjava.CardTerminals.State.*;

import static org.poreid.security.pcscforjava.PCSC.*;
import static org.poreid.security.pcscforjava.PCSCDefines.*;

/**
 * TerminalFactorySpi implementation class.
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  Matthieu Leromain
 */
final class PCSCTerminals extends CardTerminals {

    /**
     * SCARDCONTEXT, currently shared between all threads/terminals
     */
    protected static long contextId;

    /**
     * Terminal state used by waitForCard()
     */
    private Map<String,ReaderState> stateMap;

    /**
     * Plug & Play class Thread
     */
    static private PCSCPnPThread     m_cardTerminalsThread = null;
    
    /**
     * Plug & Play thread
     */
    static private Thread           m_thread;

    /**
     * Last state for the list method
     */
    private State                   m_state;

    /**
     * List of CardTerminal objects.
     */
    private List<CardTerminal>      list;

    /**
     * The current list.
     */
    private Object                  m_currentList;
    
    /**
     * Constructs a new CardTerminals object.
     *
     * <p>This constructor is called by subclasses only. Application should
     * call {@linkplain TerminalFactory#terminals}
     * to obtain a CardTerminals object.
     */
    PCSCTerminals() {
        // empty
    }

    /**
     * Initializes the PCSC context.
     * @throws PCSCException if a PCSC exception occurs.
     */
    static synchronized void initContext() throws PCSCException {
        if (contextId == 0) {
            contextId = SCardEstablishContext(SCARD_SCOPE_SYSTEM);
        }
    }
    
    /**
     * Releases the PCSC context.
     * @throws PCSCException if a PCSC exception occurs.
     */
    static synchronized void releaseContext() throws PCSCException {
        SCardReleaseContext(contextId);
        contextId = 0;
    }
    
    /**
     * The Hashmap of terminals.
     */
    private static final Map<String,Reference<TerminalImpl>> terminals
        = new HashMap<String,Reference<TerminalImpl>>();

    /**
     * Gets the terminal.
     * @param name the name of the terminal.
     * @return the terminal.
     */
    private static synchronized TerminalImpl implGetTerminal(String name) {
        Reference<TerminalImpl> ref = terminals.get(name);
        TerminalImpl terminal = (ref != null) ? ref.get() : null;
        if (terminal != null) {
            terminal.setContextId(contextId);
            return terminal;
        }
        terminal = new TerminalImpl(contextId, name);
        terminals.put(name, new WeakReference<TerminalImpl>(terminal));

        return terminal;
    }
    
    /**
     * Returns an unmodifiable list of all terminals matching the specified
     * state.<br />
     *
     * <p>If state is {@link State#ALL State.ALL}, this method returns
     * all CardTerminals encapsulated by this object.
     * If state is {@link State#CARD_PRESENT State.CARD_PRESENT} or
     * {@link State#CARD_ABSENT State.CARD_ABSENT}, it returns all
     * CardTerminals where a card is currently present or absent, respectively.
     *
     * <p>If state is {@link State#CARD_INSERTION State.CARD_INSERTION} or
     * {@link State#CARD_REMOVAL State.CARD_REMOVAL}, it returns all
     * CardTerminals for which an insertion (or removal, respectively)
     * was detected during the last call to {@linkplain #waitForChange}.
     * If <code>waitForChange()</code> has not been called on this object,
     * <code>CARD_INSERTION</code> is equivalent to <code>CARD_PRESENT</code>
     * and <code>CARD_REMOVAL</code> is equivalent to <code>CARD_ABSENT</code>.
     * For an example of the use of <code>CARD_INSERTION</code>,
     * see {@link #waitForChange}.
     *
     * @param state the State
     * @return an unmodifiable list of all terminals matching the specified
     *   attribute.
     *
     * @throws NullPointerException if attr is null
     * @throws CardException if the card operation failed
     */
    public synchronized List<CardTerminal> list(State state) throws CardException {
        if (state == null) {
            throw new NullPointerException();
        }
        
        try 
        {
            // Be sure that the context is well established
            initContext();
        } catch (PCSCException ex) 
        {
            this.startPnPThread();
            this.m_currentList = Collections.emptyList();
            return (List<CardTerminal>) this.m_currentList;
        }
      
        if(contextId == 0)
        {
            this.startPnPThread();
            this.m_currentList = Collections.emptyList();
            return (List<CardTerminal>) this.m_currentList;
        }

        this.m_state = state;

        try {
            String[] readerNames = SCardListReaders(contextId);
            
            list = new ArrayList<CardTerminal>(readerNames.length);
            if (stateMap == null) {
                // If waitForChange() has never been called, treat event
                // queries as status queries.
                if (state == CARD_INSERTION) {
                    state = CARD_PRESENT;
                } else if (state == CARD_REMOVAL) {
                    state = CARD_ABSENT;
                }
            }

            updateTerminalsHashMap(readerNames);

            for (String readerName : readerNames) {
                CardTerminal terminal = implGetTerminal(readerName);
                ReaderState readerState;
                switch (state) {
                case ALL:
                    list.add(terminal);
                    // Just to be sure that the card terminal is real
                    try { terminal.isCardPresent(); }
                    catch(Exception ex){ list.remove(terminal); }
                    break;
                case CARD_PRESENT:
                    if (terminal.isCardPresent()) {
                        list.add(terminal);
                    }
                    break;
                case CARD_ABSENT:
                    if (terminal.isCardPresent() == false) {
                        list.add(terminal);
                    }
                    break;
                case CARD_INSERTION:
                    readerState = stateMap.get(readerName);
                    if ((readerState != null) && readerState.isInsertion()) {
                        list.add(terminal);
                    }
                    break;
                case CARD_REMOVAL:
                    readerState = stateMap.get(readerName);
                    if ((readerState != null) && readerState.isRemoval()) {
                        list.add(terminal);
                    }
                    break;
                default:
                    throw new CardException("org.poreid.pcscforjava."
                    + "PCSCTerminals.list "
                    + "PCSCException: SCARD_F_UNKNOWN_ERROR " 
                    + "Unknown state: " + state);
                }
            }
           
            this.startPnPThread();
            this.m_currentList = Collections.unmodifiableList(list);
        } catch (PCSCException e) {
            if(e.getMessage().contains("SCARD_E_NO_READERS_AVAILABLE"))
            {
                this.startPnPThread();
                this.m_currentList = Collections.emptyList();
            }
            else
                throw new CardException("org.poreid.pcscforjava."
                    + "PCSCTerminals.list "
                    + "PCSCException: " + e.getMessage(), e);
        }

        return (List<CardTerminal>) this.m_currentList;
    }

    private void updateTerminalsHashMap(String[] readerNames)
    {
        int         _iNbModification = 0;
        Set         _keys = terminals.keySet();
        Iterator    _it = _keys.iterator();
        String      _key = null;
        int         _iNbKeys = _keys.size(), _i = 0;

        while(_i < _iNbKeys)
        {
            boolean _bFound = false;
            
            try
            {
               _key = (String) _it.next();
               
                for (String readerName : readerNames)
                {
                    if(readerName.equalsIgnoreCase(_key))
                    {
                        _bFound = true;
                        break;
                    }
                }
            }
            catch(Exception ex) {// If exception remove it
            }

            if(!_bFound)
            {
                _iNbModification++;
                ((TerminalImpl)terminals.get(_key).get()).notifyDisconnection();
                terminals.remove(_key);
                _it = _keys.iterator();
                _iNbKeys = _keys.size();
                _i = -1;
            }
            
            _i++;
        }

        // If there is no disconnection it is possible that a reader has just
        // restarted or new connection so in each case the last reader has no
        // correct card.
        if((_iNbModification == 0) && (_key != null))
            ((TerminalImpl)terminals.get(_key).get()).notifyDisconnection();
    }

    /**
     * Check if the current resource manager context is in valid state or not.
     * <br />It is a good way to be sure that the current resources are always
     * available and up to date.
     *
     * @return false if the current resource manager context is no more valid,
     * true otherwise.
     */
    @Override
    public boolean isValidContext() {
        try
        {
            SCardIsValidContext(contextId);
            return true;
        }
        catch (PCSCException ex)
        {
            return false;
        }
    }

    /**
     * Closes an established resource manager context.
     * It is a good way to finish the use of the smart card API.
     *
     * @throws CardException if the card operation failed
     */
    @Override
    public void closeContext() throws CardException {
    
        if(m_cardTerminalsThread != null)
        {
            m_cardTerminalsThread.stop();
            m_cardTerminalsThread = null;
        }
        
        try
        {
            SCardReleaseContext(contextId);
            contextId = 0;
        }
        catch(PCSCException ex)
        {
            throw new CardException("org.poreid.pcscforjava."
                    + "PCSCTerminals.closeContext "
                    + "PCSCException: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Indicates if the Plug & Play is supported by the library or not.
     * @param lContextId 
     * @return true if it is supported.\n
     * false otherwise.
     */
    private native boolean SCardIsPlugAndPlaySupported(long lContextId)
            throws PCSCException;
    
    /**
     * Returns if the {@linkg CardTerminals CardTerminals} object is managed
     * by Plug & Play or not.
     * @return false if the object does not support Plug & Play mode.
     * <br /> true otherwise.
     * @throws CardException if a card operation failed
     */
    public boolean isPlugAndPlaySupported() throws CardException
    {
        try
        {   
            if(contextId != 0)
                return SCardIsPlugAndPlaySupported(contextId);
        }
        catch(PCSCException ex)
        {
            throw new CardException("org.poreid.pcscforjava."
                    + "PCSCTerminals.isPlugAndPlaySupported "
                    + "PCSCException: " + ex.getMessage(), ex);
        }
        
        return false;
    }
    
    /**
     * Starts the Plug & Play thread.
     */
    private void startPnPThread()
    {
        if(m_cardTerminalsThread == null)
        {
            m_cardTerminalsThread = PCSCPnPThread.getInstance(this);
            m_thread = m_cardTerminalsThread.start();
            m_cardTerminalsThread.addObserver(this);
        }
        else
        {
            //m_cardTerminalsThread.addObserver(this);
        }
    }
    
    /**
     * Updates the card terminals list when an event of type card terminal
     * insertion / removal occurs.
     *
     * <p>This method is called as a callback by subclasses only. Application
     * should call {@linkplain TerminalFactory#terminals} which launch the
     * detection thread and call this method.</p>
     * 
     * @throws CardException if a card operation falied.
     */
    public void updateCardTerminalsListByEvent() throws CardException
    {
        if (this.m_state == null)
            this.m_state = ALL;
        
        try  {
            // Be sure that the context is well established
            initContext();
        } catch (PCSCException ex) {return;}
        
        try {
            String[] readerNames = SCardListReaders(contextId);

            List<CardTerminal> _tmpList =
                    new ArrayList<CardTerminal>(readerNames.length);

            updateTerminalsHashMap(readerNames);

            for (String readerName : readerNames) {
                CardTerminal terminal = implGetTerminal(readerName);
                ReaderState readerState;
                switch (this.m_state) {
                case ALL:
                    _tmpList.add(terminal);
                    break;
                case CARD_PRESENT:
                    if (terminal.isCardPresent()) {
                        _tmpList.add(terminal);
                    }
                    break;
                case CARD_ABSENT:
                    if (terminal.isCardPresent() == false) {
                        _tmpList.add(terminal);
                    }
                    break;
                case CARD_INSERTION:
                    readerState = stateMap.get(readerName);
                    if ((readerState != null) && readerState.isInsertion()) {
                        _tmpList.add(terminal);
                    }
                    break;
                case CARD_REMOVAL:
                    readerState = stateMap.get(readerName);
                    if ((readerState != null) && readerState.isRemoval()) {
                        _tmpList.add(terminal);
                    }
                    break;
                default:
                    throw new CardException("org.poreid.pcscforjava."
                    + "PCSCTerminals.updateCardTerminalsListByEvent "
                    + "PCSCException: SCARD_F_UNKNOWN_ERROR " + 
                            "Unknown state: " + this.m_state);
                }
            }

            this.m_currentList = _tmpList;
            
        } catch (PCSCException e) {
            if(e.getMessage().contains("SCARD_E_NO_READERS_AVAILABLE"))
            {
                this.m_currentList = Collections.emptyList();
            }
            else
                throw new CardException("org.poreid.pcscforjava."
                    + "PCSCTerminals.updateCardTerminalsListByEvent "
                    + "PCSCException: " + e.getMessage(), e);
        }
        
        List _tmp = TerminalFactory.getPnPCallbacks();

        if(_tmp != null)
        {
            for(int _i = 0; _i < _tmp.size(); _i++)
                ((CardTerminalsEvent)_tmp.get(_i)).
                        updateCardTerminalsListByEvent((List<CardTerminal>)
                        this.m_currentList);
        }
    }
    
    /**
     * The reader state class.
     */
    private static class ReaderState {
        private int current, previous;
        ReaderState() {
            current = SCARD_STATE_UNAWARE;
            previous = SCARD_STATE_UNAWARE;
        }
        int get() {
            return current;
        }
        void update(int newState) {
            previous = current;
            current = newState;
        }
        boolean isInsertion() {
            return !present(previous) && present(current);
        }
        boolean isRemoval() {
            return present(previous) && !present(current);
        }
        static boolean present(int state) {
            return (state & SCARD_STATE_PRESENT) != 0;
        }
    }

    /**
     * Waits for card insertion or removal in any of the terminals of this
     * object or until the timeout expires.
     *
     * <p>This method examines each CardTerminal of this object.
     * If a card was inserted into or removed from a CardTerminal since the
     * previous call to <code>waitForChange()</code>, it returns
     * immediately.
     * Otherwise, or if this is the first call to <code>waitForChange()</code>
     * on this object, it blocks until a card is inserted into or removed from
     * a CardTerminal.
     *
     * <p>If <code>timeout</code> is greater than 0, the method returns after
     * <code>timeout</code> milliseconds even if there is no change in state.
     * In that case, this method returns <code>false</code>; otherwise it
     * returns <code>true</code>.
     *
     * <p>This method is often used in a loop in combination with
     * {@link #list(CardTerminals.State) list(State.CARD_INSERTION)},
     * for example:
     * <pre>
     *  TerminalFactory factory = ...;
     *  CardTerminals terminals = factory.terminals();
     *  while (true) {
     *      for (CardTerminal terminal : terminals.list(CARD_INSERTION)) {
     *          // examine Card in terminal, return if it matches
     *      }
     *      terminals.waitForChange();
     *  }</pre>
     *
     * @param timeout if positive, block for up to <code>timeout</code>
     *   milliseconds; if zero, block indefinitely; must not be negative
     * @return false if the method returns due to an expired timeout,
     *   true otherwise.
     *
     * @throws IllegalStateException if this <code>CardTerminals</code>
     *   object does not contain any terminals
     * @throws IllegalArgumentException if timeout is negative
     * @throws CardException if the card operation failed
     */
    public synchronized boolean waitForChange(long timeout) 
            throws CardException {
        if (timeout < 0) {
            throw new IllegalArgumentException
                ("Timeout must not be negative: " + timeout);
        }
        if (stateMap == null) {
            // We need to initialize the state database.
            // Do that with a recursive call, which will return immediately
            // because we pass SCARD_STATE_UNAWARE.
            // After that, proceed with the real call.
            stateMap = new HashMap<String,ReaderState>();
            waitForChange(0);
        }
        if (timeout == 0) {
            timeout = TIMEOUT_INFINITE;
        }
        try {
            String[] readerNames = SCardListReaders(contextId);
            int n = readerNames.length;
            if (n == 0) {
                throw new IllegalStateException("No terminals available");
            }
            int[] status = new int[n];
            ReaderState[] readerStates = new ReaderState[n];
            for (int i = 0; i < readerNames.length; i++) {
                String name = readerNames[i];
                ReaderState state = stateMap.get(name);
                if (state == null) {
                    state = new ReaderState();
                }
                readerStates[i] = state;
                status[i] = state.get();
            }
            status = SCardGetStatusChange(contextId, timeout, status,
                    readerNames);
            stateMap.clear(); // remove any readers that are no longer available
            for (int i = 0; i < n; i++) {
                ReaderState state = readerStates[i];
                if(status != null)
                    state.update(status[i]);
                stateMap.put(readerNames[i], state);
            }
            return true;
        } catch (PCSCException e) {
            if (e.code == PCSCErrorValues.SCARD_E_TIMEOUT) {
                return false;
            } else {
                throw new CardException("org.poreid.pcscforjava."
                    + "PCSCTerminals.waitForChange "
                    + "PCSCException: " + e.getMessage(), e);
            }
        }
    }

    static List<CardTerminal> waitForCards(
            List<? extends CardTerminal> terminals,
            long timeout, boolean wantPresent) throws CardException {
        // the argument sanity checks are performed in
        // javax.smartcardio.TerminalFactory or TerminalImpl

        long thisTimeout;
        if (timeout == 0) {
            timeout = TIMEOUT_INFINITE;
            thisTimeout = TIMEOUT_INFINITE;
        } else {
            // if timeout is not infinite, do the initial call that retrieves
            // the status with a 0 timeout. Otherwise, we might get incorrect
            // timeout exceptions (seen on Solaris with PC/SC shim)
            thisTimeout = 0;
        }

        String[] names = new String[terminals.size()];
        int i = 0;
        for (CardTerminal terminal : terminals) {
            if (terminal instanceof TerminalImpl == false) {
                throw new IllegalArgumentException
                    ("Invalid terminal type: " + terminal.getClass().getName());
            }
            TerminalImpl impl = (TerminalImpl)terminal;
            names[i++] = impl.name;
        }

        int[] status = new int[names.length];
        Arrays.fill(status, SCARD_STATE_UNAWARE);

        try {
            while (true) {
                // note that we pass "timeout" on each native PC/SC call
                // that means that if we end up making multiple (more than 2)
                // calls, we might wait too long.
                // for now assume that is unlikely and not a problem.
                status = SCardGetStatusChange(contextId, thisTimeout,
                        status, names);
                thisTimeout = timeout;

                List<CardTerminal> results = null;
                for (i = 0; i < names.length; i++) {
                    boolean nowPresent = (status[i] & SCARD_STATE_PRESENT) != 0;
                    if (nowPresent == wantPresent) {
                        if (results == null) {
                            results = new ArrayList<CardTerminal>();
                        }
                        results.add(implGetTerminal(names[i]));
                    }
                }

                if (results != null) {
                    return Collections.unmodifiableList(results);
                }
            }
        } catch (PCSCException e) {
            if (e.code == PCSCErrorValues.SCARD_E_TIMEOUT) {
                return Collections.emptyList();
            } else {
                throw new CardException("org.poreid.pcscforjava."
                    + "PCSCTerminals.waitForCard "
                    + "PCSCException: " + e.getMessage(), e);
            }
        }
    }

}
