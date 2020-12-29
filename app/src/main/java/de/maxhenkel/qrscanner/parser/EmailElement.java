package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.util.Linkify;

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
    public int getTitle() {
        return R.string.type_email;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        addTitleValue(R.string.title_email_recipients, TextUtils.join("\n", email.getTo()), Linkify.EMAIL_ADDRESSES);

        if (email.getCc().length > 0) {
            addTitleValue(R.string.title_email_cc, TextUtils.join("\n", email.getCc()), Linkify.EMAIL_ADDRESSES);
        }

        if (email.getBcc().length > 0) {
            addTitleValue(R.string.title_email_bcc, TextUtils.join("\n", email.getBcc()), Linkify.EMAIL_ADDRESSES);
        }

        email.getSubject().ifPresent(sub -> {
            addTitleValue(R.string.title_email_subject, sub);
        });

        email.getBody().ifPresent(body -> {
            addTitleValue(R.string.title_email_body, body);
        });

        addButton(R.string.open_email).setOnClickListener(v -> {
            open(activity);
        });
    }

}
