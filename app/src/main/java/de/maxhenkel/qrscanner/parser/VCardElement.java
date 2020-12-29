package de.maxhenkel.qrscanner.parser;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.util.Linkify;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import ezvcard.VCard;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.parameter.VCardParameter;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.Email;
import ezvcard.property.Note;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import ezvcard.property.Url;

public class VCardElement extends ScanElement {

    public static final Pattern VCARD = Pattern.compile("^(\\s*BEGIN:VCARD\\s*([\\S\\s]*)\\s*END:VCARD\\s*)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private VCard card;

    public VCardElement(ScanResult result, VCard card) {
        super(result);
        this.card = card;
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        if (card.getFormattedName() != null) {
            sb.append(card.getFormattedName().getValue());
        } else if (card.getStructuredName() != null) {
            StructuredName name = card.getStructuredName();
            for (String s : name.getPrefixes()) {
                sb.append(s);
                sb.append(" ");
            }
            if (name.getGiven() != null) {
                sb.append(name.getGiven());
                sb.append(" ");
            }
            for (String s : name.getAdditionalNames()) {
                sb.append(s);
                sb.append(" ");
            }
            if (name.getFamily() != null) {
                sb.append(name.getFamily());
                sb.append(" ");
            }
            for (String s : name.getSuffixes()) {
                sb.append(s);
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    public static String getPhoneNumber(Telephone telephone) {
        if (telephone.getUri() != null) {
            return telephone.getUri().getNumber();
        } else {
            return telephone.getText();
        }
    }

    public static String getAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        if (address.getStreetAddress() != null) {
            sb.append(address.getStreetAddress());
            sb.append(", ");
        }
        if (address.getPostalCode() != null) {
            sb.append(address.getPostalCode());
            sb.append(" ");
        }
        if (address.getLocality() != null) {
            sb.append(address.getLocality());
            sb.append(", ");
        }
        if (address.getRegion() != null) {
            sb.append(address.getRegion());
            sb.append(" ");
        }
        if (address.getCountry() != null) {
            sb.append(address.getCountry());
        }
        return sb.toString();
    }

    @Override
    public Intent getIntent(Context context) {
        Intent insertIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        insertIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        insertIntent.putExtra(ContactsContract.Intents.Insert.NAME, getName());

        if (card.getOrganization() != null) {
            for (String org : card.getOrganization().getValues()) {
                insertIntent.putExtra(ContactsContract.Intents.Insert.COMPANY, org);
            }
        }

        for (Note note : card.getNotes()) {
            insertIntent.putExtra(ContactsContract.Intents.Insert.NOTES, note.getValue());
        }

        ArrayList<ContentValues> contactData = new ArrayList<>();

        for (Telephone tel : card.getTelephoneNumbers()) {
            ContentValues phoneRow = new ContentValues();
            phoneRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            phoneRow.put(ContactsContract.CommonDataKinds.Phone.NUMBER, getPhoneNumber(tel));
            phoneRow.put(ContactsContract.CommonDataKinds.Phone.TYPE, getPhoneType(tel));
            contactData.add(phoneRow);
        }

        for (Email email : card.getEmails()) {
            ContentValues emailRow = new ContentValues();
            emailRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
            emailRow.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email.getValue());
            emailRow.put(ContactsContract.CommonDataKinds.Email.TYPE, getEmailType(email));
            contactData.add(emailRow);
        }

        for (Address address : card.getAddresses()) {
            ContentValues emailRow = new ContentValues();
            emailRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
            emailRow.put(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, address.getCountry());
            emailRow.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, address.getLocality());
            emailRow.put(ContactsContract.CommonDataKinds.StructuredPostal.POBOX, address.getPoBox());
            emailRow.put(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, address.getPostalCode());
            emailRow.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address.getStreetAddress());
            emailRow.put(ContactsContract.CommonDataKinds.StructuredPostal.REGION, address.getRegion());
            emailRow.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, getAddressType(address));
            contactData.add(emailRow);
        }

        for (Birthday birthday : card.getBirthdays()) {
            ContentValues birthdayRow = new ContentValues();
            birthdayRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
            birthdayRow.put(ContactsContract.Data.CONTENT_TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
            birthdayRow.put(ContactsContract.CommonDataKinds.Event.START_DATE, new SimpleDateFormat(activity.getString(R.string.birthday_date_format)).format(birthday.getCalendar().getTime()));
            contactData.add(birthdayRow);
        }

        for (Url url : card.getUrls()) {
            ContentValues urlRow = new ContentValues();
            urlRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
            urlRow.put(ContactsContract.Data.CONTENT_TYPE, getUrlType(url));
            urlRow.put(ContactsContract.CommonDataKinds.Website.URL, url.getValue());
            contactData.add(urlRow);
        }

        insertIntent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, contactData);
        return insertIntent;
    }

    public static int getPhoneType(Telephone telephone) {
        if (telephone.getTypes().size() <= 0) {
            return ContactsContract.CommonDataKinds.Phone.TYPE_MAIN;
        }
        if (hasPhoneType(telephone, TelephoneType.CELL)) {
            return ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        } else if (hasPhoneType(telephone, TelephoneType.FAX)) {
            if (hasPhoneType(telephone, TelephoneType.HOME)) {
                return ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME;
            } else if (hasPhoneType(telephone, TelephoneType.WORK)) {
                return ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK;
            } else {
                return ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX;
            }
        } else if (hasPhoneType(telephone, TelephoneType.CAR)) {
            return ContactsContract.CommonDataKinds.Phone.TYPE_CAR;
        } else if (hasPhoneType(telephone, TelephoneType.ISDN)) {
            return ContactsContract.CommonDataKinds.Phone.TYPE_ISDN;
        } else if (hasPhoneType(telephone, TelephoneType.MODEM)) {
            return ContactsContract.CommonDataKinds.Phone.TYPE_OTHER;
        } else if (hasPhoneType(telephone, TelephoneType.HOME)) {
            return ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
        } else if (hasPhoneType(telephone, TelephoneType.WORK)) {
            if (hasPhoneType(telephone, TelephoneType.CELL)) {
                return ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE;
            } else if (hasPhoneType(telephone, TelephoneType.PAGER)) {
                return ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER;
            } else {
                return ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
            }
        } else if (hasPhoneType(telephone, TelephoneType.MSG) || hasPhoneType(telephone, TelephoneType.TEXTPHONE) || hasPhoneType(telephone, TelephoneType.TEXT)) {
            return ContactsContract.CommonDataKinds.Phone.TYPE_MMS;
        } else if (hasPhoneType(telephone, TelephoneType.PAGER)) {
            return ContactsContract.CommonDataKinds.Phone.TYPE_PAGER;
        } else {
            return ContactsContract.CommonDataKinds.Phone.TYPE_OTHER;
        }
    }

    private static boolean hasPhoneType(Telephone telephone, TelephoneType type) {
        return telephone.getTypes().stream().anyMatch(type::equals);
    }

    public static int getEmailType(Email mail) {
        if (mail.getTypes().size() <= 0) {
            return ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
        }

        if (hasEmailType(mail, EmailType.HOME) || hasEmailType(mail, EmailType.INTERNET)) {
            return ContactsContract.CommonDataKinds.Email.TYPE_HOME;
        } else if (hasEmailType(mail, EmailType.WORK)) {
            return ContactsContract.CommonDataKinds.Email.TYPE_WORK;
        } else {
            return ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
        }
    }

    private static boolean hasEmailType(Email email, EmailType type) {
        return email.getTypes().stream().anyMatch(type::equals);
    }

    public static int getAddressType(Address address) {
        if (address.getTypes().size() <= 0) {
            return ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER;
        }

        if (hasAddressType(address, AddressType.HOME)) {
            return ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME;
        } else if (hasAddressType(address, AddressType.WORK)) {
            return ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK;
        } else {
            return ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER;
        }
    }

    private static boolean hasAddressType(Address address, AddressType type) {
        return address.getTypes().stream().anyMatch(type::equals);
    }

    public static int getUrlType(Url url) {
        if (url.getType() == null) {
            return ContactsContract.CommonDataKinds.Website.TYPE_OTHER;
        }

        if (url.getType().toLowerCase().contains("home")) {
            return ContactsContract.CommonDataKinds.Website.TYPE_HOME;
        } else if (url.getType().toLowerCase().contains("work")) {
            return ContactsContract.CommonDataKinds.Website.TYPE_WORK;
        } else {
            return ContactsContract.CommonDataKinds.Website.TYPE_HOMEPAGE;
        }
    }

    @Override
    public String getPreview(Context context) {
        return getName();
    }

    @Override
    public int getTitle() {
        return R.string.type_contact;
    }

    @Override
    public String getMimeType() {
        return "text/vcard";
    }

    @Override
    public String getFileName() {
        return "contact.vcf";
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        String name = getName();
        if (!name.isEmpty()) {
            addTitleValue(R.string.title_contact_name, name);
        }

        if (card.getOrganization() != null) {
            addTitleValue(R.string.title_contact_organization, TextUtils.join(" ", card.getOrganization().getValues()));
        }

        if (!card.getTelephoneNumbers().isEmpty()) {
            addTitleValue(R.string.title_contact_telephone_numbers, card
                    .getTelephoneNumbers()
                    .stream()
                    .map(telephone -> {
                        if (telephone.getTypes().isEmpty()) {
                            return getPhoneNumber(telephone);
                        } else {
                            return String.format("%s: %s", telephone.getTypes().stream().map(VCardParameter::getValue).collect(Collectors.joining(", ")), getPhoneNumber(telephone));
                        }
                    })
                    .collect(Collectors.joining("\n")), Linkify.PHONE_NUMBERS);
        }

        if (!card.getEmails().isEmpty()) {
            addTitleValue(R.string.title_contact_emails, card
                    .getEmails()
                    .stream()
                    .map(email -> {
                        if (email.getTypes().isEmpty()) {
                            return email.getValue();
                        } else {
                            return String.format("%s: %s", email.getTypes().stream().map(VCardParameter::getValue).collect(Collectors.joining(", ")), email.getValue());
                        }
                    })
                    .collect(Collectors.joining("\n")), Linkify.EMAIL_ADDRESSES);
        }

        if (!card.getAddresses().isEmpty()) {
            addTitleValue(R.string.title_contact_addresses, card
                    .getAddresses()
                    .stream()
                    .map(address -> {
                        if (address.getTypes().isEmpty()) {
                            return getAddress(address);
                        } else {
                            return String.format("%s:\n%s", address.getTypes().stream().map(VCardParameter::getValue).collect(Collectors.joining(", ")), getAddress(address));
                        }
                    })
                    .collect(Collectors.joining("\n")), Linkify.MAP_ADDRESSES);
        }

        if (!card.getUrls().isEmpty()) {
            addTitleValue(R.string.title_contact_urls, card
                    .getUrls()
                    .stream()
                    .map(url -> {
                        if (url.getType().isEmpty()) {
                            return url.getValue();
                        } else {
                            return String.format("%s: %s", url.getType(), url.getValue());
                        }
                    })
                    .collect(Collectors.joining("\n")), Linkify.WEB_URLS);
        }

        if (card.getBirthday() != null) {
            addTitleValue(R.string.title_contact_birthday, new SimpleDateFormat(activity.getString(R.string.birthday_date_format)).format(card.getBirthday().getCalendar().getTime()));
        }

        if (!card.getNotes().isEmpty()) {
            addTitleValue(R.string.title_contact_notes, card
                    .getNotes()
                    .stream()
                    .map(note -> {
                        if (note.getType().isEmpty()) {
                            return note.getValue();
                        } else {
                            return String.format("%s: %s", note.getType(), note.getValue());
                        }
                    })
                    .collect(Collectors.joining("\n")));
        }

        addButton(R.string.open_contact).setOnClickListener(v -> {
            open(activity);
        });
    }

}
