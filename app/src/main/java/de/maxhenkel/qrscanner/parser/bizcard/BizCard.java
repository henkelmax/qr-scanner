package de.maxhenkel.qrscanner.parser.bizcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BizCard {

    protected String firstName;
    protected String lastName;
    protected String title;
    protected String company;
    protected String address;
    protected List<String> telephone;
    protected String email;

    public BizCard(String firstName, String lastName, String title, String company, String address, List<String> telephone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.company = company;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
    }

    protected BizCard() {
        this.telephone = new ArrayList<>();
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Optional<String> getCompany() {
        return Optional.ofNullable(company);
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(address);
    }

    public List<String> getTelephone() {
        return telephone;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }
}
