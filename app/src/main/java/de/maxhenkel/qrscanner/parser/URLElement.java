package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;

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

    public String getURL() {
        return url;
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    @Override
    public int getLayout() {
        return R.layout.result_url;
    }

    @Override
    public int getTitle() {
        return R.string.type_url;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView textView = activity.findViewById(R.id.url);
        textView.setText(getURL());

        Button openLink = activity.findViewById(R.id.openLink);
        openLink.setOnClickListener(v -> {
            open(activity);
        });
    }

}
