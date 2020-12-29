package de.maxhenkel.qrscanner.parser;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.util.Linkify;

import java.util.ArrayList;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.bizcard.BizCard;

public class BizCardElement extends ScanElement {

    public static final Pattern BIZCARD = Pattern.compile("^\\s*(BIZCARD:)(.+)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private BizCard card;

    public BizCardElement(ScanResult result, BizCard card) {
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

        return sb.toString().trim();
    }

    @Override
    public Intent getIntent(Context context) {
        Intent insertIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        insertIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        insertIntent.putExtra(ContactsContract.Intents.Insert.NAME, getName());

        if (card.getCompany().isPresent()) {
            insertIntent.putExtra(ContactsContract.Intents.Insert.COMPANY, card.getCompany().get());
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

        insertIntent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, contactData);

        return insertIntent;
    }

    @Override
    public String getPreview(Context context) {
        return getName();
    }

    @Override
    public int getTitle() {
        return R.string.type_bizcard;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        String name = getName();
        if (!name.isEmpty()) {
            addTitleValue(R.string.title_contact_name, name);
        }

        card.getTitle().ifPresent(title -> {
            addTitleValue(R.string.title_contact_biz_title, title);
        });

        card.getCompany().ifPresent(company -> {
            addTitleValue(R.string.title_contact_company, company);
        });

        if (!card.getTelephone().isEmpty()) {
            addTitleValue(R.string.title_contact_telephone_numbers, TextUtils.join("\n", card.getTelephone()), Linkify.PHONE_NUMBERS);
        }

        if (card.getEmail().isPresent()) {
            addTitleValue(R.string.title_contact_emails, card.getEmail().get(), Linkify.EMAIL_ADDRESSES);
        }

        if (card.getAddress().isPresent()) {
            addTitleValue(R.string.title_contact_addresses, card.getAddress().get(), Linkify.MAP_ADDRESSES);
        }

        addButton(R.string.open_contact).setOnClickListener(v -> {
            open(activity);
        });
    }

}
