package de.maxhenkel.qrscanner.parser.query;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Query {

    private List<NameValuePair> parameters;

    protected Query(List<NameValuePair> parameters) {
        this.parameters = parameters;
    }

    public Query() {
        this(new ArrayList<>());
    }

    public static Query parse(@Nullable String str) {
        if (str == null) {
            return new Query(Collections.emptyList());
        }
        if (str.startsWith("?")) {
            str = str.substring(1);
        }
        return new Query(URLEncodedUtils.parse(str, StandardCharsets.UTF_8));
    }

    /**
     * Gets the value of the provided parameter
     * <p>
     * Note that the parameter is case insensitive in this case
     *
     * @param param the parameter
     * @return the value or null if it doesn't exist
     */
    @Nullable
    public String getValue(String param) {
        return get(param).orElse(null);
    }

    /**
     * Gets the value of the provided parameter
     * <p>
     * Note that the parameter is case insensitive in this case
     *
     * @param param the parameter
     * @return toptional value
     */
    public Optional<String> get(String param) {
        return parameters.stream().filter(e -> e.getName().equalsIgnoreCase(param)).map(NameValuePair::getValue).findFirst();
    }

    public Optional<Integer> getInt(String param) {
        Optional<String> str = get(param);
        if (!str.isPresent()) {
            return Optional.empty();
        }
        Optional<Integer> i = Optional.empty();
        try {
            i = Optional.of(Integer.parseInt(str.get()));
        } catch (Exception e) {
        }
        return i;
    }

    public Optional<Double> getDouble(String param) {
        Optional<String> str = get(param);
        if (!str.isPresent()) {
            return Optional.empty();
        }
        Optional<Double> d = Optional.empty();
        try {
            d = Optional.of(Double.parseDouble(str.get()));
        } catch (Exception e) {
        }
        return d;
    }

    /**
     * Adds the query parameter
     * <p>
     * If one parameter is null it won't get added
     *
     * @param key   the key
     * @param value the value
     */
    public void add(String key, String value) {
        if (key != null && value != null) {
            parameters.add(new BasicNameValuePair(key, value));
        }
    }

    /**
     * Adds the query parameter
     * <p>
     * If one parameter is null it won't get added
     *
     * @param key   the key
     * @param value the value
     */
    public void add(String key, int value) {
        add(key, String.valueOf(value));
    }

    /**
     * Builds a query string including the '?'
     *
     * @return the encoded query string
     */
    public String build() {
        return "?" + parameters.stream().filter(p -> !p.getName().trim().isEmpty()).map(p -> join("=", encode(p.getName()), encode(p.getValue()))).collect(Collectors.joining("&"));
    }

    private String encode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private String join(String delimiter, String... elements) {
        return TextUtils.join(delimiter, elements);
    }

}
