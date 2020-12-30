# QR Code Scanner ![GitHub release (latest by date)](https://img.shields.io/github/v/release/henkelmax/qr-scanner?include_prereleases) ![GitHub issues](https://img.shields.io/github/issues-raw/henkelmax/qr-scanner) ![GitHub All Releases](https://img.shields.io/github/downloads/henkelmax/qr-scanner/total)

A simple QR code scanner aimed at providing the best compatibility of all formats.

## Useful Links

- [Downloads](https://github.com/henkelmax/qr-scanner/releases)
- [Example QR Codes](examples/)

## Features

- Automatic parsing of the QR code content
- Easily open related apps
- View QR code contents as plain text
- Save QR scan results as file
- A history of the last 64 scans

## Supported Formats

- Plain Text
- URLs
  - `https://`, `http://`, `ftp://`
- E-Mails
  - Plain E-Mails
  - [RFC 6068](https://tools.ietf.org/html/rfc6068) (`mailto:`)
  - MATMSG
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
- Crypto Currencies
  - [BIP21](https://github.com/bitcoin/bips/blob/master/bip-0021.mediawiki)
  - Bitcoin (`bitcoin:`)
  - Bitcoin Cash (`bitcoincash:`)
  - Ethereum (`ethereum:`)
  - Litecoin (`litecoin:`)
  - Dash (`dash:`)
- SIP
  - [RFC 3261](https://tools.ietf.org/html/rfc3261#page-148) (`sip:`, `sips:`)
- Two Factor Authentication
  - TOTP (`otpauth:`)
  - HOTP (`otpauth:`)

## Supported Barcode Types

- [QR Code](https://en.wikipedia.org/wiki/QR_code)
- [Aztec Code](https://en.wikipedia.org/wiki/Aztec_Code)
- [Data Matrix](https://en.wikipedia.org/wiki/Data_Matrix)
- [PDF417](https://en.wikipedia.org/wiki/PDF417)
- [Codabar](https://en.wikipedia.org/wiki/Codabar)
- [Code 128](https://en.wikipedia.org/wiki/Code_128)
- [Code 39](https://en.wikipedia.org/wiki/Code_39)
- [Code 93](https://en.wikipedia.org/wiki/Code_93)
- [EAN-13](https://en.wikipedia.org/wiki/International_Article_Number)
- [EAN-8](https://en.wikipedia.org/wiki/EAN-8)
- [Interleaved Two of Five](https://en.wikipedia.org/wiki/Interleaved_2_of_5)
- [GS1 DataBar (RSS-14)](https://en.wikipedia.org/wiki/GS1_DataBar)
- [GS1 DataBar Expanded (RSS Expanded)](https://en.wikipedia.org/wiki/GS1_DataBar)
- [UPC-A](https://en.wikipedia.org/wiki/Universal_Product_Code)
- [UPC-E](https://en.wikipedia.org/wiki/Universal_Product_Code)

## Credits

- [ZXing Android Embedded](https://github.com/journeyapps/zxing-android-embedded)
- [ez-vcard](https://github.com/mangstadt/ez-vcard)
