package de.maxhenkel.qrscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.maxhenkel.qrscanner.parser.QRCodeParser;
import de.maxhenkel.qrscanner.parser.ScanElement;
import de.maxhenkel.qrscanner.parser.ScanResult;

public class HistoryActivity extends Activity {

    private SimpleDateFormat dateFormat;
    private ListView listView;
    private TextView noHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        noHistory = findViewById(R.id.noHistory);
        dateFormat = new SimpleDateFormat(getString(R.string.date_format));

        new Thread(() -> {
            try {
                List<ScanResult> list = ScanHistory.get(this);
                runOnUiThread(() -> {
                    listView.setAdapter(new HistoryArrayAdapter(this, list));
                    if (listView.getAdapter().isEmpty()) {
                        noHistory.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.toast_load_history_failed, Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        }).start();
    }

    private class HistoryArrayAdapter extends ArrayAdapter<ScanResult> {

        public HistoryArrayAdapter(@NonNull Context context, @NonNull List<ScanResult> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_entry, parent, false);
            }

            TextView title = convertView.findViewById(R.id.title);
            TextView time = convertView.findViewById(R.id.time);
            TextView preview = convertView.findViewById(R.id.preview);
            ScanResult item = getItem(position);
            ScanElement element = QRCodeParser.parse(item);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(item.getTimestamp());
            title.setText(element.getTitle());
            time.setText(dateFormat.format(c.getTime()));
            preview.setText(element.getPreview(getContext()));
            convertView.setOnClickListener(v -> {
                Intent i = new Intent(HistoryActivity.this, ScanResultActivity.class);
                i.putExtra("scanResult", item);
                startActivity(i);
            });

            return convertView;
        }
    }

}