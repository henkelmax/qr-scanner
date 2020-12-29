package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.text.util.Linkify;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.vevent.VEvent;

public class VEventElement extends ScanElement {

    private VEvent event;

    public VEventElement(ScanResult result, VEvent event) {
        super(result);
        this.event = event;
    }

    @Override
    public Intent getIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);

        event.getStartDate().ifPresent(date -> {
            Calendar start = Calendar.getInstance();
            start.setTime(date);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.getTimeInMillis());
        });

        event.getEndDate().ifPresent(date -> {
            Calendar end = Calendar.getInstance();
            end.setTime(date);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.getTimeInMillis());
        });

        event.getSummary().ifPresent(s -> {
            intent.putExtra(CalendarContract.Events.TITLE, s);
        });

        event.getLocation().ifPresent(s -> {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, s);
        });

        return intent;
    }

    @Override
    public String getPreview(Context context) {
        return event.getSummary().orElse(getTimeSpan());
    }

    @Override
    public int getTitle() {
        return R.string.type_event;
    }

    @Override
    public String getMimeType() {
        return "text/calendar";
    }

    @Override
    public String getFileName() {
        return "event.ical";
    }

    public String getTimeSpan() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(activity.getString(R.string.date_format));
        event.getStartDate().ifPresent(date -> {
            sb.append(simpleDateFormat.format(date));
            if (event.getEndDate().isPresent()) {
                sb.append(" - ");
            }
        });

        event.getEndDate().ifPresent(date -> {
            sb.append(simpleDateFormat.format(date));
        });

        return sb.toString();
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        event.getSummary().ifPresent(summary -> {
            addTitleValue(R.string.title_event_summary, summary);
        });

        event.getLocation().ifPresent(location -> {
            addTitleValue(R.string.title_event_location, location, Linkify.MAP_ADDRESSES);
        });

        event.getUrl().ifPresent(url -> {
            addTitleValue(R.string.title_event_url, url, Linkify.WEB_URLS);
        });

        String timeSpan = getTimeSpan();
        if (!timeSpan.isEmpty()) {
            addTitleValue(R.string.title_event_time, timeSpan);
        }

        addButton(R.string.open_event).setOnClickListener(v -> {
            open(activity);
        });
    }
}
