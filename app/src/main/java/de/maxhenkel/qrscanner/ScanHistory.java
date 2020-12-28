package de.maxhenkel.qrscanner;

import android.content.Context;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.maxhenkel.qrscanner.parser.ScanResult;

public class ScanHistory {

    public static void add(Context context, ScanResult scan) throws IOException, JSONException {
        List<ScanResult> list = get(context);
        list.add(0, scan);
        while (list.size() > 64) {
            list.remove(list.size() - 1);
        }
        set(context, list);
    }

    public static List<ScanResult> get(Context context) throws IOException, JSONException {
        File file = getFile(context);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        JSONArray array = new JSONArray(IOUtils.toString(reader));
        reader.close();
        List<ScanResult> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(ScanResult.fromJSON(array.getJSONObject(i)));
        }
        list.sort((o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
        return list;
    }

    public static void set(Context context, List<ScanResult> list) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (ScanResult scan : list) {
            array.put(scan.toJSON());
        }
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(getFile(context)), StandardCharsets.UTF_8);
        IOUtils.write(array.toString(), writer);
        writer.close();
    }

    private static File getFile(Context context) throws FileNotFoundException {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) {
            throw new FileNotFoundException("ExternalFilesDir not found.");
        }
        return new File(dir, "history.json");
    }

}
