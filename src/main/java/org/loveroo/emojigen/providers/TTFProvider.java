package org.loveroo.emojigen.providers;

import java.util.HashMap;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.emojigen.data.Character.CharacterList;

/**
 * Loads a TTF file
 */
public class TTFProvider extends Provider {

    private String file = "font.ttf";
    
    private Optional<Double> size = Optional.empty();
    private Optional<Double> oversample = Optional.empty();
    private Optional<Shift> shift = Optional.empty();

    private Optional<CharacterList> skip = Optional.empty();
    private Optional<Filter> filter = Optional.empty();

    public TTFProvider() {
        super(ProviderType.TTF);
    }

    public String file() {
        return file;
    }

    public TTFProvider file(String file) {
        this.file = file;
        return this;
    }

    public Optional<Double> size() {
        return size;
    }

    public TTFProvider size(double size) {
        size(Optional.of(size));
        return this;
    }

    public TTFProvider size(Optional<Double> size) {
        this.size = size;
        return this;
    }

    public Optional<Shift> shift() {
        return shift;
    }

    public TTFProvider shift(Shift shift) {
        shift(Optional.of(shift));
        return this;
    }

    public TTFProvider shift(Optional<Shift> shift) {
        this.shift = shift;
        return this;
    }

    public Optional<Double> oversample() {
        return oversample;
    }

    public TTFProvider oversample(double oversample) {
        oversample(Optional.of(oversample));
        return this;
    }

    public TTFProvider oversample(Optional<Double> oversample) {
        this.oversample = oversample;
        return this;
    }

    public Optional<CharacterList> skip() {
        return skip;
    }

    public TTFProvider skip(CharacterList skip) {
        skip(Optional.of(skip));
        return this;
    }

    public TTFProvider skip(Optional<CharacterList> skip) {
        this.skip = skip;
        return this;
    }

    public Optional<Filter> filter() {
        return filter;
    }

    public TTFProvider filter(Filter filter) {
        filter(Optional.of(filter));
        return this;
    }

    public TTFProvider filter(Optional<Filter> filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public BuildResult build(String output) throws JSONException {
        final var json = buildBase();
        json.put("file", file());

        if(size().isPresent()) {
            json.put("size", size().get());
        }

        if(oversample().isPresent()) {
            json.put("oversample", oversample().get());
        }

        if(shift().isPresent()) {
            json.put("shift", shift().get().build());
        }

        if(skip().isPresent()) {
            json.put("skip", skip().get().build());
        }

        if(filter().isPresent()) {
            json.put("filter", filter().get().build());
        }

        return new BuildResult(json);
    }
    
    public static record Shift(int horizontal, int vertical) {

        public JSONArray build() {
            final var json = new JSONArray();
            
            json.put(horizontal());
            json.put(vertical());

            return json;
        }
    }

    public static class Filter {

        private final HashMap<FilterType, Optional<Boolean>> filters = new HashMap<>();

        public Optional<Boolean> filter(FilterType type) {
            return filters.getOrDefault(type, Optional.empty());
        }

        public Filter filter(FilterType type, boolean value) {
            filter(type, Optional.of(value));
            return this;
        }

        public Filter filter(FilterType type, Optional<Boolean> value) {
            filters.put(type, value);
            return this;
        }

        public JSONObject build() throws JSONException {
            final var json = new JSONObject();

            for(var type : FilterType.values()) {
                final var value = filter(type);

                if(value.isEmpty()) {
                    continue;
                }

                json.put(type.id(), value.get());
            }

            return json;
        }
        
        public static enum FilterType {

            UNIFORM("uniform"),
            JP("jp");

            private final String id;

            FilterType(String id) {
                this.id = id;
            }

            public String id() {
                return id;
            }
        }
    }
}
