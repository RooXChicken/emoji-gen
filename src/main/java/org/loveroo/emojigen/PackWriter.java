package org.loveroo.emojigen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.loveroo.emojigen.util.FileUtil;

/**
 * Creates a resource pack from several {@link Font}s
 */
public class PackWriter {
    
    private static final String ASSET_PATH = "/assets/minecraft";
    
    private static final String FONT_PATH = ASSET_PATH + "/font";
    private static final String TEXTURE_PATH = ASSET_PATH + "/textures/icons";

    private static final String FONT_FILE_PATH = FONT_PATH + "/%s.json";

    private static int PACK_VERSION = 64;

    private String name;
    private String description;

    private final List<Font> fonts = new ArrayList<>();

    public PackWriter() {
        this("pack", "emoji pack\n[ made with love by roo ]");
    }

    public PackWriter(String name, String description) {
        name(name);
        description(description);
    }

    /**
     * Gets the pack name. Controls what the root folder of the pack is.
     * @return The name
     */
    public String name() {
        return name;
    }

    /**
     * Sets the pack name
     * @param name The new name
     */
    public void name(String name) {
        this.name = name;
    }

    /**
     * Gets the pack description. Shows up in {@code pack.mcmeta}
     * @return The description
     */
    public String description() {
        return description;
    }

    /**
     * Sets the pack description
     * @param description The new description
     */
    public void description(String description) {
        this.description = description;
    }

    /**
     * Gets a mutable list of the fonts
     * @return The fonts
     */
    public List<Font> fonts() {
        return fonts;
    }

    /**
     * Adds a font to the list. Comparable to {@code PackWriter#fonts().add(...);}
     * @param font
     */
    public void addFont(Font font) {
        fonts().add(font);
    }

    /**
     * Builds the resource pack and saves it to ./{@link PackWriter#name()}
     */
    public void build() {
        final var output = name();

        final var packFolder = new File(output);
        packFolder.delete();
        packFolder.mkdirs();

        final var fontFolder = new File(output + FONT_PATH);
        fontFolder.mkdirs();

        final var textureFolder = new File(output + TEXTURE_PATH);
        textureFolder.mkdirs();

        FileUtil.writeString(
            new File(output + "/pack.mcmeta"),
            generateMCMeta()
        );

        for(var font : fonts()) {
            final var fontContents = font.build(output);
    
            FileUtil.writeString(
                new File(output + String.format(FONT_FILE_PATH, font.name())),
                fontContents
            );
        }
    }

    /**
     * Generates the {@code pack.mcmeta} for this pack
     * @return The JSON {@code pack.mcmeta}
     */
    private String generateMCMeta() {
        try {
            final var json = new JSONObject();
    
            final var packJson = new JSONObject();
            packJson.put("pack_format", PACK_VERSION);
            packJson.put("description", description());
    
            json.put("pack", packJson);
    
            return json.toString();
        }
        catch(Exception e) {
            // exception handling is for losers
            e.printStackTrace();
            return "{}";
        }
    }

    /**
     * Helper function for getting the path a texture should output to
     * @param output The pack root folder
     * @param name The name of the texture
     * @return The path where the texture should be saved
     */
    public static String texturePath(String output, String name) {
        return (output + TEXTURE_PATH + "/" + name);
    }
}
