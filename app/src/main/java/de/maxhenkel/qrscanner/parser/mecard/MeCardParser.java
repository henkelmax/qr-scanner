package de.maxhenkel.qrscanner.parser.mecard;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.parser.Tokenizer;

public class MeCardParser {

    public static final Pattern MECARD = Pattern.compile("^MECARD:([\\s\\S]+)$", Pattern.CASE_INSENSITIVE);

    @Nullable
    public static MeCard parse(String s) {
        Matcher matcher = MECARD.matcher(s);

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

        MeCard meCard = new MeCard();

        for (String element : strings) {
            String[] split = element.split(":", 2);
            if (split.length != 2) {
                continue;
            }

            if (split[0].equalsIgnoreCase("ADR")) {
                meCard.address = split[1];
            } else if (split[0].equalsIgnoreCase("BDAY")) {
                meCard.birthday = split[1];
            } else if (split[0].equalsIgnoreCase("EMAIL")) {
                meCard.email = split[1];
            } else if (split[0].equalsIgnoreCase("N")) {
                String[] names = Tokenizer.splitTrim(split[1], ",");
                if (names.length >= 2) {
                    meCard.lastName = names[0];
                    meCard.firstName = names[1];
                } else {
                    meCard.firstName = split[1];
                }
            } else if (split[0].equalsIgnoreCase("NICKNAME")) {
                meCard.nickname = split[1];
            } else if (split[0].equalsIgnoreCase("NOTE")) {
                meCard.note = split[1];
            } else if (split[0].equalsIgnoreCase("TEL")) {
                meCard.telephone.add(split[1]);
            } else if (split[0].equalsIgnoreCase("TEL-AV")) {
                meCard.videoPhone.add(split[1]);
            } else if (split[0].equalsIgnoreCase("URL")) {
                meCard.url = split[1];
            } else if (split[0].equalsIgnoreCase("ORG")) {
                meCard.org = split[1];
            }
        }

        return meCard;
    }

}
