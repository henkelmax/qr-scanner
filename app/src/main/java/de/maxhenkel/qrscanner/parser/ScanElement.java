package de.maxhenkel.qrscanner.parser;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
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
            } catch (Exception e) {
                showOpenErrorToast(context);
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

    protected void showOpenErrorToast(Context context) {
        Toast.makeText(context, R.string.toast_open_uri_error, Toast.LENGTH_LONG).show();
    }

    public void create(ScanResultActivity activity) {
        this.activity = activity;
        activity.setContentView(R.layout.activity_result);
        viewRaw = activity.findViewById(R.id.viewRaw);
        save = activity.findViewById(R.id.save);
        title = activity.findViewById(R.id.title);

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

    public TextView addTitleTextView(String title) {
        LinearLayout content = activity.findViewById(R.id.content);
        TextView v = (TextView) LayoutInflater.from(activity).inflate(R.layout.template_title, content, false);
        v.setText(title);
        content.addView(v);
        return v;
    }

    public TextView addTitleTextView(int title) {
        LinearLayout content = activity.findViewById(R.id.content);
        TextView v = (TextView) LayoutInflater.from(activity).inflate(R.layout.template_title, content, false);
        v.setText(title);
        content.addView(v);
        return v;
    }

    public TextView addValueTextView(String text, int linkify) {
        LinearLayout content = activity.findViewById(R.id.content);
        TextView v = (TextView) LayoutInflater.from(activity).inflate(R.layout.template_value, content, false);
        v.setAutoLinkMask(linkify);
        v.setText(text);
        content.addView(v);
        return v;
    }

    public TextView addValueTextView(String text) {
        return addValueTextView(text, 0);
    }

    public void addTitleValue(String title, String text, int linkify) {
        addTitleTextView(title);
        addValueTextView(text, linkify);
    }

    public void addTitleValue(int title, String text, int linkify) {
        addTitleTextView(title);
        addValueTextView(text, linkify);
    }

    public void addTitleValue(String title, String text) {
        addTitleValue(title, text, 0);
    }

    public void addTitleValue(int title, String text) {
        addTitleValue(title, text, 0);
    }

    public TextView addMonospaceValueTextView(String text, int linkify) {
        LinearLayout content = activity.findViewById(R.id.content);
        TextView v = (TextView) LayoutInflater.from(activity).inflate(R.layout.template_value_monospace, content, false);
        v.setAutoLinkMask(linkify);
        v.setText(text);
        content.addView(v);
        return v;
    }

    public TextView addMonospaceValueTextView(String text) {
        return addMonospaceValueTextView(text);
    }

    public Button addButton(String text) {
        LinearLayout content = activity.findViewById(R.id.content);
        LinearLayout l = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.template_button, content, false);
        Button button = (Button) l.getChildAt(0);
        button.setText(text);
        content.addView(l);
        return button;
    }

    public Button addButton(int text) {
        LinearLayout content = activity.findViewById(R.id.content);
        LinearLayout l = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.template_button, content, false);
        Button button = (Button) l.getChildAt(0);
        button.setText(text);
        content.addView(l);
        return button;
    }

}
