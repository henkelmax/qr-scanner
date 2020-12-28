package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.matmsg.Email;
import de.maxhenkel.qrscanner.parser.query.Query;

public class EmailElement extends ScanElement {

    public static final Pattern EMAIL = Pattern.compile("^((([!#$%&'*+\\-/=?^_`{|}~\\w])|([!#$%&'*+\\-/=?^_`{|}~\\w][!#$%&'*+\\-/=?^_`{|}~\\.\\w]{0,}[!#$%&'*+\\-/=?^_`{|}~\\w]))[@]\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)$");
    public static final Pattern MAILTO = Pattern.compile("^mailto:([^?]+)(?:\\?(.+))?$", Pattern.CASE_INSENSITIVE);

    private String[] emails;
    private String[] cc;
    private String[] bcc;
    private String subject;
    private String body;
    private String mailto;

    public EmailElement(ScanResult result, String[] emails, String[] cc, String[] bcc, String subject, String body, String mailto) {
        super(result);
        this.emails = emails;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.mailto = mailto;
    }

    public static EmailElement matmsg(ScanResult result, Email email) {
        StringBuilder sb = new StringBuilder();
        sb.append("mailto:");
        sb.append(email.getTo());

        Query query = new Query();
        query.add("subject", email.getSubject());
        query.add("body", email.getBody());

        sb.append(query.build());
        return new EmailElement(result, new String[]{email.getTo()}, new String[0], new String[0], email.getSubject() == null ? "" : email.getSubject(), email.getBody() == null ? "" : email.getBody(), sb.toString());
    }

    public static EmailElement email(ScanResult result, Matcher matcher) {
        return new EmailElement(result, new String[]{result.getText()}, new String[0], new String[0], "", "", "mailto:" + result.getText());
    }

    public static EmailElement mailto(ScanResult result, Matcher matcher) {
        String email = matcher.group(1);
        String[] emails = email.split(",");

        Query query = Query.parse(matcher.group(2));

        String body = query.get("body").orElse("");
        String subject = query.get("subject").orElse("");
        String cc = query.get("cc").orElse("");
        String bcc = query.get("bcc").orElse("");

        return new EmailElement(result, emails, cc.split(","), bcc.split(","), subject, body, result.getText());
    }

    public String[] getEmails() {
        return emails;
    }

    public String[] getCc() {
        return cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getMailto() {
        return mailto;
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_SENDTO, Uri.parse(mailto));
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
        emails.setText(Arrays.stream(getEmails()).collect(Collectors.joining(", ")));

        TextView cc = activity.findViewById(R.id.cc);
        if (getCc().length > 0) {
            cc.setText(Arrays.stream(getCc()).collect(Collectors.joining(", ")));
        } else {
            TextView titleCc = activity.findViewById(R.id.titleCc);
            titleCc.setVisibility(View.GONE);
            cc.setVisibility(View.GONE);
        }

        TextView bcc = activity.findViewById(R.id.bcc);
        if (getBcc().length > 0) {
            bcc.setText(Arrays.stream(getBcc()).collect(Collectors.joining(", ")));
        } else {
            TextView titleBcc = activity.findViewById(R.id.titleBcc);
            titleBcc.setVisibility(View.GONE);
            bcc.setVisibility(View.GONE);
        }

        TextView subject = activity.findViewById(R.id.subject);
        subject.setText(getSubject());

        TextView body = activity.findViewById(R.id.body);
        body.setText(getBody());

        Button send = activity.findViewById(R.id.sendEmail);
        send.setOnClickListener(v -> {
            open(activity);
        });
    }

}
