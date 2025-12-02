package org.loveroo.emojigen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.loveroo.emojigen.util.FileUtil;

public class PackWriter {
    
    private static final String ASSET_PATH = "/assets/minecraft";
    
    private static final String FONT_PATH = ASSET_PATH + "/font";
    private static final String TEXTURE_PATH = ASSET_PATH + "/textures/icons";

    private static final String FONT_FILE_PATH = FONT_PATH + "/%s.json";

    private static int PACK_VERSION = 64;

    private static final String PACK_MCMETA = """
    {
        "pack": {
            "pack_format": %s,
            "description": "%s"
        }
    }
    """;

    private String name;
    private String description;

    private final List<Font> fonts = new ArrayList<>();

    public PackWriter() {
        this("pack", "emoji pack [ made with love by roo ]");
    }

    public PackWriter(String name, String description) {
        name(name);
        description(description);
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public String description() {
        return description;
    }

    public void description(String description) {
        this.description = description;
    }

    public List<Font> fonts() {
        return fonts;
    }

    public void addFont(Font font) {
        fonts().add(font);
    }

    public void build() {
        final var output = name();

        var packFolder = new File(output);
        packFolder.mkdirs();

        var fontFolder = new File(output + FONT_PATH);
        fontFolder.mkdirs();

        var textureFolder = new File(output + TEXTURE_PATH);
        textureFolder.mkdirs();

        FileUtil.writeString(
            new File(output + "/pack.mcmeta"),
            String.format(
                PACK_MCMETA,
                PACK_VERSION,
                description()
            )
        );

        for(var font : fonts()) {
            final var fontContents = font.build(output);
    
            FileUtil.writeString(
                new File(output + String.format(FONT_FILE_PATH, font.name())),
                fontContents
            );
        }
    }

    public static String texturePath(String output, String name) {
        return (output + TEXTURE_PATH + "/" + name);
    }
}
