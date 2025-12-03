package org.loveroo.emojigen.providers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A font provider. Controls what the font does. See {@link ProviderType}
 */
public abstract class Provider {

    private final ProviderType type;

    public Provider(ProviderType type) {
        this.type = type;
    }

    /**
     * Gets the type of provider. See {@link ProviderType}
     * @return The type
     */
    public ProviderType type() {
        return type;
    }

    /**
     * Builds the provider
     * @param output The root folder of the pack
     * @return The result of the provider
     * @throws JSONException
     */
    public abstract BuildResult build(String output) throws JSONException;
    
    /**
     * The result of a build
     */
    public static record BuildResult(JSONObject output) { }

    /**
     * Builds a basic provider
     * @return The base provider
     * @throws JSONException
     */
    protected JSONObject buildBase() throws JSONException {
        final var json = new JSONObject();
        json.put("type", type().id());
        
        return json;
    }

    /**
     * All provider types
     */
    public static enum ProviderType {

        BITMAP("bitmap"),
        SPACE("space"),
        TTF("ttf"),
        UNIHEX("unihex"),
        REFERENCE("reference");

        private final String id;

        ProviderType(String id) {
            this.id = id;
        }

        /**
         * The ID of the provider that Minecraft expects
         * @return The ID
         */
        public String id() {
            return id;
        }
    }
}