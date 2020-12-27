package de.maxhenkel.qrscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import de.maxhenkel.qrscanner.parser.ScanResult;

public class RawActivity extends Activity {

    private TextView rawText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw);

        rawText = findViewById(R.id.rawText);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        ScanResult scanResult = intent.getParcelableExtra("scanResult");
        rawText.setText(scanResult.getText());
    }
}