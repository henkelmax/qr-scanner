package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.query.Query;

public class PlayStoreElement extends ScanElement {

    public static final Pattern PLAY_STORE = Pattern.compile("^market://([^?]+)(?:\\?(.+))?$", Pattern.CASE_INSENSITIVE);

    private String packageID;

    public PlayStoreElement(ScanResult result, String packageID) {
        super(result);
        this.packageID = packageID;
    }

    public static PlayStoreElement market(ScanResult result, Matcher matcher) {
        Query query = Query.parse(matcher.group(2));
        return new PlayStoreElement(result, query.get("id").orElse(""));
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(result.getData()));
    }

    @Override
    public String getPreview(Context context) {
        return packageID;
    }

    @Override
    public int getTitle() {
        return R.string.type_store;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        addTitleValue(R.string.title_playstore_package, packageID);

        addButton(R.string.open_store).setOnClickListener(v -> {
            open(activity);
        });
    }

}
