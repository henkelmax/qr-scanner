package de.maxhenkel.qrscanner.parser.matmsg;

public class Email {

    protected String to, subject, body;

    public Email(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    protected Email() {

    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
