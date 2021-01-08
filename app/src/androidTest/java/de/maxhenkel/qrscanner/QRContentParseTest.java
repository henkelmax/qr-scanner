package de.maxhenkel.qrscanner;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import de.maxhenkel.qrscanner.parser.*;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QRContentParseTest {

    @Test
    public void parseText() {
        ScanElement e = fromText("Hello World!");
        assertTrue(e instanceof TextElement);
    }

    @Test
    public void parseURI() {
        ScanElement e = fromText("test:test123");
        assertTrue(e instanceof URIElement);
    }

    @Test
    public void parseURL() {
        ScanElement e = fromText("https://example.com");
        assertTrue(e instanceof URLElement);
    }

    @Test
    public void parseEmail() {
        ScanElement e = fromText("john.doe@example.com");
        assertTrue(e instanceof EmailElement);
    }

    @Test
    public void parseMailto() {
        ScanElement e = fromText("mailto:john.doe@example.com?cc=jane.doe@example.com,john.doe@example.com,&bcc=john.doe@example.com&subject=Hello&body=Hello%20World%21");
        assertTrue(e instanceof EmailElement);
    }

    @Test
    public void parseMatmsg() {
        ScanElement e = fromText("MATMSG:TO:john.doe@example.com;SUB:Hello;BODY:Hello World!;;");
        assertTrue(e instanceof EmailElement);
    }

    @Test
    public void parseTel() {
        ScanElement e = fromText("tel:+1-123-555-4567");
        assertTrue(e instanceof TelElement);
    }

    @Test
    public void parseVcard() {
        ScanElement e = fromText("BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "N:Doe;John;;Mr.;\n" +
                "FN:John Doe\n" +
                "ORG:John Doe Co.\n" +
                "TEL;TYPE=WORK,VOICE:(123) 555-4567\n" +
                "TEL;TYPE=HOME,VOICE:(765) 555-4321\n" +
                "ADR;TYPE=HOME:;;123 Main Street;New York;NY;10030;United States of America\n" +
                "LABEL;TYPE=HOME:123 Main Street\\nNew York\\, NY 10030\\nUnited States of America\n" +
                "EMAIL:john.doe@example.com\n" +
                "URL;TYPE=Homepage:https://johndoe.example.com/\n" +
                "BDAY:19990101\n" +
                "REV:2020-12-24T18:00:00Z\n" +
                "END:VCARD");
        assertTrue(e instanceof VCardElement);
    }

    @Test
    public void parseMecard() {
        ScanElement e = fromText("MECARD:N:Doe,John;NICKNAME:Johnny;ORG:John Doe Co.;ADR:123 Main Street, New York, NY 10030;BDAY:19990101;TEL:1235554567;TEL:7655554321;EMAIL:john.doe@example.com;URL:https://johndoe.example.com/;NOTE:Hello World!;;");
        assertTrue(e instanceof MeCardElement);
    }

    @Test
    public void parseBizcard() {
        ScanElement e = fromText("BIZCARD:N:John;X:Doe;T:Software Engineer;C:John Doe Co.;A:123 Main Street, New York, NY 10030;B:+11235554567;M:+17655554321;E:john.doe@example.com;;");
        assertTrue(e instanceof BizCardElement);
    }

    @Test
    public void parseSms() {
        ScanElement e = fromText("sms:+11235554567,+17655554321?body=Hello%20World%21");
        assertTrue(e instanceof SMSElement);
    }

    @Test
    public void parseGeo() {
        ScanElement e = fromText("geo:40.762573,-73.8336205,100?z=22&q=My%20Home%21");
        assertTrue(e instanceof GeoElement);
    }

    @Test
    public void parseVevent() {
        ScanElement e = fromText("BEGIN:VEVENT\n" +
                "SUMMARY:Summer Vacation!\n" +
                "LOCATION:Germany\n" +
                "URL:https://johndoe.example.com/\n" +
                "DTSTART:20210607T080000Z\n" +
                "DTEND:20210614T080000Z\n" +
                "END:VEVENT");
        assertTrue(e instanceof VEventElement);
    }

    @Test
    public void parseWifi() {
        ScanElement e = fromText("WIFI:T:WPA2-EAP;S:John's Wi-Fi;P:supersecretp@ssword;H:true;E:TTLS;A:anaonymousIdent;I:ident;PH2:MSCHAPV2;;");
        assertTrue(e instanceof WifiElement);
    }

    @Test
    public void parsePlayStore() {
        ScanElement e = fromText("market://details?id=com.google.android.apps.maps");
        assertTrue(e instanceof PlayStoreElement);
    }

    @Test
    public void parseBitcoin() {
        ScanElement e = fromText("bitcoin:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?amount=0.1&label=John%20Doe&message=For%20you%21");
        assertTrue(e instanceof CryptoCurrencyElement);
    }

    @Test
    public void parseEthereum() {
        ScanElement e = fromText("ethereum:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?amount=1.5&label=John%20Doe&message=For%20you%21");
        assertTrue(e instanceof CryptoCurrencyElement);
    }

    @Test
    public void parseSip() {
        ScanElement e = fromText("sip:1234567@example.com:1234");
        assertTrue(e instanceof SipElement);
    }

    @Test
    public void parseTotp() {
        ScanElement e = fromText("otpauth://totp/My%20Google%202FA?secret=JBSWY3DPEHPK3PXP&issuer=Google&algorithm=SHA1&digits=6&period=30");
        assertTrue(e instanceof OTPElement);
    }

    @Test
    public void parseHotp() {
        ScanElement e = fromText("otpauth://hotp/My%20Google%202FA?secret=JBSWY3DPEHPK3PXP&issuer=Google&counter=0&algorithm=SHA1&digits=6");
        assertTrue(e instanceof OTPElement);
    }

    @Test
    public void parseSpd() {
        ScanElement e = fromText("SPD*1.0*ACC:DE1234567890+ABCDEF*ALT-ACC:DE0987654321,123123123123+ABCDEF*AM:420.69*CC:EUR*RF:12345*RN:John Doe*DT:20201231*PT:SPD*MSG:Here is your money%21*CRC32:FF00FF00");
        assertTrue(e instanceof SPDElement);
    }

    @Test
    public void parsePayto() {
        ScanElement e = fromText("payto://sepa/DE1234567890?amount=EUR:420.69&creditor-name=John%20Doe&debitor-name=Jane%20Doe&message=Here%20is%20your%20money&instruction=Save%20it");
        assertTrue(e instanceof PaytoElement);
    }

    @Test
    public void parseEpc() {
        ScanElement e = fromText("BCD\n" +
                "001\n" +
                "1\n" +
                "SCT\n" +
                "ABCDEF\n" +
                "John Doe\n" +
                "DE1234567890\n" +
                "EUR420\n" +
                "Your money!\n" +
                "Save it!");
        assertTrue(e instanceof EPCElement);
    }

    private static ScanElement fromText(String text) {
        Instrumentation i = InstrumentationRegistry.getInstrumentation();
        ScanResult scanResult = new ScanResult(System.currentTimeMillis(), text);
        Intent intent = new Intent(i.getTargetContext(), ScanResultActivity.class);
        intent.putExtra("scanResult", scanResult);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Activity a = i.startActivitySync(intent);
        assertTrue(a instanceof ScanResultActivity);
        ScanResultActivity activity = (ScanResultActivity) a;
        try {
            Field scanElement = ScanResultActivity.class.getDeclaredField("element");
            scanElement.setAccessible(true);
            return (ScanElement) scanElement.get(activity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
