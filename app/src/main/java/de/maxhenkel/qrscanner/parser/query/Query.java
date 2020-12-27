package de.maxhenkel.qrscanner.parser.query;

import androidx.annotation.Nullable;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Query {

    private List<NameValuePair> parameters;

    protected Query(List<NameValuePair> parameters) {
        this.parameters = parameters;
    }

    public static Query parse(@Nullable String str) {
        if (str == null) {
            return new Query(Collections.emptyList());
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

}
