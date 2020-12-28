package de.maxhenkel.qrscanner.parser;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.RawActivity;
import de.maxhenkel.qrscanner.ScanResultActivity;

public abstract class ScanElement {

    @Nullable
    protected ScanResultActivity activity;
    protected ScanResult result;
    @Nullable
    protected Button viewRaw;
    @Nullable
    protected Button save;
    @Nullable
    protected TextView title;

    public ScanElement(ScanResult result) {
        this.result = result;
    }

    public String getRawText() {
        return result.getData();
    }

    public int getLayout() {
        return R.layout.result_text;
    }

    public int getTitle() {
        return R.string.type_text;
    }

    public String getPreview(Context context) {
        return result.getData();
    }

    public String getFileName() {
        return "qrcode.txt";
    }

    public String getMimeType() {
        return "text/plain";
    }

    @Nullable
    public Intent getIntent(Context context) {
        return null;
    }

    public boolean open(Context context) {
        Intent intent = getIntent(context);
        if (intent == null) {
            return false;
        }
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            try {
                context.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                showNoActivityFoundToast(context);
                return false;
            }
        } else {
            showNoActivityFoundToast(context);
            return false;
        }
    }

    protected void showNoActivityFoundToast(Context context) {
        Toast.makeText(context, R.string.toast_no_activity_found, Toast.LENGTH_LONG).show();
    }

    public void create(ScanResultActivity activity) {
        this.activity = activity;
        activity.setContentView(getLayout());
        viewRaw = activity.findViewById(R.id.viewRaw);
        save = activity.findViewById(R.id.save);
        title = activity.findViewById(R.id.title);

        if (viewRaw == null || title == null) {
            return;
        }

        title.setText(getTitle());

        viewRaw.setOnClickListener(v -> {
            Intent i = new Intent(activity, RawActivity.class);
            i.putExtra("scanResult", result);
            activity.startActivity(i);
        });
        save.setOnClickListener(v -> {
            activity.save(getFileName(), getMimeType());
        });
    }

}
