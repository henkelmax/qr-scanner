package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.util.Linkify;

import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;

public class URIElement extends ScanElement {

    public static final Pattern URI = Pattern.compile("^[a-zA-Z0-9\\+\\-\\.]+:.+$", Pattern.CASE_INSENSITIVE);

    public URIElement(ScanResult result) {
        super(result);
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(result.getData()));
    }

    @Override
    public String getPreview(Context context) {
        return result.getData();
    }

    @Override
    public int getTitle() {
        return R.string.type_uri;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        addValueTextView(result.getData(), Linkify.WEB_URLS);

        addButton(R.string.open_uri).setOnClickListener(v -> {
            open(activity);
        });
    }

}
