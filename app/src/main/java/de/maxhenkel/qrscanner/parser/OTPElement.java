package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.query.Query;

public class OTPElement extends ScanElement {

    public static final Pattern OTPAUTH = Pattern.compile("^otpauth://([^?/]+)/?([^?]+)?(\\?.*)?$", Pattern.CASE_INSENSITIVE);

    private String type;
    private String label;
    private String secret;
    private String issuer;
    private String algorithm;
    private String digits;
    private String period;
    private String initialCounter;

    public OTPElement(ScanResult result, String type, String label, String secret, String issuer, String algorithm, String digits, String period, String initialCounter) {
        super(result);
        this.type = type;
        this.label = label;
        this.secret = secret;
        this.issuer = issuer;
        this.algorithm = algorithm;
        this.digits = digits;
        this.period = period;
        this.initialCounter = initialCounter;
    }

    public static OTPElement otpauth(ScanResult result, Matcher matcher) {
        String type = matcher.group(1);
        String l = matcher.group(2);
        String label = "";
        if (l != null) {
            label = Query.decode(l, true);
        }
        Query query = Query.parse(matcher.group(3));
        return new OTPElement(result, type, label, query.get("secret").orElse(""), query.get("issuer").orElse(""), query.get("algorithm").orElse(""), query.get("digits").orElse("6"), query.get("period").orElse(type.equalsIgnoreCase("totp") ? "30" : ""), query.get("counter").orElse(""));
    }

    public String getType() {
        return type.toUpperCase();
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(result.getData()));
    }

    @Override
    public String getPreview(Context context) {
        return label;
    }

    @Override
    public int getTitle() {
        return R.string.type_otp;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        addTitleValue(R.string.title_otp_type, getType());

        if (!label.isEmpty()) {
            addTitleValue(R.string.title_otp_label, label);
        }

        addTitleValue(R.string.title_otp_secret, secret);

        if (!issuer.isEmpty()) {
            addTitleValue(R.string.title_otp_issuer, issuer);
        }

        if (!algorithm.isEmpty()) {
            addTitleValue(R.string.title_otp_algorithm, algorithm);
        }

        if (!digits.isEmpty()) {
            addTitleValue(R.string.title_otp_digits, digits);
        }

        if (!period.isEmpty()) {
            addTitleValue(R.string.title_otp_period, period);
        }

        if (!initialCounter.isEmpty()) {
            addTitleValue(R.string.title_otp_initial_counter, initialCounter);
        }

        addButton(R.string.open_otp).setOnClickListener(v -> {
            open(activity);
        });
    }

}
