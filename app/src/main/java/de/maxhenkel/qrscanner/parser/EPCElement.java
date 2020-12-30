package de.maxhenkel.qrscanner.parser;

import android.content.Context;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.epc.EPC;

public class EPCElement extends ScanElement {

    private EPC epc;

    public EPCElement(ScanResult result, EPC epc) {
        super(result);
        this.epc = epc;
    }

    @Override
    public String getPreview(Context context) {
        return epc.getIban().orElse("");
    }

    @Override
    public int getTitle() {
        return R.string.type_payment;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        epc.getIdentification().ifPresent(id -> {
            addTitleValue(R.string.title_payment_identification, id);
        });

        epc.getBic().ifPresent(bic -> {
            addTitleValue(R.string.title_payment_bic, bic);
        });

        epc.getName().ifPresent(name -> {
            addTitleValue(R.string.title_payment_recipient_name, name);
        });

        epc.getIban().ifPresent(iban -> {
            addTitleValue(R.string.title_payment_iban, iban);
        });

        epc.getAmount().ifPresent(amount -> {
            addTitleValue(R.string.title_payment_amount, amount);
        });

        epc.getReference().ifPresent(ref -> {
            addTitleValue(R.string.title_payment_reference, ref);
        });

        epc.getInformation().ifPresent(info -> {
            addTitleValue(R.string.title_payment_information, info);
        });
    }

}
