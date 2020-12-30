package de.maxhenkel.qrscanner.parser.epc;

import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EPCParser {

    public static final Pattern EPC = Pattern.compile("^BCD\\r?\\n\\r?\\d{3}\\r?\\n\\r?\\d+\\r?\\n\\r?[\\s\\S]*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    @Nullable
    public static EPC parse(String s) {
        Matcher matcher = EPC.matcher(s);

        if (!matcher.matches()) {
            return null;
        }

        String[] lines = s.split("\\n");
        EPC epc = new EPC();
        if (lines.length >= 4) {
            epc.identification = lines[3];
        }
        if (lines.length >= 5) {
            epc.bic = lines[4];
        }
        if (lines.length >= 6) {
            epc.name = lines[5];
        }
        if (lines.length >= 7) {
            epc.iban = lines[6];
        }
        if (lines.length >= 8) {
            epc.amount = lines[7];
        }
        if (lines.length >= 9) {
            epc.reference = lines[8];
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 9; i < lines.length; i++) {
            sb.append(lines[i]);
            sb.append("\n");
        }
        String info = sb.toString().trim();
        if (!info.isEmpty()) {
            epc.information = info;
        }
        return epc;
    }

}
