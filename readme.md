# QR Code Scanner

A simple QR code scanner aimed at providing the best compatibility of all formats.

## Features

- Automatic parsing of the QR code content
- Easily open related apps
- View QR code contents as plain text
- Save QR scan results as file

## Supported Formats

- Plain Text
- URLs
  - `https://`, `http://`, `ftp://`
- E-Mails
  - Plain E-Mails
  - [RFC 6068](https://tools.ietf.org/html/rfc6068) (`mailto:`)
- Telephone Numbers
  - [RFC 3966](https://tools.ietf.org/html/rfc3966) (`tel:`, `voicemail:`)
- Contact Information
  - [vCard](https://en.wikipedia.org/wiki/VCard) ([RFC 2426](https://tools.ietf.org/html/rfc2426))
  - [MeCard](https://en.wikipedia.org/wiki/MeCard_(QR_code))
- SMS/MMS
  - [RFC 5724](https://tools.ietf.org/html/rfc5724) (`sms:`, `smsto:`, `mms:`, `mmsto:`)
- Geographic Information
  - [RFC 5870](https://tools.ietf.org/html/rfc5870) (`geo:`)
- Calendar Events
  - [RFC 5545](https://tools.ietf.org/html/rfc5545) (iCalendar, vCalendar, vEvent)
- Wi-Fi Network Config
  - [ZXing](https://github.com/zxing/zxing/wiki/Barcode-Contents#wi-fi-network-config-android-ios-11) (`wifi:`)
- Google Play Store Apps
  - `market:`

## Credits

- [ez-vcard](https://github.com/mangstadt/ez-vcard)
- [QrCardParsing](https://github.com/RurioLuca/QrCardParsing)
