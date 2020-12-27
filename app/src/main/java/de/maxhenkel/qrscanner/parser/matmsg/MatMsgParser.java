package de.maxhenkel.qrscanner.parser.matmsg;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.parser.Tokenizer;

public class MatMsgParser {

    public static final Pattern MATMSG = Pattern.compile("^MATMSG:([\\s\\S]+)$", Pattern.CASE_INSENSITIVE);

    @Nullable
    public static Email parse(String s) {
        Matcher matcher = MATMSG.matcher(s);

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

        Email email = new Email();

        for (String element : strings) {
            String[] split = element.split(":", 2);
            if (split.length != 2) {
                continue;
            }

            if (split[0].equalsIgnoreCase("TO")) {
                email.to = split[1];
            } else if (split[0].equalsIgnoreCase("SUB")) {
                email.subject = split[1];
            } else if (split[0].equalsIgnoreCase("BODY")) {
                email.body = split[1];
            }
        }

        if (email.getTo() == null) {
            return null;
        }

        return email;
    }

}
