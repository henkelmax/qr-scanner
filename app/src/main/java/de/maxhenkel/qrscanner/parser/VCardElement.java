package de.maxhenkel.qrscanner.parser;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
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
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private VCard card;

    public VCardElement(ScanResult result, VCard card) {
        super(result);
        this.card = card;
    }

    public VCard getCard() {
        return card;
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
            birthdayRow.put(ContactsContract.CommonDataKinds.Event.START_DATE, DATE_FORMAT.format(birthday.getCalendar().getTime()));
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
    public int getLayout() {
        return R.layout.result_contact;
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
        TextView contact = activity.findViewById(R.id.contact);
        StringBuilder sb = new StringBuilder();

        sb.append(getName());
        sb.append("<br/><br/>");

        if (card.getOrganization() != null) {
            for (String org : card.getOrganization().getValues()) {
                sb.append(org);
                sb.append(" ");
            }
            sb.append("<br/><br/>");
        }

        for (Telephone tel : card.getTelephoneNumbers()) {
            sb.append("<b>");
            sb.append(tel.getTypes().stream().map(VCardParameter::getValue).collect(Collectors.joining(", ")));
            if (!tel.getTypes().isEmpty()) {
                sb.append(": ");
            }
            sb.append("</b>");
            sb.append(getPhoneNumber(tel));
            sb.append("<br/>");
        }

        for (Email email : card.getEmails()) {
            sb.append("<b>");
            sb.append(email.getTypes().stream().map(VCardParameter::getValue).collect(Collectors.joining(", ")));
            if (!email.getTypes().isEmpty()) {
                sb.append(": ");
            }
            sb.append("</b>");
            sb.append(email.getValue());
            sb.append("<br/>");
        }

        for (Address address : card.getAddresses()) {
            sb.append("<b>");
            sb.append(address.getTypes().stream().map(VCardParameter::getValue).collect(Collectors.joining(", ")));
            if (!address.getTypes().isEmpty()) {
                sb.append(": ");
            }
            sb.append("</b>");
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
            sb.append("<br/>");
        }

        for (Url url : card.getUrls()) {
            if (url.getType() != null) {
                sb.append("<b>");
                sb.append(url.getType());
                sb.append(": </b>");
            }
            sb.append(url.getValue());
            sb.append("<br/>");
        }

        for (Birthday birthday : card.getBirthdays()) {
            sb.append(DATE_FORMAT.format(birthday.getCalendar().getTime()));
            sb.append("<br/>");
        }

        sb.append("<br/>");

        for (Note note : card.getNotes()) {
            if (note.getType() != null) {
                sb.append("<b>");
                sb.append(note.getType());
                sb.append(": </b>");
            }
            sb.append(note.getValue());
            sb.append("<br/>");
        }

        contact.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_COMPACT));

        Button send = activity.findViewById(R.id.addContact);
        send.setOnClickListener(v -> {
            open(activity);
        });
    }

}
