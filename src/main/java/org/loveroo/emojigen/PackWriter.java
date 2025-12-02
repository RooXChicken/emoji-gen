package org.loveroo.emojigen;

import java.io.File;

import org.loveroo.emojigen.util.FileUtil;

public class PackWriter {
    
    private static final String ASSET_PATH = "/assets/minecraft";
    
    private static final String FONT_PATH = ASSET_PATH + "/font";
    private static final String TEXTURE_PATH = ASSET_PATH + "/textures/icons";

    private static int PACK_VERSION = 64;

    private static final String PACK_MCMETA = """
    {
        "pack": {
            "pack_format": %s,
            "description": "%s"
        }
    }
    """;

    private String name = "pack";
    private String description = "emoji pack [ made with love by roo ]";

    public PackWriter() {

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

    public void build(String output, Font font) {
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

        final var fontContents = font.build(output);

        FileUtil.writeString(
            new File(output + FONT_PATH + "/default.json"),
            fontContents
        );
    }

    public static String texturePath(String output, String name) {
        return (output + TEXTURE_PATH + "/" + name);
    }
}
