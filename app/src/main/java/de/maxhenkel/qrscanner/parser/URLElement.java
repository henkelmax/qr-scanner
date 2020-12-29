package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.util.Linkify;

import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;

public class URLElement extends ScanElement {

    public static final Pattern URL = Pattern.compile("^(https?|ftp)://.+$", Pattern.CASE_INSENSITIVE);
    public static final Pattern URLTO = Pattern.compile("^urlto:(((https?|ftp)://)?.*)$", Pattern.CASE_INSENSITIVE);

    private String url;

    public URLElement(ScanResult result, String url) {
        super(result);
        this.url = url;
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    @Override
    public String getPreview(Context context) {
        return url;
    }

    @Override
    public int getTitle() {
        return R.string.type_url;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        addValueTextView(url, Linkify.WEB_URLS);

        addButton(R.string.open_link).setOnClickListener(v -> {
            open(activity);
        });
    }

}
