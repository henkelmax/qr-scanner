package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.widget.Toast;

import java.util.Collections;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.wifi.WifiConfig;

public class WifiElement extends ScanElement {

    public static final Pattern WIFI = Pattern.compile("^wifi:.+$", Pattern.CASE_INSENSITIVE);
    private static final String[] PHASE_2_METHODS = {"NULL", "PAP", "MSCHAP", "MSCHAPV2", "GTC", "SIM", "AKA", "AKA'"};
    private static final String[] EAP_METHODS = {"PEAP", "TLS", "TTLS", "PWD", "SIM", "AKA", "AKA'", "WFA-UNAUTH-TLS"};

    private WifiConfig wifiConfig;

    public WifiElement(ScanResult result, WifiConfig wifiConfig) {
        super(result);
        this.wifiConfig = wifiConfig;
    }

    @Override
    public boolean open(Context context) {
        WifiNetworkSuggestion.Builder suggestion = new WifiNetworkSuggestion.Builder()
                .setSsid(wifiConfig.getSsid().orElse(""))
                .setIsHiddenSsid(wifiConfig.isHidden());

        if (wifiConfig.getAuthenticationType().isPresent()) {
            String auth = wifiConfig.getAuthenticationType().get().toUpperCase();

            if (auth.equals("WPA") || auth.equals("WPA2") || auth.equals("WPA-2")) {
                if (wifiConfig.getPassword().isPresent()) {
                    suggestion.setWpa2Passphrase(wifiConfig.getPassword().get());
                }
            } else if (auth.equals("WPA3") || auth.equals("WPA-3")) {
                if (wifiConfig.getPassword().isPresent()) {
                    suggestion.setWpa3Passphrase(wifiConfig.getPassword().get());
                }
            } else if (auth.equals("WPA2-EAP")) {
                suggestion.setWpa2EnterpriseConfig(getEnterpriseConfig(wifiConfig));
            } else if (auth.equals("WPA3-EAP")) {
                suggestion.setWpa3EnterpriseConfig(getEnterpriseConfig(wifiConfig));
            }
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int status = wifiManager.addNetworkSuggestions(Collections.singletonList(suggestion.build()));
        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            Toast.makeText(context, R.string.toast_wifi_config_success, Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(context, R.string.toast_wifi_config_failed, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private WifiEnterpriseConfig getEnterpriseConfig(WifiConfig wifiConfig) {
        WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
        if (wifiConfig.getAnonymousIdentity().isPresent()) {
            enterpriseConfig.setAnonymousIdentity(wifiConfig.getAnonymousIdentity().get());
        }
        if (wifiConfig.getIdentity().isPresent()) {
            enterpriseConfig.setIdentity(wifiConfig.getIdentity().get());
        }
        if (wifiConfig.getPassword().isPresent()) {
            enterpriseConfig.setPassword(wifiConfig.getPassword().get());
        }
        if (wifiConfig.getPshase2Method().isPresent()) {
            enterpriseConfig.setPhase2Method(getPhase2Method(wifiConfig.getPshase2Method().get()));
        }
        if (wifiConfig.getEapMethod().isPresent()) {
            enterpriseConfig.setEapMethod(getEapMethod(wifiConfig.getEapMethod().get()));
        }
        return enterpriseConfig;
    }

    private int getPhase2Method(String str) {
        for (int i = 0; i < PHASE_2_METHODS.length; i++) {
            if (str.equalsIgnoreCase(PHASE_2_METHODS[i])) {
                return i;
            }
        }
        return 0;
    }

    private int getEapMethod(String str) {
        for (int i = 0; i < EAP_METHODS.length; i++) {
            if (str.equalsIgnoreCase(EAP_METHODS[i])) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public String getPreview(Context context) {
        return wifiConfig.getSsid().orElse("");
    }

    @Override
    public int getTitle() {
        return R.string.type_wifi;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        if (wifiConfig.getAuthenticationType().isPresent()) {
            addTitleValue(R.string.title_wifi_auth_type, wifiConfig.getAuthenticationType().get());
        }

        addTitleValue(R.string.title_wifi_ssid, wifiConfig.getSsid().orElse(""));

        if (wifiConfig.getPassword().isPresent()) {
            addTitleValue(R.string.title_wifi_password, wifiConfig.getPassword().get());
        }

        if (wifiConfig.isHidden()) {
            addTitleValue(R.string.title_wifi_hidden, wifiConfig.getPassword().get());
        }

        if (wifiConfig.getEapMethod().isPresent()) {
            addTitleValue(R.string.title_wifi_eap_method, wifiConfig.getEapMethod().get());
        }

        if (wifiConfig.getIdentity().isPresent()) {
            addTitleValue(R.string.title_wifi_identity, wifiConfig.getIdentity().get());
        }

        if (wifiConfig.getAnonymousIdentity().isPresent()) {
            addTitleValue(R.string.title_wifi_anonymous_identity, wifiConfig.getAnonymousIdentity().get());
        }

        if (wifiConfig.getPshase2Method().isPresent()) {
            addTitleValue(R.string.title_wifi_phase_2_method, wifiConfig.getPshase2Method().get());
        }

        addButton(R.string.open_wifi).setOnClickListener(v -> {
            open(activity);
        });
    }

}
