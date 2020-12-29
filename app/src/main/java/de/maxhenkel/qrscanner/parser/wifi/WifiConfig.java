package de.maxhenkel.qrscanner.parser.wifi;

import java.util.Optional;

public class WifiConfig {

    protected String authenticationType;
    protected String ssid;
    protected String password;
    protected boolean hidden;
    protected String eapMethod;
    protected String anonymousIdentity;
    protected String identity;
    protected String pshase2Method;

    public WifiConfig(String authenticationType, String ssid, String password, boolean hidden, String eapMethod, String anonymousIdentity, String identity, String pshase2Method) {
        this.authenticationType = authenticationType;
        this.ssid = ssid;
        this.password = password;
        this.hidden = hidden;
        this.eapMethod = eapMethod;
        this.anonymousIdentity = anonymousIdentity;
        this.identity = identity;
        this.pshase2Method = pshase2Method;
    }

    protected WifiConfig() {

    }

    public Optional<String> getAuthenticationType() {
        return Optional.ofNullable(authenticationType);
    }

    public Optional<String> getSsid() {
        return Optional.ofNullable(ssid);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public boolean isHidden() {
        return hidden;
    }

    public Optional<String> getEapMethod() {
        return Optional.ofNullable(eapMethod);
    }

    public Optional<String> getAnonymousIdentity() {
        return Optional.ofNullable(anonymousIdentity);
    }

    public Optional<String> getIdentity() {
        return Optional.ofNullable(identity);
    }

    public Optional<String> getPshase2Method() {
        return Optional.ofNullable(pshase2Method);
    }
}
