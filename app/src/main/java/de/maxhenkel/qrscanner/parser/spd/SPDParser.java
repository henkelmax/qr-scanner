package de.maxhenkel.qrscanner.parser.spd;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.parser.query.Query;
import de.maxhenkel.qrscanner.parser.spd.SPD.Account;

import de.maxhenkel.qrscanner.parser.Tokenizer;

public class SPDParser {

    public static final Pattern SPD = Pattern.compile("^SPD\\*([^\\*]+)\\*(.*)$", Pattern.CASE_INSENSITIVE);
    public static final Pattern ACCOUNT = Pattern.compile("^([^+]+)(?:\\+(.+))?$", Pattern.CASE_INSENSITIVE);

    @Nullable
    public static SPD parse(String s) {
        Matcher matcher = SPD.matcher(s);

        if (!matcher.matches()) {
            return null;
        }

        String str = matcher.group(2);

        if (str == null) {
            return null;
        }
        List<String> strings = Tokenizer.tokenizeString(str, '*', '\\');

        if (strings == null) {
            return null;
        }

        SPD spd = new SPD();

        for (String element : strings) {
            String[] split = element.split(":", 2);
            if (split.length != 2) {
                continue;
            }

            String decoded = Query.decode(split[1], false);
            if (split[0].equalsIgnoreCase("ACC")) {
                spd.account = parseAccount(decoded);
            } else if (split[0].equalsIgnoreCase("ALT-ACC")) {
                String[] accs = decoded.split(",");
                for (String acc : accs) {
                    spd.alternativeAccounts.add(parseAccount(acc));
                }
            } else if (split[0].equalsIgnoreCase("AM")) {
                spd.amount = decoded;
            } else if (split[0].equalsIgnoreCase("CC")) {
                spd.currency = decoded;
            } else if (split[0].equalsIgnoreCase("RF")) {
                spd.reference = decoded;
            } else if (split[0].equalsIgnoreCase("RN")) {
                spd.recipientName = decoded;
            } else if (split[0].equalsIgnoreCase("DT")) {
                spd.dueDate = decoded;
            } else if (split[0].equalsIgnoreCase("PT")) {
                spd.paymentType = decoded;
            } else if (split[0].equalsIgnoreCase("MSG")) {
                spd.message = decoded;
            } else if (split[0].equalsIgnoreCase("CRC32")) {
                spd.checksum = decoded;
            }
        }

        return spd;
    }

    private static Account parseAccount(String str) {
        Account account = new Account();
        Matcher acc = ACCOUNT.matcher(str);
        if (acc.matches()) {
            account.iban = acc.group(1);
            String bic = acc.group(2);
            if (bic != null) {
                account.bic = bic;
            }
        }
        return account;
    }


}
