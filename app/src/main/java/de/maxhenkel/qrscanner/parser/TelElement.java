package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.util.Linkify;

import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;

public class TelElement extends ScanElement {

    public static final Pattern TEL = Pattern.compile("^(tel|voicemail):(.+)$", Pattern.CASE_INSENSITIVE);

    private String number;

    public TelElement(ScanResult result, String number) {
        super(result);
        this.number = number;
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
    }

    @Override
    public String getPreview(Context context) {
        return number;
    }

    @Override
    public int getTitle() {
        return R.string.type_tel;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        addValueTextView(number, Linkify.PHONE_NUMBERS);

        addButton(R.string.open_tel).setOnClickListener(v -> {
            open(activity);
        });
    }

}
