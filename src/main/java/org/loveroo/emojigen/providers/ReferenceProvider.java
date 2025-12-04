package org.loveroo.emojigen.providers;

import org.json.JSONException;

/**
 * Includes another font JSON file
 */
public class ReferenceProvider extends Provider {

    private String id;

    public ReferenceProvider(String id) {
        super(ProviderType.REFERENCE);
        this.id = id;
    }

    public ReferenceProvider() {
        this("");
    }

    /**
     * Gets the resource location to the other font file
     * @return The location
     */
    public String id() {
        return id;
    }

    /**
     * Sets the resource location. See {@link ReferenceProvider#id()}
     * EX: {@code minecraft:include/default}
     * @param id The new location
     * @return This
     */
    public ReferenceProvider id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public BuildResult build(String output) throws JSONException {
        final var json = buildBase();
        json.put("id", id());

        return new BuildResult(json);
    }
}
