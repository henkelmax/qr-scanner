package de.maxhenkel.qrscanner.parser;

import java.util.regex.Matcher;

import de.maxhenkel.qrscanner.parser.matmsg.Email;
import de.maxhenkel.qrscanner.parser.matmsg.MatMsgParser;
import de.maxhenkel.qrscanner.parser.wifi.WifiConfig;
import de.maxhenkel.qrscanner.parser.wifi.WifiConfigParser;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import it.auron.library.mecard.MeCard;
import it.auron.library.mecard.MeCardParser;
import it.auron.library.vevent.VEvent;
import it.auron.library.vevent.VEventParser;

public class QRCodeParser {

    public static ScanElement parse(ScanResult result) {
        Matcher m;
        if ((m = URLElement.URL.matcher(result.getText())).matches()) {
            return new URLElement(result, result.getText());
        }
        if ((m = URLElement.URLTO.matcher(result.getText())).matches()) {
            return new URLElement(result, m.group(1));
        }
        if ((m = EmailElement.EMAIL.matcher(result.getText())).matches()) {
            return EmailElement.email(result, m);
        }
        if ((m = EmailElement.MAILTO.matcher(result.getText())).matches()) {
            return EmailElement.mailto(result, m);
        }
        if ((m = MatMsgParser.MATMSG.matcher(result.getText())).matches()) {
            Email email = MatMsgParser.parse(result.getText());
            if (email != null) {
                return EmailElement.matmsg(result, email);
            }
            return EmailElement.mailto(result, m);
        }
        if ((m = TelElement.TEL.matcher(result.getText())).matches()) {
            return new TelElement(result, m.group(2));
        }
        if ((m = VCardElement.VCARD.matcher(result.getText())).matches()) {
            VCard card = Ezvcard.parse(result.getText()).first();
            if (card != null) {
                return new VCardElement(result, card);
            }
        }
        if ((m = MeCardElement.MECARD.matcher(result.getText())).matches()) {
            MeCard card = MeCardParser.parse(result.getText());
            if (card != null) {
                return new MeCardElement(result, card);
            }
        }
        if ((m = SMSElement.SMS.matcher(result.getText())).matches()) {
            return SMSElement.sms(result, m);
        }
        if ((m = SMSElement.SMS_RAW.matcher(result.getText())).matches()) {
            return SMSElement.smsRaw(result, m);
        }
        if ((m = GeoElement.GEO.matcher(result.getText())).matches()) {
            return GeoElement.geo(result, m);
        }
        if ((m = VEventElement.VEVENT.matcher(result.getText())).matches()) {
            VEvent event = VEventParser.parse(result.getText());
            if (event != null) {
                return new VEventElement(result, event);
            }
        }
        if ((m = WifiElement.WIFI.matcher(result.getText())).matches()) {
            WifiConfig parse = WifiConfigParser.parse(result.getText());
            if (parse != null) {
                return new WifiElement(result, parse);
            }
        }
        if ((m = PlayStoreElement.PLAY_STORE.matcher(result.getText())).matches()) {
            return PlayStoreElement.market(result, m);
        }
        if ((m = CryptoCurrencyElement.CRYPTO.matcher(result.getText())).matches()) {
            return CryptoCurrencyElement.crypto(result, m);
        }

        return new TextElement(result);
    }

}
