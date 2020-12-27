package de.maxhenkel.qrscanner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import de.maxhenkel.qrscanner.parser.ScanElement;
import de.maxhenkel.qrscanner.parser.ScanResult;

public class ScanResultActivity extends Activity {

    private static final int REQ_SAVE_FILE = 1;

    private ScanResult scanResult;
    private ScanElement element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        scanResult = intent.getParcelableExtra("scanResult");

        if (scanResult == null) {
            finish();
            return;
        }

        element = scanResult.parse();
        element.create(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SAVE_FILE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri uri = data.getData();
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
                if (pfd != null) {
                    FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                    fileOutputStream.write(scanResult.getText().getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.close();
                    pfd.close();
                }
                Toast.makeText(this, R.string.toast_save_success, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.toast_save_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void save(String filename, String mimetype) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimetype);
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        startActivityForResult(intent, REQ_SAVE_FILE);
    }
}