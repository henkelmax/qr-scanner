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

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.mecard.MeCard;

public class MeCardElement extends ScanElement {

    public static final Pattern MECARD = Pattern.compile("^\\s*(MECARD:)(.+)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private MeCard card;

    public MeCardElement(ScanResult result, MeCard card) {
        super(result);
        this.card = card;
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();

        sb.append(card.getFirstName().orElse(""));
        if (card.getFirstName().isPresent()) {
            sb.append(" ");
        }
        sb.append(card.getLastName().orElse(""));

        String name = sb.toString().trim();

        if (name.isEmpty()) {
            return card.getNickname().orElse("");
        }

        return name;
    }

    @Override
    public Intent getIntent(Context context) {
        Intent insertIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        insertIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        insertIntent.putExtra(ContactsContract.Intents.Insert.NAME, getName());

        if (card.getOrg().isPresent()) {
            insertIntent.putExtra(ContactsContract.Intents.Insert.COMPANY, card.getOrg().get());
        }

        if (card.getNote().isPresent()) {
            insertIntent.putExtra(ContactsContract.Intents.Insert.NOTES, card.getNote().get());
        }

        if (card.getAddress().isPresent()) {
            insertIntent.putExtra(ContactsContract.Intents.Insert.POSTAL, card.getAddress().get());
        }

        ArrayList<ContentValues> contactData = new ArrayList<>();

        for (String tel : card.getTelephone()) {
            ContentValues phoneRow = new ContentValues();
            phoneRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            phoneRow.put(ContactsContract.CommonDataKinds.Phone.NUMBER, tel);
            contactData.add(phoneRow);
        }

        if (card.getEmail().isPresent()) {
            ContentValues emailRow = new ContentValues();
            emailRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
            emailRow.put(ContactsContract.CommonDataKinds.Email.ADDRESS, card.getEmail().get());
            contactData.add(emailRow);
        }

        if (card.getUrl().isPresent()) {
            ContentValues urlRow = new ContentValues();
            urlRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
            urlRow.put(ContactsContract.CommonDataKinds.Website.URL, card.getUrl().get());
            contactData.add(urlRow);
        }


        if (card.getBirthday().isPresent()) {
            ContentValues birthdayRow = new ContentValues();
            birthdayRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
            birthdayRow.put(ContactsContract.Data.CONTENT_TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
            birthdayRow.put(ContactsContract.CommonDataKinds.Event.START_DATE, card.getBirthday().get());
            contactData.add(birthdayRow);
        }

        insertIntent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, contactData);

        return insertIntent;
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
    public void create(ScanResultActivity activity) {
        super.create(activity);

        String name = getName();
        if (!name.isEmpty()) {
            addTitleValue(R.string.title_contact_name, name);
        }
        card.getOrg().ifPresent(org -> {
            addTitleValue(R.string.title_contact_organization, org);
        });

        if (!card.getTelephone().isEmpty()) {
            addTitleValue(R.string.title_contact_telephone_numbers, TextUtils.join("\n", card.getTelephone()), Linkify.PHONE_NUMBERS);
        }

        card.getEmail().ifPresent(email -> {
            addTitleValue(R.string.title_contact_emails, email, Linkify.EMAIL_ADDRESSES);
        });

        card.getAddress().ifPresent(address -> {
            addTitleValue(R.string.title_contact_addresses, address, Linkify.MAP_ADDRESSES);
        });

        card.getUrl().ifPresent(url -> {
            addTitleValue(R.string.title_contact_urls, url, Linkify.WEB_URLS);
        });

        card.getBirthdayDate().ifPresent(date -> {
            addTitleValue(R.string.title_contact_birthday, new SimpleDateFormat(activity.getString(R.string.birthday_date_format)).format(date));
        });

        card.getNote().ifPresent(note -> {
            addTitleValue(R.string.title_contact_notes, note);
        });

        addButton(R.string.open_contact).setOnClickListener(v -> {
            open(activity);
        });
    }

}
