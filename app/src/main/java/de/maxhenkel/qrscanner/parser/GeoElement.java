package de.maxhenkel.qrscanner.parser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
    public int getTitle() {
        return R.string.type_geo;
    }

    @Override
    public void create(ScanResultActivity activity) {
        super.create(activity);

        addTitleValue(R.string.title_geo_lat, String.valueOf(lat));
        addTitleValue(R.string.title_geo_lng, String.valueOf(lng));

        if (altitude > 0D) {
            addTitleValue(R.string.title_geo_altitude, String.valueOf(altitude));
        }

        if (zoom > 0) {
            addTitleValue(R.string.title_geo_zoom, String.valueOf(zoom));
        }

        if (!description.isEmpty()) {
            addTitleValue(R.string.title_geo_description, description);
        }

        addButton(R.string.open_geo).setOnClickListener(v -> {
            open(activity);
        });
    }

}
