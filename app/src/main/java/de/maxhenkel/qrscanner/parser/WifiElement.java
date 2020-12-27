package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
                .setSsid(wifiConfig.getSsid())
                .setIsHiddenSsid(wifiConfig.isHidden());

        if (wifiConfig.getAuthenticationType() != null) {
            String auth = wifiConfig.getAuthenticationType().toUpperCase();

            if (auth.equals("WPA") || auth.equals("WPA2") || auth.equals("WPA-2")) {
                if (wifiConfig.getPassword() != null) {
                    suggestion.setWpa2Passphrase(wifiConfig.getPassword());
                }
            } else if (auth.equals("WPA3") || auth.equals("WPA-3")) {
                if (wifiConfig.getPassword() != null) {
                    suggestion.setWpa3Passphrase(wifiConfig.getPassword());
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
        if (wifiConfig.getAnonymousIdentity() != null) {
            enterpriseConfig.setAnonymousIdentity(wifiConfig.getAnonymousIdentity());
        }
        if (wifiConfig.getIdentity() != null) {
            enterpriseConfig.setIdentity(wifiConfig.getIdentity());
        }
        if (wifiConfig.getPassword() != null) {
            enterpriseConfig.setPassword(wifiConfig.getPassword());
        }
        if (wifiConfig.getPshase2Method() != null) {
            enterpriseConfig.setPhase2Method(getPhase2Method(wifiConfig.getPshase2Method()));
        }
        if (wifiConfig.getEapMethod() != null) {
            enterpriseConfig.setEapMethod(getEapMethod(wifiConfig.getEapMethod()));
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
    public int getLayout() {
        return R.layout.result_wifi;
    }

    @Override
    public int getTitle() {
        return R.string.type_wifi;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView auth = activity.findViewById(R.id.auth);
        if (wifiConfig.getAuthenticationType() != null) {
            auth.setText(wifiConfig.getAuthenticationType());
        }

        TextView ssid = activity.findViewById(R.id.ssid);
        if (wifiConfig.getSsid() != null) {
            ssid.setText(wifiConfig.getSsid());
        }

        TextView password = activity.findViewById(R.id.password);
        if (wifiConfig.getPassword() != null) {
            password.setText(wifiConfig.getPassword());
        }

        TextView other = activity.findViewById(R.id.other);
        StringBuilder sb = new StringBuilder();

        if (wifiConfig.isHidden()) {
            sb.append("<b>Hidden: </b>");
            sb.append(wifiConfig.isHidden());
            sb.append("<br/>");
        }
        if (wifiConfig.getEapMethod() != null) {
            sb.append("<b>EAP: </b>");
            sb.append(wifiConfig.getEapMethod());
            sb.append("<br/>");
        }
        if (wifiConfig.getIdentity() != null) {
            sb.append("<b>Identity: </b>");
            sb.append(wifiConfig.getIdentity());
            sb.append("<br/>");
        }
        if (wifiConfig.getAnonymousIdentity() != null) {
            sb.append("<b>Anonymous Identity: </b>");
            sb.append(wifiConfig.getAnonymousIdentity());
            sb.append("<br/>");
        }
        if (wifiConfig.getEapMethod() != null) {
            sb.append("<b>EAP Method: </b>");
            sb.append(wifiConfig.getEapMethod());
            sb.append("<br/>");
        }
        if (wifiConfig.getPshase2Method() != null) {
            sb.append("<b>Phase 2 Method: </b>");
            sb.append(wifiConfig.getPshase2Method());
            sb.append("<br/>");
        }

        String o = sb.toString();
        if (!o.isEmpty()) {
            other.setText(Html.fromHtml(o, Html.FROM_HTML_MODE_COMPACT));
        } else {
            TextView otherTitle = activity.findViewById(R.id.titleOther);
            other.setVisibility(View.GONE);
            otherTitle.setVisibility(View.GONE);
        }

        Button saveWifi = activity.findViewById(R.id.saveWifi);
        saveWifi.setOnClickListener(v -> {
            open(activity);
        });
    }

}
