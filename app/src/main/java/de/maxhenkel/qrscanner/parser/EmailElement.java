package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.email.Email;
import de.maxhenkel.qrscanner.parser.query.Query;

public class EmailElement extends ScanElement {

    public static final Pattern EMAIL = Pattern.compile("^((([!#$%&'*+\\-/=?^_`{|}~\\w])|([!#$%&'*+\\-/=?^_`{|}~\\w][!#$%&'*+\\-/=?^_`{|}~\\.\\w]{0,}[!#$%&'*+\\-/=?^_`{|}~\\w]))[@]\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)$");
    public static final Pattern MAILTO = Pattern.compile("^mailto:([^?]+)(?:\\?(.+))?$", Pattern.CASE_INSENSITIVE);

    private Email email;
    private String mailto;

    public EmailElement(ScanResult result, Email email, String mailto) {
        super(result);
        this.email = email;
        this.mailto = mailto;
    }

    public static EmailElement email(ScanResult result, Email email) {
        StringBuilder sb = new StringBuilder();
        sb.append("mailto:");
        sb.append(TextUtils.join(",", email.getTo()));

        Query query = new Query();
        query.add("cc", TextUtils.join(",", email.getCc()));
        query.add("bcc", TextUtils.join(",", email.getBcc()));
        query.add("subject", email.getSubject());
        query.add("body", email.getBody());

        sb.append(query.build());
        return new EmailElement(result, email, sb.toString());
    }

    public static EmailElement plainEmail(ScanResult result, Matcher matcher) {
        return new EmailElement(result, new Email(new String[]{result.getData()}, new String[0], new String[0], null, null), "mailto:" + result.getData());
    }

    public static EmailElement mailto(ScanResult result, Matcher matcher) {
        String email = matcher.group(1);
        String[] emails = Tokenizer.splitTrim(email, ",");

        Query query = Query.parse(matcher.group(2));

        String body = query.get("body").orElse(null);
        String subject = query.get("subject").orElse(null);
        String[] cc = Tokenizer.splitTrim(query.get("cc").orElse(""), ",");
        String[] bcc = Tokenizer.splitTrim(query.get("bcc").orElse(""), ",");

        return new EmailElement(result, new Email(emails, cc, bcc, subject, body), result.getData());
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_SENDTO, Uri.parse(mailto));
    }

    @Override
    public String getPreview(Context context) {
        return TextUtils.join(", ", email.getTo());
    }

    @Override
    public int getLayout() {
        return R.layout.result_email;
    }

    @Override
    public int getTitle() {
        return R.string.type_email;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView emails = activity.findViewById(R.id.emails);
        emails.setText(TextUtils.join("\n", email.getTo()));

        TextView cc = activity.findViewById(R.id.cc);
        if (email.getCc().length > 0) {
            cc.setText(TextUtils.join("\n", email.getCc()));
        } else {
            TextView titleCc = activity.findViewById(R.id.titleCc);
            titleCc.setVisibility(View.GONE);
            cc.setVisibility(View.GONE);
        }

        TextView bcc = activity.findViewById(R.id.bcc);
        if (email.getBcc().length > 0) {
            bcc.setText(TextUtils.join("\n", email.getBcc()));
        } else {
            TextView titleBcc = activity.findViewById(R.id.titleBcc);
            titleBcc.setVisibility(View.GONE);
            bcc.setVisibility(View.GONE);
        }

        TextView subject = activity.findViewById(R.id.subject);
        subject.setText(email.getSubject().orElse(""));

        TextView body = activity.findViewById(R.id.body);
        body.setText(email.getBody().orElse(""));

        Button send = activity.findViewById(R.id.sendEmail);
        send.setOnClickListener(v -> {
            open(activity);
        });
    }

}
