package de.maxhenkel.qrscanner.parser;

import android.widget.TextView;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;

public class TextElement extends ScanElement {

    public TextElement(ScanResult result) {
        super(result);
    }

    public String getText() {
        return result.getText();
    }

    @Override
    public int getLayout() {
        return R.layout.result_text;
    }

    @Override
    public int getTitle() {
        return R.string.type_text;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView textView = activity.findViewById(R.id.text);
        textView.setText(getText());
    }
}
