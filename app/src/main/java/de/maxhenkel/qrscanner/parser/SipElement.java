package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;

public class SipElement extends ScanElement {

    public static final Pattern SIP = Pattern.compile("^(?:sip|sips):(.+)$", Pattern.CASE_INSENSITIVE);

    private String address;

    public SipElement(ScanResult result, String address) {
        super(result);
        this.address = address;
    }

    public static SipElement sip(ScanResult result, Matcher matcher) {
        return new SipElement(result, matcher.group(1));
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse(result.getData()));
    }

    @Override
    public String getPreview(Context context) {
        return address;
    }

    @Override
    public int getLayout() {
        return R.layout.result_sip;
    }

    @Override
    public int getTitle() {
        return R.string.type_sip;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView address = activity.findViewById(R.id.address);
        address.setText(this.address);

        Button call = activity.findViewById(R.id.call);
        call.setOnClickListener(v -> {
            open(activity);
        });
    }

}
