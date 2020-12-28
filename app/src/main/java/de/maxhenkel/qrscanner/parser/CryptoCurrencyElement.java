package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.query.Query;

public class CryptoCurrencyElement extends ScanElement {

    public static final Pattern CRYPTO = Pattern.compile("^(bitcoin|bitcoincash|ethereum|litecoin|dash):([^?]+)(?:\\?(.*))?$", Pattern.CASE_INSENSITIVE);

    private String type;
    private String address;
    private String amount;
    private String label;
    private String message;

    public CryptoCurrencyElement(ScanResult result, String type, String address, String amount, String label, String message) {
        super(result);
        this.type = type;
        this.address = address;
        this.amount = amount;
        this.label = label;
        this.message = message;
    }

    public static CryptoCurrencyElement crypto(ScanResult result, Matcher matcher) {
        String type = matcher.group(1);
        String address = matcher.group(2);
        Query query = Query.parse(matcher.group(3));
        return new CryptoCurrencyElement(result, type, address, query.get("amount").orElse(""), query.get("label").orElse(""), query.get("message").orElse(""));
    }

    public String getType() {
        switch (type) {
            default:
                return "";
            case "bitcoin":
                return "Bitcoin";
            case "bitcoincash":
                return "Bitcoin Cash";
            case "ethereum":
                return "Ethereum";
            case "litecoin":
                return "Litecoin";
            case "dash":
                return "Dash";
        }
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(result.getText()));
    }

    @Override
    public int getLayout() {
        return R.layout.result_crypto_currency;
    }

    @Override
    public int getTitle() {
        return R.string.type_crypto_currency;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView currency = activity.findViewById(R.id.currency);
        currency.setText(getType());

        TextView address = activity.findViewById(R.id.address);
        address.setText(this.address);

        TextView amount = activity.findViewById(R.id.amount);
        amount.setText(String.valueOf(this.amount));

        TextView label = activity.findViewById(R.id.label);
        label.setText(this.label);

        TextView message = activity.findViewById(R.id.message);
        message.setText(this.message);

        Button open = activity.findViewById(R.id.open);
        open.setOnClickListener(v -> {
            open(activity);
        });
    }

}
