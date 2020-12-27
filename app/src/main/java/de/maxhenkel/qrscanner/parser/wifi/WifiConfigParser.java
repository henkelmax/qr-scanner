package de.maxhenkel.qrscanner.parser.wifi;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.parser.Tokenizer;

public class WifiConfigParser {

    public static final Pattern WIFI = Pattern.compile("^wifi:([\\s\\S]+)$", Pattern.CASE_INSENSITIVE);

    @Nullable
    public static WifiConfig parse(String s) {
        Matcher matcher = WIFI.matcher(s);

        if (!matcher.matches()) {
            return null;
        }

        String str = matcher.group(1);

        if (str == null) {
            return null;
        }
        List<String> strings = Tokenizer.tokenizeString(str, ';', '\\');

        if (strings == null) {
            return null;
        }

        WifiConfig config = new WifiConfig();

        for (String element : strings) {
            String[] split = element.split(":", 2);
            if (split.length != 2) {
                continue;
            }

            if (split[0].equalsIgnoreCase("T")) {
                config.authenticationType = split[1];
            } else if (split[0].equalsIgnoreCase("S")) {
                config.ssid = split[1];
            } else if (split[0].equalsIgnoreCase("P")) {
                config.password = split[1];
            } else if (split[0].equalsIgnoreCase("H")) {
                config.hidden = Boolean.parseBoolean(split[1]);
            } else if (split[0].equalsIgnoreCase("E")) {
                config.eapMethod = split[1];
            } else if (split[0].equalsIgnoreCase("A")) {
                config.anonymousIdentity = split[1];
            } else if (split[0].equalsIgnoreCase("I")) {
                config.identity = split[1];
            } else if (split[0].equalsIgnoreCase("PH2")) {
                config.pshase2Method = split[1];
            }
        }

        return config;
    }

}
