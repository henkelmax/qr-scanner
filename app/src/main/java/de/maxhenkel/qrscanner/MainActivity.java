package de.maxhenkel.qrscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import de.maxhenkel.qrscanner.parser.ScanResult;

public class MainActivity extends Activity implements DecoratedBarcodeView.TorchListener {

    private DecoratedBarcodeView scannerView;
    private CaptureManager captureManager;
    private ImageButton flash;
    private View flashLayout;
    private View history;
    private boolean torch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        scannerView = findViewById(R.id.scanner);
        flash = findViewById(R.id.flash);
        flashLayout = findViewById(R.id.flashLayout);
        history = findViewById(R.id.history);
        scannerView.decodeContinuous(result -> {
            vibrator.vibrate(50L);

            ScanResult scanResult = new ScanResult(result.getTimestamp(), result.getText());
            new Thread(() -> {
                try {
                    ScanHistory.add(getApplicationContext(), scanResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            Intent i = new Intent(this, ScanResultActivity.class);
            i.putExtra("scanResult", scanResult);
            startActivity(i);
        });
        scannerView.setTorchListener(this);

        CameraSettings settings = new CameraSettings();
        settings.setFocusMode(CameraSettings.FocusMode.CONTINUOUS);
        scannerView.setCameraSettings(settings);

        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            flashLayout.setVisibility(View.GONE);
        }

        flashLayout.setOnClickListener(v -> {
            if (torch) {
                scannerView.setTorchOff();
            } else {
                scannerView.setTorchOn();
            }
        });

        history.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });

        captureManager = new CaptureManager(this, scannerView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.setShowMissingCameraPermissionDialog(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        captureManager.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return scannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        captureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onTorchOn() {
        torch = true;
        flash.setImageResource(R.drawable.flash);
    }

    @Override
    public void onTorchOff() {
        torch = false;
        flash.setImageResource(R.drawable.flash_off);
    }
}