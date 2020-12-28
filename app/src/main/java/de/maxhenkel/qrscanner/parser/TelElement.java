package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;

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

    public String getNumber() {
        return number;
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
    public int getLayout() {
        return R.layout.result_tel;
    }

    @Override
    public int getTitle() {
        return R.string.type_tel;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView textView = activity.findViewById(R.id.tel);
        textView.setText(getNumber());

        Button call = activity.findViewById(R.id.call);
        call.setOnClickListener(v -> {
            open(activity);
        });
    }

}
