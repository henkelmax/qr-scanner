package de.maxhenkel.qrscanner.parser.bizcard;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.parser.Tokenizer;

public class BizCardParser {

    public static final Pattern BIZCARD = Pattern.compile("^BIZCARD:([\\s\\S]+)$", Pattern.CASE_INSENSITIVE);

    @Nullable
    public static BizCard parse(String s) {
        Matcher matcher = BIZCARD.matcher(s);

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

        BizCard bizCard = new BizCard();

        for (String element : strings) {
            String[] split = element.split(":", 2);
            if (split.length != 2) {
                continue;
            }

            if (split[0].equalsIgnoreCase("N")) {
                bizCard.firstName = split[1];
            } else if (split[0].equalsIgnoreCase("X")) {
                bizCard.lastName = split[1];
            } else if (split[0].equalsIgnoreCase("T")) {
                bizCard.title = split[1];
            } else if (split[0].equalsIgnoreCase("C")) {
                bizCard.company = split[1];
            } else if (split[0].equalsIgnoreCase("A")) {
                bizCard.address = split[1];
            } else if (split[0].equalsIgnoreCase("B") || split[0].equalsIgnoreCase("M") || split[0].equalsIgnoreCase("F")) {
                bizCard.telephone.add(split[1]);
            } else if (split[0].equalsIgnoreCase("E")) {
                bizCard.email = split[1];
            }
        }

        return bizCard;
    }

}
