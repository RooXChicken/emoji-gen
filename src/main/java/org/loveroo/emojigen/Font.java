package org.loveroo.emojigen;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.emojigen.providers.Provider;

/**
 * Creates a font file based on a set of providers
 */
public class Font {

    private final List<Provider> providers = new ArrayList<>();

    private String name;

    public Font() {
        this("default");
    }

    public Font(String fontFile) {
        name(fontFile);
    }

    /**
     * Gets a mutable list of the providers
     * @return The providers
     */
    public List<Provider> providers() {
        return providers;
    }

    /**
     * Adds a provider to the list. Comparable to {@code Font#providers().add(...);}
     * @param provider The provider to add
     * @return This
     */
    public Font addProvider(Provider provider) {
        providers().add(provider);
        return this;
    }

    /**
     * Gets the name of the font. Used for the name of the font file (default: {@code default<.json>})
     * @return The name
     */
    public String name() {
        return name;
    }

    /**
     * Sets the name of the font. See {@link Font#name()}
     * @param name The new name
     * @return This
     */
    public Font name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Builds the font based on its providers
     * @param output The root folder of the pack
     * @return The font JSON
     */
    public String build(String output) {
        final var json = new JSONObject();
        final var providers = new JSONArray();

        for(var provider : providers()) {
            try {
                final var result = provider.build(output);
                providers.put(result.output());
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        try {
            json.put("providers", providers);
            
            // needed becuase the JSON library force escapes backslashes even though that isn't what i want
            return json.toString().replaceAll("__BACKSLASH__", "\\\\");
        }
        catch(Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
