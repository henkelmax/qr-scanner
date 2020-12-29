package de.maxhenkel.qrscanner.parser.vevent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class VEvent {

    protected static final SimpleDateFormat VEVENT_FORMAT = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    protected String summary;
    protected String location;
    protected String url;
    protected String start;
    protected String end;

    public VEvent(String summary, String location, String url, String start, String end) {
        this.summary = summary;
        this.location = location;
        this.url = url;
        this.start = start;
        this.end = end;
    }

    protected VEvent() {

    }

    public Optional<String> getSummary() {
        return Optional.ofNullable(summary);
    }

    public Optional<String> getLocation() {
        return Optional.ofNullable(location);
    }

    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }

    public Optional<String> getStart() {
        return Optional.ofNullable(start);
    }

    public Optional<String> getEnd() {
        return Optional.ofNullable(end);
    }

    public Optional<Date> getStartDate() {
        return parseDate(start);
    }

    public Optional<Date> getEndDate() {
        return parseDate(end);
    }

    protected Optional<Date> parseDate(String date) {
        if (date == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(VEVENT_FORMAT.parse(date));
        } catch (ParseException e1) {
            try {
                return Optional.ofNullable(DATE_FORMAT.parse(date));
            } catch (ParseException e2) {
                return Optional.empty();
            }
        }
    }
}
