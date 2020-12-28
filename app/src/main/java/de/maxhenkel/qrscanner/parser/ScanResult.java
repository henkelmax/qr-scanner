package de.maxhenkel.qrscanner.parser;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanResult implements Parcelable {

    public static final Parcelable.Creator<ScanResult> CREATOR = new Parcelable.Creator<ScanResult>() {
        public ScanResult createFromParcel(Parcel in) {
            return new ScanResult(in.readLong(), in.readString());
        }

        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };

    private final long timestamp;
    private final String data;

    public ScanResult(long timestamp, String data) {
        this.timestamp = timestamp;
        this.data = data;
    }

    public ScanElement parse() {
        return QRCodeParser.parse(this);
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeString(data);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("timestamp", timestamp);
        o.put("data", data);
        return o;
    }

    public static ScanResult fromJSON(JSONObject o) throws JSONException {
        return new ScanResult(o.getLong("timestamp"), o.getString("data"));
    }
}
