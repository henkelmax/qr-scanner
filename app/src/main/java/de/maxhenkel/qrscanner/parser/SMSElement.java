package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.util.Linkify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.query.Query;

public class SMSElement extends ScanElement {

    public static final Pattern SMS_RAW = Pattern.compile("^(?:smsto|mmsto|sms|mms):([^:?]+)(?::([\\s\\S]*))?$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public static final Pattern SMS = Pattern.compile("^(?:smsto|mmsto|sms|mms):([^:?]+)(?:\\?(.*))?$", Pattern.CASE_INSENSITIVE);

    private String[] numbers;
    private String body;
    private String action;

    public SMSElement(ScanResult result, String[] numbers, String body, String action) {
        super(result);
        this.numbers = numbers;
        this.body = body;
        this.action = action;
    }

    public static SMSElement smsRaw(ScanResult result, Matcher matcher) {
        String number = matcher.group(1);
        String[] numbers = Tokenizer.splitTrim(number, ",");

        Query query = new Query();
        query.add("body", matcher.group(2));
        StringBuilder sb = new StringBuilder("sms:");
        sb.append(number);
        sb.append(query.build());

        return new SMSElement(result, numbers, query.get("body").orElse(""), sb.toString());
    }

    public static SMSElement sms(ScanResult result, Matcher matcher) {
        String number = matcher.group(1);
        String[] numbers = Tokenizer.splitTrim(number, ",");
        Query query = Query.parse(matcher.group(2));
        return new SMSElement(result, numbers, query.get("body").orElse(""), result.getData());
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_SENDTO, Uri.parse(action));
    }

    @Override
    public String getPreview(Context context) {
        return TextUtils.join(", ", numbers);
    }

    @Override
    public int getTitle() {
        return R.string.type_sms;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        addTitleValue(R.string.title_numbers, TextUtils.join("\n", numbers), Linkify.PHONE_NUMBERS);
        addTitleValue(R.string.title_sms_text, body);

        addButton(R.string.open_sms).setOnClickListener(v -> {
            open(activity);
        });
    }

}
