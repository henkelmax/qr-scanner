package de.maxhenkel.qrscanner.parser;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.zxing.Result;

public class ScanResult implements Parcelable {

    public static final Parcelable.Creator<ScanResult> CREATOR = new Parcelable.Creator<ScanResult>() {
        public ScanResult createFromParcel(Parcel in) {
            byte[] data = new byte[in.readInt()];
            in.readByteArray(data);
            return new ScanResult(data, in.readString());
        }

        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };

    private final byte[] data;
    private final String text;

    public ScanResult(byte[] data, String text) {
        this.data = data;
        this.text = text;
    }

    public ScanResult(Result result) {
        this(result.getRawBytes() == null ? new byte[0] : result.getRawBytes(), result.getText());
    }

    public ScanElement parse() {
        return QRCodeParser.parse(this);
    }

    public byte[] getData() {
        return data;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(data.length);
        dest.writeByteArray(data);
        dest.writeString(text);
    }
}
