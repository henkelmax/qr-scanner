package de.maxhenkel.qrscanner.parser.wifi;

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

    public String getAuthenticationType() {
        return authenticationType;
    }

    public String getSsid() {
        return ssid;
    }

    public String getPassword() {
        return password;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getEapMethod() {
        return eapMethod;
    }

    public String getAnonymousIdentity() {
        return anonymousIdentity;
    }

    public String getIdentity() {
        return identity;
    }

    public String getPshase2Method() {
        return pshase2Method;
    }
}
