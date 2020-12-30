package de.maxhenkel.qrscanner.parser.spd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SPD {

    protected static final SimpleDateFormat DUE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    protected Account account;
    protected List<Account> alternativeAccounts;
    protected String amount;
    protected String currency;
    protected String reference;
    protected String recipientName;
    protected String dueDate;
    protected String paymentType;
    protected String message;
    protected String checksum;

    public SPD(Account account, List<Account> alternativeAccounts, String amount, String currency, String reference, String recipientName, String dueDate, String paymentType, String message, String checksum) {
        this.account = account;
        this.alternativeAccounts = alternativeAccounts;
        this.amount = amount;
        this.currency = currency;
        this.reference = reference;
        this.recipientName = recipientName;
        this.dueDate = dueDate;
        this.paymentType = paymentType;
        this.message = message;
        this.checksum = checksum;
    }

    protected SPD() {
        alternativeAccounts = new ArrayList<>();
    }

    public Optional<Account> getAccount() {
        return Optional.ofNullable(account);
    }

    public List<Account> getAlternativeAccounts() {
        return alternativeAccounts;
    }

    public Optional<String> getAmount() {
        return Optional.ofNullable(amount);
    }

    public Optional<String> getCurrency() {
        return Optional.ofNullable(currency);
    }

    public Optional<String> getReference() {
        return Optional.ofNullable(reference);
    }

    public Optional<String> getRecipientName() {
        return Optional.ofNullable(recipientName);
    }

    public Optional<String> getDue() {
        return Optional.ofNullable(dueDate);
    }

    public Optional<Date> getDueDate() {
        if (dueDate == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(DUE_DATE_FORMAT.parse(dueDate));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    public Optional<String> getPaymentType() {
        return Optional.ofNullable(paymentType);
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    public Optional<String> getChecksum() {
        return Optional.ofNullable(checksum);
    }

    public static class Account {
        protected String iban, bic;

        public Account(String iban, String bic) {
            this.iban = iban;
            this.bic = bic;
        }

        public Account() {

        }

        public Optional<String> getIban() {
            return Optional.ofNullable(iban);
        }

        public Optional<String> getBic() {
            return Optional.ofNullable(bic);
        }
    }
}
