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

package org.poreid.pcscforjava;

import org.poreid.pcscforjava.sun.internal.GetPropertyAction;
//import org.poreid.pcscforjava.sun.internal.GetInstance;
import org.poreid.security.pcscforjava.PCSC4Java;
import java.util.*;
import java.security.*;
import java.security.Provider.Service;
import org.poreid.pcscforjava.sun.internal.GetInstance;
import org.poreid.pcscforjava.sun.internal.GetInstance.Instance;
//import sun.security.jca.*;
//import sun.security.jca.GetInstance.*;

/**
 * A factory for {@link CardTerminal CardTerminal} objects.<br /><br />
 *
 * It allows an application to
 * <ul>
 * <li>obtain a TerminalFactory by calling
 * one of the static factory methods in this class
 * ({@linkplain #getDefault} or {@linkplain #getInstance getInstance()}).
 * <li>use this TerminalFactory object to access the CardTerminals by
 * calling the {@linkplain #terminals} method.
 * </ul>
 *
 * <p>Each TerminalFactory has a <code>type</code> indicating how it
 * was implemented. It must be specified when the implementation is obtained
 * using a {@linkplain #getInstance getInstance()} method and can be retrieved
 * via the {@linkplain #getType} method.
 *
 * <P>The following standard type names have been defined:
 * <dl>
 * <dt><code>PC/SC</code>
 * <dd>an implementation that calls into the PC/SC Smart Card stack
 * of the host platform.
 * Implementations do not require parameters and accept "null" as argument
 * in the getInstance() calls.
 * <dt><code>None</code>
 * <dd>an implementation that does not supply any CardTerminals. On platforms
 * that do not support other implementations,
 * {@linkplain #getDefaultType} returns <code>None</code> and
 * {@linkplain #getDefault} returns an instance of a <code>None</code>
 * TerminalFactory. Factories of this type cannot be obtained by calling the
 * <code>getInstance()</code> methods.
 * </dl>
 * Additional standard types may be defined in the future.
 *
 * <p><strong>Note:</strong>
 * Provider implementations that accept initialization parameters via the
 * <code>getInstance()</code> methods are strongly
 * encouraged to use a {@linkplain java.util.Properties} object as the
 * representation for String name-value pair based parameters whenever
 * possible. This allows applications to more easily interoperate with
 * multiple providers than if each provider used different provider
 * specific class as parameters.
 *
 * <P>TerminalFactory utilizes an extensible service provider framework.
 * Service providers that wish to add a new implementation should see the
 * {@linkplain TerminalFactorySpi} class for more information.
 *
 * @see CardTerminals
 * @see Provider
 *
 * @since   1.6
 * @author  Andreas Sterbenz
 * @author  JSR 268 Expert Group
 * @author  Matthieu Leromain
 */
public final class TerminalFactory {

    /**
     * The name of the factory.
     */
    private final static String PROP_NAME =
                        "org.poreid.pcscforjava.TerminalFactory.DefaultType";

    /**
     * The default type name.
     */
    private static String defaultType;
    
    /**
     * The default factory.
     */
    private static TerminalFactory defaultFactory;

    /**
     * The list of Plug & Play callbacks.<br />
     * Each callback is called when a Plug & Play event occurs.
     */
    private static List m_pnpCallbacks = new ArrayList<CardTerminalsEvent>();
    
    /**
     * To be sure that the service PCSC is correctly started.
     */
    private static boolean m_bPCSCServiceIsStarted = false;
    
    /**
     * Static constructor ...
     */
    static
    {
        constructPCSCService(true);
    }

    /**
     * Constructor to the PC/SC Service.
     * @param bConstructor indicates if the call is made by the constructor
     * or by an other method.
     * @return true if the PC/SC Service is correctly started.<br />
     * false otherwise.
     */
    protected static boolean constructPCSCService(boolean bConstructor)
    {
        if(!m_bPCSCServiceIsStarted)
        {
        // Lookup up the user specified type, default to PC/SC
        String type = AccessController.doPrivileged
                            (new GetPropertyAction(PROP_NAME, "PC/SC")).trim();
        TerminalFactory factory = null;
       
        try
        {
            type = "PC/SC";
            Provider _pcsc4Java = Security.getProvider("PCSC4Java");
                    if (_pcsc4Java == null)
            {
                _pcsc4Java = new PCSC4Java();
            }

            factory = TerminalFactory.getInstance(type, null, _pcsc4Java);

                m_bPCSCServiceIsStarted = true;
        }
        catch (Exception e) 
        {
                m_bPCSCServiceIsStarted = false;
        }

            if(m_bPCSCServiceIsStarted)
            {
        if (factory == null)
        {
            type = "None";
            factory = new TerminalFactory
                                (NoneFactorySpi.INSTANCE, NoneProvider.INSTANCE,
                                "None");
        }

        defaultType = type;
        defaultFactory = factory;
    }
        }

        return m_bPCSCServiceIsStarted;
    }
    
    /**
     * Adds a Plug & Play callback to the list.
     * @param cardTerminalsEvent the {@link CardTerminalsEvent 
     * CardTerminalsEvent} object which contains the callback to call.
     */
    public static void setPnPCallback(CardTerminalsEvent cardTerminalsEvent)
    {
        m_pnpCallbacks.add(cardTerminalsEvent);
    }

    /**
     * Removes a Plug & Play callback to the list.
     * @param cardTerminalsEvent the {@link CardTerminalsEvent 
     * CardTerminalsEvent} object which contains the callback to remove.
     */
    public static void removePnPCallback(CardTerminalsEvent cardTerminalsEvent)
    {
        for(int _i = 0; _i < m_pnpCallbacks.size(); _i++)
        {
            if(m_pnpCallbacks.get(_i) == cardTerminalsEvent)
            {
                m_pnpCallbacks.remove(_i);
                break;
            }
        }
    }
    
    /**
     * Gets the Plug & Play callbacks list.
     * @return the Plug & Play callbacks list.
     */
    public static List getPnPCallbacks()
    {
        return m_pnpCallbacks;
    }
    
    /**
     * Releases the context of the terminal factory.<br />
     * Makes a proper stop of the factory.
     */
    public void releaseContext() {
        defaultFactory.spi.destroyTerminals();
        m_bPCSCServiceIsStarted = false;
    }
    
    /**
     * Defines a {@link NoneProvider NoneProvider} class.
     */
    private static final class NoneProvider extends Provider {
        final static Provider INSTANCE = new NoneProvider();
        private NoneProvider() {
            super("None", 1.0d, "none");
        }
    }
    
    /**
     * Defines a {@link NoneFactorySpi NoneFactorySpi} class.
     */
    private static final class NoneFactorySpi extends TerminalFactorySpi {
        final static TerminalFactorySpi INSTANCE = new NoneFactorySpi();
        private NoneFactorySpi() {
            // empty
        }
        
        /**
         * Returns a {@link NoneCardTerminals NoneCardTerminals} intance.
         * @return a {@link NoneCardTerminals NoneCardTerminals} intance.
         */
        protected CardTerminals engineTerminals() {
            return NoneCardTerminals.INSTANCE;
        }

        /**
         * Returns false because it is not possible to destroy terminals which
         * do not exist.
         * @return false because it is not possible to destroy terminals which
         * do not exist.
         */
        @Override
        protected boolean destroyTerminals() {
            return false;
        }
    }

    /**
     * Defines a {@link NoneCardTerminals NoneCardTerminals} class.
     */
    private static final class NoneCardTerminals extends CardTerminals {
        final static CardTerminals INSTANCE = new NoneCardTerminals();
        private NoneCardTerminals() {
            // empty
        }
        
        
        /**
         * Returns an empty list because there is no terminal matching the 
         * specified state.<br /><br />
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
         * @return an empty list because there is no terminal matching the 
         * specified state.
         *
         * @throws NullPointerException if state is null
         * @throws CardException if the card operation failed
         */
        public List<CardTerminal> list(State state) throws CardException {
            if (state == null) {
                throw new NullPointerException();
            }
            
            return Collections.emptyList();
        }
        
        /**
         * Waits for card insertion or removal in any of the terminals of this
         * object.
         *
         * <p>This call is equivalent to calling
         * {@linkplain #waitForChange(long) waitForChange(0)}.
         *
         * @throws IllegalStateException if this <code>CardTerminals</code>
         *   object does not contain any terminals
         * @throws CardException if the card operation failed
         */
        public boolean waitForChange(long timeout) throws CardException {
            throw new IllegalStateException("no terminals");
        }
        
        /**
         * Check if the current resource manager context is in valid state or not.
         * <br />It is a good way to be sure that the current resources are always
         * available and up to date.
         *
         * @return false: the current resource manager context is no more valid.
         */
        @Override
        public boolean isValidContext() {
            return false;
        }
            
        /**
         * Closes an established resource manager context.
         * It is a good way to finish the use of the smart card API.
         *
         * @throws CardException if the card operation failed
         */
        @Override
        public void closeContext() throws CardException {
            // Nothing
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
        @Override
        public void updateCardTerminalsListByEvent() throws CardException {
            // empty
        }
        
        /**
         * Returns if the {@linkg CardTerminals CardTerminals} object is managed
         * by Plug & Play or not.
         * @return false if the object does not support Plug & Play mode.
         * <br /> true otherwise.
         * @throws CardException if a card operation failed
         */
        @Override
        public boolean isPlugAndPlaySupported() throws CardException {
            return false;
        }
    }
    
    /**
     * The terminal factory spi.
     */
    private final TerminalFactorySpi spi;

    /**
     * The provider.
     */
    private final Provider provider;

    /**
     * The type name.
     */
    private final String type;
    
    /**
     * Private constructor to get the terminal factory spi, the provider and
     * the type.
     * @param spi the terminal factory spi.
     * @param provider the provider.
     * @param type the type.
     */
    private TerminalFactory(TerminalFactorySpi spi, Provider provider, 
            String type) {
        this.spi = spi;
        this.provider = provider;
        this.type = type;
    }

    /**
     * Get the default TerminalFactory type.
     *
     * <p>It is determined as follows:
     *
     * when this class is initialized, the system property
     * <code>javax.smartcardio.TerminalFactory.DefaultType</code>
     * is examined. If it is set, a TerminalFactory of this type is
     * instantiated by calling the {@linkplain #getInstance
     * getInstance(String,Object)} method passing
     * <code>null</code> as the value for <code>params</code>. If the call
     * succeeds, the type becomes the default type and the factory becomes
     * the {@linkplain #getDefault default} factory.
     *
     * <p>If the system property is not set or the getInstance() call fails
     * for any reason, the system defaults to an implementation specific
     * default type and TerminalFactory.
     *
     * @return the default TerminalFactory type
     */
    public static String getDefaultType() {
        return defaultType;
    }

    /**
     * Returns the default TerminalFactory instance. See
     * {@linkplain #getDefaultType} for more information.
     *
     * <p>A default TerminalFactory is always available. However, depending
     * on the implementation, it may not offer any terminals.
     *
     * @return the default TerminalFactory
     * @throws CardException if the card operation failed.
     */
    public static TerminalFactory getDefault() throws CardException {
        constructPCSCService(false);
        if(!m_bPCSCServiceIsStarted)
            throw new CardException("PCSCException: SCARD_E_NO_SERVICE");
        
        return defaultFactory;
    }

    /**
     * Returns a TerminalFactory of the specified type that is initialized
     * with the specified parameters.
     *
     * <p> This method traverses the list of registered security Providers,
     * starting with the most preferred Provider.
     * A new TerminalFactory object encapsulating the
     * TerminalFactorySpi implementation from the first
     * Provider that supports the specified type is returned.
     *
     * <p> Note that the list of registered providers may be retrieved via
     * the {@linkplain Security#getProviders() Security.getProviders()} method.
     *
     * <p>The <code>TerminalFactory</code> is initialized with the
     * specified parameters Object. The type of parameters
     * needed may vary between different types of <code>TerminalFactory</code>s.
     *
     * @param type the type of the requested TerminalFactory
     * @param params the parameters to pass to the TerminalFactorySpi
     *   implementation, or null if no parameters are needed
     * @return a TerminalFactory of the specified type
     *
     * @throws NullPointerException if type is null
     * @throws NoSuchAlgorithmException if no Provider supports a
     *   TerminalFactorySpi of the specified type
     */
    public static TerminalFactory getInstance(String type, Object params)
            throws NoSuchAlgorithmException {

        Instance instance = GetInstance.getInstance("TerminalFactory",
            TerminalFactorySpi.class, type, params);
        return new TerminalFactory((TerminalFactorySpi)instance.impl,
            instance.provider, type);
    }

    /**
     * Returns a TerminalFactory of the specified type that is initialized
     * with the specified parameters.
     *
     * <p> A new TerminalFactory object encapsulating the
     * TerminalFactorySpi implementation from the specified provider
     * is returned.  The specified provider must be registered
     * in the security provider list.
     *
     * <p> Note that the list of registered providers may be retrieved via
     * the {@linkplain Security#getProviders() Security.getProviders()} method.
     *
     * <p>The <code>TerminalFactory</code> is initialized with the
     * specified parameters Object. The type of parameters
     * needed may vary between different types of <code>TerminalFactory</code>s.
     *
     * @param type the type of the requested TerminalFactory
     * @param params the parameters to pass to the TerminalFactorySpi
     *   implementation, or null if no parameters are needed
     * @param provider the name of the provider
     * @return a TerminalFactory of the specified type
     *
     * @throws NullPointerException if type is null
     * @throws IllegalArgumentException if provider is null or the empty String
     * @throws NoSuchAlgorithmException if a TerminalFactorySpi implementation
     *   of the specified type is not available from the specified provider.
     * <br /> Or if no TerminalFactory of the specified type could be found.
     * @throws NoSuchProviderException if the specified provider could not
     *   be found
     */
    public static TerminalFactory getInstance(String type, Object params,
            String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        Instance instance = GetInstance.getInstance("TerminalFactory",
            TerminalFactorySpi.class, type, params, provider);
        return new TerminalFactory((TerminalFactorySpi)instance.impl,
            instance.provider, type);
    }

    /**
     * Returns a TerminalFactory of the specified type that is initialized
     * with the specified parameters.
     *
     * <p> A new TerminalFactory object encapsulating the
     * TerminalFactorySpi implementation from the specified provider object
     * is returned. Note that the specified provider object does not have to be
     * registered in the provider list.
     *
     * <p>The <code>TerminalFactory</code> is initialized with the
     * specified parameters Object. The type of parameters
     * needed may vary between different types of <code>TerminalFactory</code>s.
     *
     * @param type the type of the requested TerminalFactory
     * @param params the parameters to pass to the TerminalFactorySpi
     *   implementation, or null if no parameters are needed
     * @param provider the provider
     * @return a TerminalFactory of the specified type
     *
     * @throws NullPointerException if type is null
     * @throws IllegalArgumentException if provider is null
     * @throws NoSuchAlgorithmException if a TerminalFactorySpi implementation
     *   of the specified type is not available from the specified Provider
     */
    public static TerminalFactory getInstance(String type, Object params,
            Provider provider) throws NoSuchAlgorithmException {
        Service service = GetInstance.getService("TerminalFactory", type, provider);
        /*Instance instance = GetInstance.getInstance("TerminalFactory",
            TerminalFactorySpi.class, type, params, provider);*/
        Instance instance = GetInstance.getInstance(service,
            TerminalFactorySpi.class, params);
        return new TerminalFactory((TerminalFactorySpi)instance.impl,
            instance.provider, type);
    }

    /**
     * Returns the provider of this TerminalFactory.
     *
     * @return the provider of this TerminalFactory.
     */
    public Provider getProvider() {
        return provider;
    }

    /**
     * Returns the type of this TerminalFactory. This is the value that was
     * specified in the getInstance() method that returned this object.
     *
     * @return the type of this TerminalFactory
     */
    public String getType() {
        return type;
    }

    /**
     * Returns a new CardTerminals object encapsulating the terminals
     * supported by this factory.
     * See the class comment of the {@linkplain CardTerminals} class
     * regarding how the returned objects can be shared and reused.
     *
     * @return a new CardTerminals object encapsulating the terminals
     * supported by this factory.
     */
    public CardTerminals terminals() {
        return spi.engineTerminals();
    }

    /**
     * Returns a string representation of this TerminalFactory.
     *
     * @return a string representation of this TerminalFactory.
     */
    public String toString() {
        return "TerminalFactory for type " + type + " from provider "
            + provider.getName();
    }

}
