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

import org.poreid.pcscforjava.Card;
import org.poreid.pcscforjava.CardException;
import org.poreid.pcscforjava.CardTerminal;
import org.poreid.pcscforjava.CardTerminals;
import org.poreid.pcscforjava.TerminalFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.poreid.common.Util;
import org.poreid.config.POReIDConfig;
import org.poreid.dialogs.pindialogs.usepinpad.UsePinPadDialogController;
import org.poreid.dialogs.pindialogs.verifypin.VerifyPinDialogController;
import org.poreid.dialogs.selectcard.CanceledSelectionException;
import org.poreid.dialogs.selectcard.SelectCardDialogController;

/**
 * Responsável pela instanciação da classe relativa ao tipo de cartão.
 * @author POReID
 */
public final class CardFactory {  
    private static final Locale defaultLocale;    
   
    
    static {        
        defaultLocale = POReIDConfig.getDefaultLocale();
    }

    /**
     * Obter um cartão e utilizar as parametrizações (linguagem e utilização da cache) definidas no ficheiro de configuração.
     * @param <T> Uma classe que implemente a interface POReIDSmartCard
     * @return cartão suportado pelo poreid
     * @throws CardTerminalNotPresentException Exceção lançada quando não existe um leitor de cartões no sistema
     * @throws UnknownCardException Exceção lançada quando o cartão não é reconhecido
     * @throws CardNotPresentException Exceção lançada quando não existe um cartão no leitor
     * @throws CanceledSelectionException Exceção lançada quando o utilizador não selecionou um de entre os vários cartões que foram detetados
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    public static <T extends POReIDSmartCard> T getCard() throws CardTerminalNotPresentException, UnknownCardException, CardNotPresentException, CanceledSelectionException, POReIDException{
        return getCard(defaultLocale, CacheStatus.UNSET, Proxy.NO_PROXY);
    }
    
    /**
     * Obter um cartão e utilizar as parametrizações (linguagem e utilização da cache) definidas no ficheiro de configuração.
     * @param <T> Uma classe que implemente a interface POReIDSmartCard
     * @param proxy Permite indicar um proxy
     * @return cartão suportado pelo poreid
     * @throws CardTerminalNotPresentException Exceção lançada quando não existe um leitor de cartões no sistema
     * @throws UnknownCardException Exceção lançada quando o cartão não é reconhecido
     * @throws CardNotPresentException Exceção lançada quando não existe um cartão no leitor
     * @throws CanceledSelectionException Exceção lançada quando o utilizador não selecionou um de entre os vários cartões que foram detetados
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    public static <T extends POReIDSmartCard> T getCard(Proxy proxy) throws CardTerminalNotPresentException, UnknownCardException, CardNotPresentException, CanceledSelectionException, POReIDException{
        return getCard(defaultLocale, CacheStatus.UNSET, proxy);
    }

    /**
     * Obter um cartão, redefinir o comportamento da cache (ligada / desligada) e utilizar a parametrização relativa à linguagem definida no ficheiro de configuração.
     * @param <T> Uma classe que implemente a interface POReIDSmartCard
     * @param cachePreferences Permite indicar se a cache deve ser utilizada
     * @return cartão suportado pelo poreid
     * @throws CardTerminalNotPresentException Exceção lançada quando não existe um leitor de cartões no sistema
     * @throws UnknownCardException Exceção lançada quando o cartão não é reconhecido
     * @throws CardNotPresentException Exceção lançada quando não existe um cartão no leitor
     * @throws CanceledSelectionException Exceção lançada quando o utilizador não selecionou um de entre os vários cartões que foram detetados
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    public static <T extends POReIDSmartCard> T getCard(boolean cachePreferences) throws CardTerminalNotPresentException, UnknownCardException, CardNotPresentException, CanceledSelectionException, POReIDException{
        return getCard(defaultLocale, CacheStatus.getStatus(cachePreferences), Proxy.NO_PROXY);
    }
    
    /**
     * Obter um cartão, redefinir o comportamento da cache (ligada / desligada) e utilizar a parametrização relativa à linguagem definida no ficheiro de configuração.
     * @param <T> Uma classe que implemente a interface POReIDSmartCard
     * @param cachePreferences Permite indicar se a cache deve ser utilizada
     * @param proxy Permite indicar um proxy
     * @return cartão suportado pelo poreid
     * @throws CardTerminalNotPresentException Exceção lançada quando não existe um leitor de cartões no sistema
     * @throws UnknownCardException Exceção lançada quando o cartão não é reconhecido
     * @throws CardNotPresentException Exceção lançada quando não existe um cartão no leitor
     * @throws CanceledSelectionException Exceção lançada quando o utilizador não selecionou um de entre os vários cartões que foram detetados
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    public static <T extends POReIDSmartCard> T getCard(boolean cachePreferences, Proxy proxy) throws CardTerminalNotPresentException, UnknownCardException, CardNotPresentException, CanceledSelectionException, POReIDException{
        return getCard(defaultLocale, CacheStatus.getStatus(cachePreferences), proxy);
    }

    /**
     * Obter um cartão, redefinir a linguagem e utilizar a parametrização relativa ao estado da cache definida no ficheiro de configuração.
     * @param <T> Uma classe que implemente a interface POReIDSmartCard
     * @param locale Permite escolher a linguagem utilizada (português/inglês)
     * @return cartão suportado pelo poreid
     * @throws CardTerminalNotPresentException Exceção lançada quando não existe um leitor de cartões no sistema
     * @throws UnknownCardException Exceção lançada quando o cartão não é reconhecido
     * @throws CardNotPresentException Exceção lançada quando não existe um cartão no leitor
     * @throws CanceledSelectionException Exceção lançada quando o utilizador não selecionou um de entre os vários cartões que foram detetados
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    public static <T extends POReIDSmartCard> T getCard(Locale locale) throws CardTerminalNotPresentException, UnknownCardException, CardNotPresentException, CanceledSelectionException, POReIDException{
        if (null == locale){
            locale = defaultLocale;
        }
        
        return getCard(locale, CacheStatus.UNSET, Proxy.NO_PROXY);
    }
    
    /**
     * Obter um cartão, redefinir a linguagem e utilizar a parametrização relativa ao estado da cache definida no ficheiro de configuração.
     * @param <T> Uma classe que implemente a interface POReIDSmartCard
     * @param locale Permite escolher a linguagem utilizada (português/inglês)
     * @param proxy Permite indicar um proxy
     * @return cartão suportado pelo poreid
     * @throws CardTerminalNotPresentException Exceção lançada quando não existe um leitor de cartões no sistema
     * @throws UnknownCardException Exceção lançada quando o cartão não é reconhecido
     * @throws CardNotPresentException Exceção lançada quando não existe um cartão no leitor
     * @throws CanceledSelectionException Exceção lançada quando o utilizador não selecionou um de entre os vários cartões que foram detetados
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    public static <T extends POReIDSmartCard> T getCard(Locale locale, Proxy proxy) throws CardTerminalNotPresentException, UnknownCardException, CardNotPresentException, CanceledSelectionException, POReIDException{
        if (null == locale){
            locale = defaultLocale;
        }
        
        return getCard(locale, CacheStatus.UNSET, proxy);
    }
    
    
    /**
     * Obter um cartão, redefinir o comportamento da cache e da linguagem.
     * @param <T> Uma classe que implemente a interface POReIDSmartCard
     * @param locale Permite escolher a linguagem utilizada (português/inglês)
     * @param cachePreferences Permite indicar se a cache deve ser utilizada
     * @return cartão suportado pelo poreid
     * @throws CardTerminalNotPresentException Exceção lançada quando não existe um leitor de cartões no sistema
     * @throws UnknownCardException Exceção lançada quando o cartão não é reconhecido
     * @throws CardNotPresentException Exceção lançada quando não existe um cartão no leitor
     * @throws CanceledSelectionException Exceção lançada quando o utilizador não selecionou um de entre os vários cartões que foram detetados
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    public static <T extends POReIDSmartCard> T getCard(Locale locale, CacheStatus cachePreferences) throws CardTerminalNotPresentException, UnknownCardException, CardNotPresentException, CanceledSelectionException, POReIDException{
        return getCard(locale, cachePreferences, Proxy.NO_PROXY);
    }

    /**
     * Obter um cartão, redefinir o comportamento da cache e da linguagem.
     * @param <T> Uma classe que implemente a interface POReIDSmartCard
     * @param locale Permite escolher a linguagem utilizada (português/inglês)
     * @param cachePreferences Permite indicar se a cache deve ser utilizada
     * @param proxy Permite indicar um proxy
     * @return cartão suportado pelo poreid
     * @throws CardTerminalNotPresentException Exceção lançada quando não existe um leitor de cartões no sistema
     * @throws UnknownCardException Exceção lançada quando o cartão não é reconhecido
     * @throws CardNotPresentException Exceção lançada quando não existe um cartão no leitor
     * @throws CanceledSelectionException Exceção lançada quando o utilizador não selecionou um de entre os vários cartões que foram detetados
     * @throws POReIDException Exceção lançada quando ocorre uma exceção num componente (encapsula a exeção original)
     */
    public static <T extends POReIDSmartCard> T getCard(Locale locale, CacheStatus cachePreferences, Proxy proxy) throws CardTerminalNotPresentException, UnknownCardException, CardNotPresentException, CanceledSelectionException, POReIDException{
        TerminalFactory factory;        
        Iterator<CardTerminal> iterator;
        List<CardTerminal> terminals;
        List<T> cardList = new ArrayList<>();
        boolean unknownCard = false;
        T t;
        
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            throw new POReIDException("Não deve utilizar a Event Dispatch Thread (EDT) para executar lógica da aplicação");
        }
                       
        try {
            factory = TerminalFactory.getDefault();
            if (!factory.terminals().isValidContext()) {
                factory.releaseContext();
                factory = TerminalFactory.getDefault();
            }
        } catch (CardException ex){
            throw new CardTerminalNotPresentException("Não foi encontrado um leitor de cartões",ex);
        }
        
        try {                        
            if (factory.terminals().list().isEmpty()) {
                factory.releaseContext();
                throw new CardTerminalNotPresentException("Não foi encontrado um leitor de cartões");
            }
            terminals = factory.terminals().list(CardTerminals.State.CARD_PRESENT);
        } catch (CardException | NullPointerException ex) {
            factory.releaseContext();
            throw new CardTerminalNotPresentException("Não foi possível obter lista de leitores", ex);
        }     
        
        iterator = terminals.iterator();
        while (iterator.hasNext()) {
            try {      
                t = knownATR(iterator.next(),locale, cachePreferences, proxy);
                cardList.add(t);
            } catch (UnknownCardException ex) {
                unknownCard = true;
            } catch (CardException ignored) { /* trata-se no switch */ }
        }

        switch (cardList.size()) {
            case 0:
                if (unknownCard) {
                    throw new UnknownCardException("Cartão não suportado");
                }
                throw new CardNotPresentException("Verifique se o cartão está no leitor");
            case 1:
                    return cardList.get(0);
            default:
                    return SelectCardDialogController.getInstance(cardList, locale).selectCard(new Date());   
        }        
    }
    
    
    @SuppressWarnings("unchecked")
    private static <T extends POReIDSmartCard> T knownATR(CardTerminal terminal, Locale locale, CacheStatus status, Proxy proxy) throws CardException, UnknownCardException{
        Card card = terminal.connect("*");
        String className = POReIDConfig.getSmartCardImplementingClassName(Util.bytesToHex(card.getATR().getBytes()));
        boolean cachePreferences = POReIDConfig.getSmartCardCacheStatus(Util.bytesToHex(card.getATR().getBytes()));

        if (!CacheStatus.isUnset(status)){
            cachePreferences = CacheStatus.getStatus(status);
        }

        if (null != className){
            try {
                Constructor<? extends POReIDSmartCard> ctor = Class.forName(className).asSubclass(POReIDSmartCard.class).getDeclaredConstructor(Card.class, CardTerminal.class, Locale.class, boolean.class, Proxy.class, Date.class);
                return (T) ctor.newInstance(card, terminal, locale, cachePreferences, proxy, new Date());
            } catch (InvocationTargetException | IllegalArgumentException | SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                Logger.getLogger(CardFactory.class.getName()).log(Level.SEVERE, null, ex);
                throw new UnknownCardException("Cartão não suportado", ex);
            }
        }

        throw new UnknownCardException("Cartão não suportado");
    }
    
    /**
     * Possibilita a exibição de uma mensagem nas janelas de diálogo de pedido de PIN - Típicamente informação de contexto
     * @param infoMessage - Mensagem a ser exibida, possivel utilizar tags html strong e em
     */
    public static void setPinDialogsInfoMessage(String infoMessage){
        UsePinPadDialogController.setInfoMessage(infoMessage);
        VerifyPinDialogController.setInfoMessage(infoMessage);
    }
    
    /**
     * Remove a mensagem préviamente passada
     */
    public static void removePinDialogsInfoMessage(){        
        UsePinPadDialogController.removeInfoMessage();
        VerifyPinDialogController.removeInfoMessage();
    }    
    
    
    /**
     * Permite redefinir a utilização da cache face à predefinição existente.
     */
    public enum CacheStatus {

        /**
         * Utilizar e criar cache
         */
        ENABLED,

        /**
         * Não utilizar e criar cache
         */
        DISABLED,

        /**
         * Utilizar a parametrização existente no ficheiro de configuração
         */
        UNSET;
        
        static CacheStatus getStatus(boolean status) {
            return (status) ? ENABLED : DISABLED;
        }
        
        static boolean getStatus(CacheStatus status){
            return (ENABLED == status);
        }
        
        static boolean isUnset(CacheStatus status){
            return (UNSET == status);
        }
    };
}