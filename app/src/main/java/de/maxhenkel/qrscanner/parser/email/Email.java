package de.maxhenkel.qrscanner.parser.email;

import java.util.Optional;

public class Email {

    protected String[] to, cc, bcc;
    protected String subject, body;

    public Email(String[] to, String[] cc, String[] bcc, String subject, String body) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
    }

    protected Email() {
        to = new String[0];
        cc = new String[0];
        bcc = new String[0];
    }

    public String[] getTo() {
        return to;
    }

    public String[] getCc() {
        return cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public Optional<String> getSubject() {
        return Optional.ofNullable(subject);
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }
}
