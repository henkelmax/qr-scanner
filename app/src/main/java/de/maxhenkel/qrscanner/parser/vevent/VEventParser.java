package de.maxhenkel.qrscanner.parser.vevent;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.parser.Tokenizer;

public class VEventParser {

    public static final Pattern VEVENT = Pattern.compile("^(\\s*BEGIN:VEVENT\\s*([\\S\\s]*)\\s*END:VEVENT\\s*)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    @Nullable
    public static VEvent parse(String s) {
        Matcher matcher = VEVENT.matcher(s);

        if (!matcher.matches()) {
            return null;
        }

        String str = matcher.group(1);

        if (str == null) {
            return null;
        }
        List<String> strings = Tokenizer.tokenizeString(str, '\n', '\\');

        if (strings == null) {
            return null;
        }

        VEvent vevent = new VEvent();

        for (String element : strings) {
            String[] split = element.trim().split(":", 2);
            if (split.length != 2) {
                continue;
            }

            if (split[0].equalsIgnoreCase("SUMMARY")) {
                vevent.summary = split[1];
            } else if (split[0].equalsIgnoreCase("LOCATION")) {
                vevent.location = split[1];
            } else if (split[0].equalsIgnoreCase("DTSTART")) {
                vevent.start = split[1];
            } else if (split[0].equalsIgnoreCase("DTEND")) {
                vevent.end = split[1];
            } else if (split[0].equalsIgnoreCase("URL")) {
                vevent.url = split[1];
            }
        }

        return vevent;
    }

}
