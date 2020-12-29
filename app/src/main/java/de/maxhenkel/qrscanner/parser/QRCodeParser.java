package de.maxhenkel.qrscanner.parser;

import java.util.regex.Matcher;

import de.maxhenkel.qrscanner.parser.email.Email;
import de.maxhenkel.qrscanner.parser.email.MatMsgParser;
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
        if ((m = URLElement.URL.matcher(result.getData())).matches()) {
            return new URLElement(result, result.getData());
        }
        if ((m = URLElement.URLTO.matcher(result.getData())).matches()) {
            return new URLElement(result, m.group(1));
        }
        if ((m = EmailElement.EMAIL.matcher(result.getData())).matches()) {
            return EmailElement.plainEmail(result, m);
        }
        if ((m = EmailElement.MAILTO.matcher(result.getData())).matches()) {
            return EmailElement.mailto(result, m);
        }
        if ((m = MatMsgParser.MATMSG.matcher(result.getData())).matches()) {
            Email email = MatMsgParser.parse(result.getData());
            if (email != null) {
                return EmailElement.email(result, email);
            }
            return EmailElement.mailto(result, m);
        }
        if ((m = TelElement.TEL.matcher(result.getData())).matches()) {
            return new TelElement(result, m.group(2));
        }
        if ((m = VCardElement.VCARD.matcher(result.getData())).matches()) {
            VCard card = Ezvcard.parse(result.getData()).first();
            if (card != null) {
                return new VCardElement(result, card);
            }
        }
        if ((m = MeCardElement.MECARD.matcher(result.getData())).matches()) {
            MeCard card = MeCardParser.parse(result.getData());
            if (card != null) {
                return new MeCardElement(result, card);
            }
        }
        if ((m = SMSElement.SMS.matcher(result.getData())).matches()) {
            return SMSElement.sms(result, m);
        }
        if ((m = SMSElement.SMS_RAW.matcher(result.getData())).matches()) {
            return SMSElement.smsRaw(result, m);
        }
        if ((m = GeoElement.GEO.matcher(result.getData())).matches()) {
            return GeoElement.geo(result, m);
        }
        if ((m = VEventElement.VEVENT.matcher(result.getData())).matches()) {
            VEvent event = VEventParser.parse(result.getData());
            if (event != null) {
                return new VEventElement(result, event);
            }
        }
        if ((m = WifiElement.WIFI.matcher(result.getData())).matches()) {
            WifiConfig parse = WifiConfigParser.parse(result.getData());
            if (parse != null) {
                return new WifiElement(result, parse);
            }
        }
        if ((m = PlayStoreElement.PLAY_STORE.matcher(result.getData())).matches()) {
            return PlayStoreElement.market(result, m);
        }
        if ((m = CryptoCurrencyElement.CRYPTO.matcher(result.getData())).matches()) {
            return CryptoCurrencyElement.crypto(result, m);
        }
        if ((m = SipElement.SIP.matcher(result.getData())).matches()) {
            return SipElement.sip(result, m);
        }

        return new TextElement(result);
    }

}
