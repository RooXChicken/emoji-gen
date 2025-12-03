package org.loveroo.emojigen.providers;

import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.emojigen.data.Character;

/**
 * Loads a TTF file
 */
public class TTFProvider extends Provider {

    private String file = "font.ttf";
    
    private Optional<Double> size = Optional.empty();
    private Optional<Double> oversample = Optional.empty();
    private Optional<Shift> shift = Optional.empty();

    private Optional<List<Character>> skip = Optional.empty();
    private Optional<Filter> filter = Optional.empty();

    public TTFProvider() {
        super(ProviderType.SPACE);
    }

    public String file() {
        return file;
    }

    public void file(String file) {
        this.file = file;
    }

    public Optional<Double> size() {
        return size;
    }

    public void size(double size) {
        size(Optional.of(size));
    }

    public void size(Optional<Double> size) {
        this.size = size;
    }

    public Optional<Shift> shift() {
        return shift;
    }

    public void shift(Shift shift) {
        shift(Optional.of(shift));
    }

    public void shift(Optional<Shift> shift) {
        this.shift = shift;
    }

    public Optional<Double> oversample() {
        return oversample;
    }

    public void oversample(double oversample) {
        oversample(Optional.of(oversample));
    }

    public void oversample(Optional<Double> oversample) {
        this.oversample = oversample;
    }

    public Optional<List<Character>> skip() {
        return skip;
    }

    public void skip(List<Character> skip) {
        skip(Optional.of(skip));
    }

    public void skip(Optional<List<Character>> skip) {
        this.skip = skip;
    }

    public void addSkip(Character character) {
        if(skip().isEmpty()) {
            return;
        }
        
        skip().get().add(character);
    }

    public Optional<Filter> filter() {
        return filter;
    }

    public void filter(Filter filter) {
        filter(Optional.of(filter));
    }

    public void filter(Optional<Filter> filter) {
        this.filter = filter;
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

        }

        if(filter().isPresent()) {
            final var filter = filter().get();
            final var filterJson = new JSONObject();
            
            if(filter.uniform().isPresent()) {
                filterJson.put("uniform", filter.uniform().get());
            }

            if(filter.jp().isPresent()) {
                filterJson.put("jp", filter.jp().get());
            }
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

        private Optional<Boolean> uniform = Optional.empty();
        private Optional<Boolean> jp = Optional.empty();

        public Optional<Boolean> uniform() {
            return uniform;
        }

        public void uniform(boolean uniform) {
            uniform(Optional.of(uniform));
        }

        public void uniform(Optional<Boolean> uniform) {
            this.uniform = uniform;
        }

        public Optional<Boolean> jp() {
            return jp;
        }

        public void jp(boolean jp) {
            jp(Optional.of(jp));
        }

        public void jp(Optional<Boolean> jp) {
            this.jp = jp;
        }
    }
}
