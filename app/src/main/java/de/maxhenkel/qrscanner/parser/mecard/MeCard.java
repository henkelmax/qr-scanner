package de.maxhenkel.qrscanner.parser.mecard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MeCard {

    protected static final SimpleDateFormat BIRTHDAY_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    protected String address;
    protected String birthday;
    protected String email;
    protected String firstName;
    protected String lastName;
    protected String nickname;
    protected String note;
    protected List<String> telephone;
    protected List<String> videoPhone;
    protected String url;
    protected String org;

    public MeCard(String address, String birthday, String email, String firstName, String lastName, String nickname, String note, List<String> telephone, List<String> videoPhone, String url, String org) {
        this.address = address;
        this.birthday = birthday;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.note = note;
        this.telephone = telephone;
        this.videoPhone = videoPhone;
        this.url = url;
        this.org = org;
    }

    protected MeCard() {
        this.telephone = new ArrayList<>();
        this.videoPhone = new ArrayList<>();
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(address);
    }

    public Optional<String> getBirthday() {
        return Optional.ofNullable(birthday);
    }

    public Optional<Date> getBirthdayDate() {
        if (birthday == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(BIRTHDAY_DATE_FORMAT.parse(birthday));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<String> getNickname() {
        return Optional.ofNullable(nickname);
    }

    public Optional<String> getNote() {
        return Optional.ofNullable(note);
    }

    public List<String> getTelephone() {
        return telephone;
    }

    public List<String> getVideoPhone() {
        return videoPhone;
    }

    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }

    public Optional<String> getOrg() {
        return Optional.ofNullable(org);
    }
}
