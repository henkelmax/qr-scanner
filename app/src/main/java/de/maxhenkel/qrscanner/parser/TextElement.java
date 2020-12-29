package de.maxhenkel.qrscanner.parser;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;

public class TextElement extends ScanElement {

    public TextElement(ScanResult result) {
        super(result);
    }

    @Override
    public int getTitle() {
        return R.string.type_text;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        addMonospaceValueTextView(result.getData(), 0);
    }
}
