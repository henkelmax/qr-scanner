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
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.mecard.MeCard;

public class MeCardElement extends ScanElement {

    public static final Pattern MECARD = Pattern.compile("^\\s*((MECARD:)|(BIZCARD:))([\\S\\s]+)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

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
        sb.append("<br/>");

        Optional<Date> birthdayDate = card.getBirthdayDate();
        if (birthdayDate.isPresent()) {
            SimpleDateFormat sdf = new SimpleDateFormat(activity.getString(R.string.birthday_date_format));
            sb.append("*");
            sb.append(sdf.format(birthdayDate.get()));
            sb.append("<br/>");
        }
        sb.append("<br/>");

        if (card.getOrg().isPresent()) {
            sb.append(card.getOrg().get());
            sb.append("<br/><br/>");
        }

        for (String tel : card.getTelephone()) {
            sb.append(tel);
            sb.append("<br/>");
        }

        if (card.getEmail().isPresent()) {
            sb.append(card.getEmail().get());
            sb.append("<br/>");
        }

        if (card.getAddress().isPresent()) {
            sb.append(card.getAddress().get());
            sb.append("<br/>");
        }

        if (card.getUrl().isPresent()) {
            sb.append(card.getUrl().get());
            sb.append("<br/>");
        }

        if (card.getNote().isPresent()) {
            sb.append("<br/>");
            sb.append(card.getNote().get());
            sb.append("<br/>");
        }

        contact.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_COMPACT));

        Button send = activity.findViewById(R.id.addContact);
        send.setOnClickListener(v -> {
            open(activity);
        });
    }

}
