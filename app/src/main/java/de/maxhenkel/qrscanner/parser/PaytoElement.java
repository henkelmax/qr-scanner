package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.query.Query;

public class PaytoElement extends ScanElement {

    public static final Pattern PAYTO = Pattern.compile("^payto:\\/((?:\\/[^?\\/]+)+)\\/??(\\?.*)?$", Pattern.CASE_INSENSITIVE);

    private String target;
    private String amount;
    private String currency;
    private String creditorName;
    private String debitorName;
    private String message;
    private String instruction;

    public PaytoElement(ScanResult result, String target, String amount, String currency, String creditorName, String debitorName, String message, String instruction) {
        super(result);
        this.target = target;
        this.amount = amount;
        this.currency = currency;
        this.creditorName = creditorName;
        this.debitorName = debitorName;
        this.message = message;
        this.instruction = instruction;
    }

    public static PaytoElement payto(ScanResult result, Matcher matcher) {
        String t = matcher.group(1);
        String target = "";
        if (t != null) {
            String[] targets = t.split("\\/");
            target = TextUtils.join(" ", targets).trim();
        }
        Query query = Query.parse(matcher.group(2));

        String currency = "";
        String amount = "";
        String a = query.getValue("amount");
        if (a != null) {
            String[] split = a.split(":", 2);
            if (split.length == 2) {
                currency = split[0];
                amount = split[1];
            }
        }

        return new PaytoElement(result, target, amount, currency, query.get("creditor-name").orElse(""), query.get("debitor-name").orElse(""), query.get("message").orElse(""), query.get("instruction").orElse(""));
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(result.getData()));
    }

    @Override
    public String getPreview(Context context) {
        return target;
    }

    @Override
    public int getTitle() {
        return R.string.type_payment;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        if (!target.isEmpty()) {
            addTitleValue(R.string.title_payment_target, target);
        }

        if (!amount.isEmpty()) {
            addTitleValue(R.string.title_payment_amount, amount);
        }

        if (!currency.isEmpty()) {
            addTitleValue(R.string.title_payment_currency, currency);
        }

        if (!creditorName.isEmpty()) {
            addTitleValue(R.string.title_payment_creditor_name, creditorName);
        }

        if (!debitorName.isEmpty()) {
            addTitleValue(R.string.title_payment_debitor_name, debitorName);
        }

        if (!message.isEmpty()) {
            addTitleValue(R.string.title_payment_message, message);
        }

        if (!instruction.isEmpty()) {
            addTitleValue(R.string.title_payment_instruction, instruction);
        }

        addButton(R.string.open_payment).setOnClickListener(v -> {
            open(activity);
        });
    }

}
