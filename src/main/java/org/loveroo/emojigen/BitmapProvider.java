package org.loveroo.emojigen;

import ij.IJ;

public class BitmapProvider extends Provider {

    private static final String BITMAP_BASE = """
            "file": "%s",
            "height": %s,
            "ascent": %s
            """;

    private String file = "";
    private String imagePath = "";

    private int glyphSize = 16;

    private int height;
    private int ascent;

    public BitmapProvider() {
        super(ProviderType.BITMAP);
    }

    public String file() {
        return file;
    }

    public void file(String file) {
        this.file = file;
    }

    public String imagePath() {
        return imagePath;
    }

    public void imagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int glyphSize() {
        return glyphSize;
    }

    public void glyphSize(int glyphSize) {
        this.glyphSize = glyphSize;
    }

    public int height() {
        return height;
    }

    public void height(int height) {
        this.height = height;
    }

    public int ascent() {
        return ascent;
    }

    public void ascent(int ascent) {
        this.ascent = ascent;
    }

    @Override
    public String build() {
        var base = buildBase();

        var bitmap = String.format(
            BITMAP_BASE,
            file(),
            height(),
            ascent()
        );

        return String.format(
            base,
            bitmap
        );
    }

    @Override
    protected String buildChars() {
        var charBuilder = new StringBuilder();
        charBuilder.append("\"");

        var image = IJ.openImage(imagePath());
        var rowCount = image.getWidth() / glyphSize();

        for(var i = 0; i < characters().size(); i++) {
            var character = characters().get(i);

            charBuilder.append(character.build());
            
            if(i % rowCount == rowCount - 1) {
                charBuilder.append("\",\n\"");
            }
        }

        charBuilder.append("\"");
        return charBuilder.toString();
    }
}