package de.maxhenkel.qrscanner.parser.epc;

import java.util.Optional;

public class EPC {

    protected String identification;
    protected String bic;
    protected String name;
    protected String iban;
    protected String amount;
    protected String reference;
    protected String information;

    public EPC(String identification, String bic, String name, String iban, String amount, String reference, String information) {
        this.identification = identification;
        this.bic = bic;
        this.name = name;
        this.iban = iban;
        this.amount = amount;
        this.reference = reference;
        this.information = information;
    }

    protected EPC() {

    }

    public Optional<String> getIdentification() {
        return Optional.ofNullable(identification);
    }

    public Optional<String> getBic() {
        return Optional.ofNullable(bic);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getIban() {
        return Optional.ofNullable(iban);
    }

    public Optional<String> getAmount() {
        return Optional.ofNullable(amount);
    }

    public Optional<String> getReference() {
        return Optional.ofNullable(reference);
    }

    public Optional<String> getInformation() {
        return Optional.ofNullable(information);
    }
}
