package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import it.auron.library.vevent.VEvent;
import it.auron.library.vevent.VEventCostant;

public class VEventElement extends ScanElement {

    public static final Pattern VEVENT = Pattern.compile("^(\\s*BEGIN:VEVENT\\s*([\\S\\s]*)\\s*END:VEVENT\\s*)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public static final SimpleDateFormat VEVENT_FORMAT = new SimpleDateFormat(VEventCostant.DATE_FORMAT, Locale.US);

    private VEvent event;

    public VEventElement(ScanResult result, VEvent event) {
        super(result);
        this.event = event;
    }

    @Override
    public Intent getIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);

        if (event.getDtStart() != null) {
            Calendar start = Calendar.getInstance();
            try {
                start.setTime(VEVENT_FORMAT.parse(event.getDtStart()));
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.getTimeInMillis());
            } catch (ParseException e) {
            }
        }
        if (event.getDtEnd() != null) {
            Calendar end = Calendar.getInstance();
            try {
                end.setTime(VEVENT_FORMAT.parse(event.getDtEnd()));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.getTimeInMillis());
            } catch (ParseException e) {
            }
        }

        if (event.getSummary() != null) {
            intent.putExtra(CalendarContract.Events.TITLE, event.getSummary());
        }
        if (event.getLocation() != null) {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
        }
        return intent;
    }

    @Override
    public int getLayout() {
        return R.layout.result_event;
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

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView summary = activity.findViewById(R.id.summary);
        if (event.getSummary() != null) {
            summary.setText(event.getSummary());
        }

        TextView location = activity.findViewById(R.id.location);
        if (event.getLocation() != null) {
            location.setText(event.getLocation());
        }

        TextView url = activity.findViewById(R.id.url);
        if (event.getUrl() != null) {
            url.setText(event.getUrl());
        }

        TextView time = activity.findViewById(R.id.time);
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(activity.getString(R.string.date_format));
        if (event.getDtStart() != null) {
            Calendar start = Calendar.getInstance();
            try {
                start.setTime(VEVENT_FORMAT.parse(event.getDtStart()));
                sb.append(simpleDateFormat.format(start.getTime()));
                if (event.getDtEnd() != null) {
                    sb.append(" - ");
                }
            } catch (Exception e) {
            }
        }
        if (event.getDtEnd() != null) {
            Calendar end = Calendar.getInstance();
            try {
                end.setTime(VEVENT_FORMAT.parse(event.getDtEnd()));
                sb.append(simpleDateFormat.format(end.getTime()));
            } catch (Exception e) {
            }
        }
        time.setText(sb.toString());

        Button saveEvent = activity.findViewById(R.id.saveEvent);
        saveEvent.setOnClickListener(v -> {
            open(activity);
        });
    }
}
