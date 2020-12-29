package de.maxhenkel.qrscanner.parser;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokenizer {

    // https://rosettacode.org/wiki/Tokenize_a_string_with_escaping#Java
    @Nullable
    public static List<String> tokenizeString(String s, char sep, char escape) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        boolean inEscape = false;
        for (char c : s.toCharArray()) {
            if (inEscape) {
                inEscape = false;
            } else if (c == escape) {
                inEscape = true;
                continue;
            } else if (c == sep) {
                tokens.add(sb.toString());
                sb.setLength(0);
                continue;
            }
            sb.append(c);
        }
        if (inEscape) {
            return null;
        }

        tokens.add(sb.toString());

        return tokens;
    }

    public static String[] splitTrim(String str, String split) {
        return Arrays.stream(str.split(split)).map(String::trim).toArray(String[]::new);
    }

}
