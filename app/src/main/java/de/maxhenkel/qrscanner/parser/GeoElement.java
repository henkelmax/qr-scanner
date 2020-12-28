package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maxhenkel.qrscanner.R;
import de.maxhenkel.qrscanner.ScanResultActivity;
import de.maxhenkel.qrscanner.parser.query.Query;

public class GeoElement extends ScanElement {

    public static final Pattern GEO = Pattern.compile("^geo:([-+\\d.]+),([-+\\d.]+)(?:,([-+\\d.]+))?(?:\\?([\\s\\S]+))?$", Pattern.CASE_INSENSITIVE);
    public static final Pattern MAPS_QUERY = Pattern.compile("^([-+\\d.]+),([-+\\d.]+)(?:\\((.+)\\))?$", Pattern.CASE_INSENSITIVE);

    private double lat, lng, altitude;
    private int zoom;
    private String description;

    public GeoElement(ScanResult result, double lat, double lng, double altitude, int zoom, String description) {
        super(result);
        this.lat = lat;
        this.lng = lng;
        this.altitude = altitude;
        this.zoom = zoom;
        this.description = description;
    }

    public static GeoElement geo(ScanResult result, Matcher matcher) {
        double lat = 0D;
        double lng = 0D;
        double altitude = 0D;
        try {
            lat = Double.parseDouble(matcher.group(1));
        } catch (Exception e) {
        }
        try {
            lng = Double.parseDouble(matcher.group(2));
        } catch (Exception e) {
        }
        try {
            altitude = Double.parseDouble(matcher.group(3));
        } catch (Exception e) {
        }

        Query query = Query.parse(matcher.group(4));

        int zoom = query.getInt("z").orElse(0);

        String desc = "";
        String q = query.getValue("q");
        if (q != null) {
            Matcher m = MAPS_QUERY.matcher(q);
            if (m.matches()) {
                try {
                    lat = Double.parseDouble(m.group(1));
                    lng = Double.parseDouble(m.group(2));
                } catch (Exception e) {
                }
                String d = m.group(3);
                if (d != null) {
                    desc = d;
                }
            } else {
                desc = q;
            }
        }

        return new GeoElement(result, lat, lng, altitude, zoom, desc);
    }

    @Override
    public Intent getIntent(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(result.getData()));
    }

    @Override
    public String getPreview(Context context) {
        return context.getString(R.string.preview_geo, lat, lng);
    }

    @Override
    public int getLayout() {
        return R.layout.result_geo;
    }

    @Override
    public int getTitle() {
        return R.string.type_geo;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);
        TextView latText = activity.findViewById(R.id.lat);
        latText.setText(String.valueOf(lat));

        TextView lngText = activity.findViewById(R.id.lng);
        lngText.setText(String.valueOf(lng));

        TextView altitudeText = activity.findViewById(R.id.altitude);
        if (altitude > 0D) {
            altitudeText.setText(String.valueOf(altitude));
        } else {
            TextView altitudeTitle = activity.findViewById(R.id.titleAltitude);
            altitudeText.setVisibility(View.GONE);
            altitudeTitle.setVisibility(View.GONE);
        }
        TextView zoomText = activity.findViewById(R.id.zoom);
        if (zoom > 0) {
            zoomText.setText(String.valueOf(zoom));
        } else {
            TextView zoomTitle = activity.findViewById(R.id.titleZoom);
            zoomText.setVisibility(View.GONE);
            zoomTitle.setVisibility(View.GONE);
        }

        TextView descriptionText = activity.findViewById(R.id.description);
        if (!description.isEmpty()) {
            descriptionText.setText(description);
        } else {
            TextView descriptionTitle = activity.findViewById(R.id.titleDescription);
            descriptionText.setVisibility(View.GONE);
            descriptionTitle.setVisibility(View.GONE);
        }

        Button open = activity.findViewById(R.id.openMap);
        open.setOnClickListener(v -> {
            open(activity);
        });
    }

}
