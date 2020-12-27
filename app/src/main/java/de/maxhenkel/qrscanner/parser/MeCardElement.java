package de.maxhenkel.qrscanner.parser;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import it.auron.library.mecard.MeCard;

public class MeCardElement extends ScanElement {

    public static final Pattern MECARD = Pattern.compile("^\\s*((MECARD:)|(BIZCARD:))([\\S\\s]+)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private MeCard card;

    public MeCardElement(ScanResult result, MeCard card) {
        super(result);
        this.card = card;
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        if (card.getName() != null) {
            sb.append(card.getName());
            sb.append(" ");
        }
        if (card.getSurname() != null) {
            sb.append(card.getSurname());
        }

        return sb.toString().trim();
    }

    @Override
    public Intent getIntent(Context context) {
        Intent insertIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        insertIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        insertIntent.putExtra(ContactsContract.Intents.Insert.NAME, getName());

        if (card.getOrg() != null) {
            insertIntent.putExtra(ContactsContract.Intents.Insert.COMPANY, card.getOrg());
        }

        if (card.getNote() != null) {
            insertIntent.putExtra(ContactsContract.Intents.Insert.NOTES, card.getNote());
        }

        if (card.getAddress() != null) {
            insertIntent.putExtra(ContactsContract.Intents.Insert.POSTAL, card.getAddress());
        }

        ArrayList<ContentValues> contactData = new ArrayList<>();

        for (String tel : card.getTelephones()) {
            ContentValues phoneRow = new ContentValues();
            phoneRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            phoneRow.put(ContactsContract.CommonDataKinds.Phone.NUMBER, tel);
            contactData.add(phoneRow);
        }

        if (card.getEmail() != null) {
            ContentValues emailRow = new ContentValues();
            emailRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
            emailRow.put(ContactsContract.CommonDataKinds.Email.ADDRESS, card.getEmail());
            contactData.add(emailRow);
        }

        if (card.getUrl() != null) {
            ContentValues urlRow = new ContentValues();
            urlRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
            urlRow.put(ContactsContract.CommonDataKinds.Website.URL, card.getUrl());
            contactData.add(urlRow);
        }


        if (card.getDate() != null) {
            ContentValues birthdayRow = new ContentValues();
            birthdayRow.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
            birthdayRow.put(ContactsContract.Data.CONTENT_TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY);
            birthdayRow.put(ContactsContract.CommonDataKinds.Event.START_DATE, card.getDate());
            contactData.add(birthdayRow);
        }

        insertIntent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, contactData);

        return insertIntent;
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
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView contact = activity.findViewById(R.id.contact);
        StringBuilder sb = new StringBuilder();

        sb.append(getName());
        sb.append("<br/><br/>");

        if (card.getOrg() != null) {
            sb.append(card.getOrg());
            sb.append("<br/><br/>");
        }

        for (String tel : card.getTelephones()) {
            sb.append(tel);
            sb.append("<br/>");
        }

        if (card.getEmail() != null) {
            sb.append(card.getEmail());
            sb.append("<br/>");
        }

        if (card.getAddress() != null) {
            sb.append(card.getAddress());
            sb.append("<br/>");
        }

        if (card.getUrl() != null) {
            sb.append(card.getUrl());
            sb.append("<br/>");
        }

        if (card.getDate() != null) {
            sb.append(card.getDate());
            sb.append("<br/>");
        }

        if (card.getNote() != null) {
            sb.append("<br/>");
            sb.append(card.getNote());
            sb.append("<br/>");
        }

        contact.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_COMPACT));

        Button send = activity.findViewById(R.id.addContact);
        send.setOnClickListener(v -> {
            open(activity);
        });
    }

}
