package de.maxhenkel.qrscanner.parser;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.spd.SPD;

public class SPDElement extends ScanElement {

    private SPD spd;

    public SPDElement(ScanResult result, SPD spd) {
        super(result);
        this.spd = spd;
    }

    @Override
    public String getMimeType() {
        return "application/x-shortpaymentdescriptor";
    }

    @Override
    public String getFileName() {
        return "payment.spayd";
    }

    @Override
    public String getPreview(Context context) {
        return spd.getAccount().orElse(new SPD.Account()).getIban().orElse("");
    }

    @Override
    public int getTitle() {
        return R.string.type_payment;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        Optional<SPD.Account> account = spd.getAccount();
        account.ifPresent(acc -> {
            addTitleTextView(R.string.title_payment_account);
            addAcc(acc);
        });

        List<SPD.Account> alternativeAccounts = spd.getAlternativeAccounts();
        if (!alternativeAccounts.isEmpty()) {
            addTitleTextView(R.string.title_payment_alternative_accounts);
        }
        for (SPD.Account acc : alternativeAccounts) {
            addAcc(acc);
        }

        spd.getAmount().ifPresent(amount -> {
            addTitleValue(R.string.title_payment_amount, amount);
        });

        spd.getCurrency().ifPresent(curr -> {
            addTitleValue(R.string.title_payment_currency, curr);
        });

        spd.getReference().ifPresent(ref -> {
            addTitleValue(R.string.title_payment_reference, ref);
        });

        spd.getRecipientName().ifPresent(rec -> {
            addTitleValue(R.string.title_payment_recipient_name, rec);
        });

        spd.getDueDate().ifPresent(date -> {
            addTitleValue(R.string.title_payment_due_date, new SimpleDateFormat(activity.getString(R.string.birthday_date_format)).format(date));
        });

        spd.getPaymentType().ifPresent(type -> {
            addTitleValue(R.string.title_payment_type, type);
        });

        spd.getMessage().ifPresent(msg -> {
            addTitleValue(R.string.title_payment_message, msg);
        });

        spd.getChecksum().ifPresent(chk -> {
            addTitleValue(R.string.title_payment_checksum, chk);
        });
    }

    private void addAcc(SPD.Account acc) {
        addTitleValue(R.string.title_payment_iban, acc.getIban().orElse(""));
        acc.getBic().ifPresent(bic -> {
            addTitleValue(R.string.title_payment_bic, bic);
        });
    }

}
