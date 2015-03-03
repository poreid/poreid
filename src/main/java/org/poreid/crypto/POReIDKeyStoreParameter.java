package org.poreid.crypto;

import java.security.KeyStore.LoadStoreParameter;
import java.security.KeyStore.ProtectionParameter;
import java.util.Locale;
import org.poreid.POReIDSmartCard;


public final class POReIDKeyStoreParameter implements LoadStoreParameter {
    private Locale locale;
    private POReIDSmartCard card;
    
    
    public POReIDKeyStoreParameter(){
    }
    
    
    @Override
    public ProtectionParameter getProtectionParameter() {
        return null;
    }

    
    public Locale getLocale() {
        return locale;
    }
    
    
    public POReIDKeyStoreParameter setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }
    
    
    public POReIDSmartCard getCard() {
        return card;
    }

    public void setCard(POReIDSmartCard card) {
        this.card = card;
    }
}
