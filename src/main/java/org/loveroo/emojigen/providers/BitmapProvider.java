package org.loveroo.emojigen.providers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.loveroo.emojigen.PackWriter;
import org.loveroo.emojigen.data.Character;
import org.loveroo.emojigen.util.Pair;

public class BitmapProvider extends Provider {

    private static final String CHAR_TAB_SIZE = "               ";

    private static final String BITMAP_BASE = """
                "file": "%s",
                "height": %s,
                "ascent": %s
    """;

    private static final String FILE_FORMAT = "minecraft:icons/%s.png";

    private static final Pattern NUMBER_SORT_REGEX = Pattern.compile("[0-9]+(?=\\.)");
    private static final Pattern NAME_REGEX = Pattern.compile("[^0-9]+");

    private final String name;

    private String imagePath = "";

    private Optional<Integer> height = Optional.empty();
    private Optional<Integer> ascent = Optional.empty();

    public BitmapProvider(String name) {
        super(ProviderType.BITMAP);

        this.name = name;
    }

    public String name() {
        return name;
    }

    public String imagePath() {
        return imagePath;
    }

    public void imagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Optional<Integer> height() {
        return height;
    }

    public void height(Optional<Integer> height) {
        this.height = height;
    }

    public void height(int height) {
        height(Optional.of(height));
    }

    public Optional<Integer> ascent() {
        return ascent;
    }

    public void ascent(Optional<Integer> ascent) {
        this.ascent = ascent;
    }

    public void ascent(int ascent) {
        ascent(Optional.of(ascent));
    }

    @Override
    public BuildResult build(String output, int unicodeStart) {
        TextureData textureData;
        try {
            textureData = buildMegaTexture(output, unicodeStart);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(name() + " -> " + unicodeStart);
        
        final var base = buildBase(buildBitmapChars(textureData));

        final var height = height().orElse(textureData.height());
        final var ascent = ascent().orElse(height - 1);

        final var bitmap = String.format(
            BITMAP_BASE,
            String.format(
                FILE_FORMAT,
                name()
            ),
            height,
            ascent
        );

        return new BuildResult(
            String.format(
                base,
                bitmap
            ),
            characterCount()
        );
    }

    protected String buildBitmapChars(TextureData data) {
        final var charBuilder = new StringBuilder();
        charBuilder.append(CHAR_TAB_SIZE);
        charBuilder.append("\"");

        for(var i = 0; i < characters().size(); i++) {
            final var character = characters().get(i);

            charBuilder.append(character.build());
            
            if(i % data.megaDimensions() == data.megaDimensions() - 1 && i != characters().size() - 1) {
                charBuilder.append("\",\n");
                charBuilder.append(CHAR_TAB_SIZE);
                charBuilder.append("\"");
            }
        }

        charBuilder.append("\"");
        return charBuilder.toString();
    }

    private List<File> textures() {
        final var list = new File(imagePath()).listFiles();

        return Arrays.stream(list)
            .sorted((f1, f2) -> {
                // use a custom sort to have numbers ordered properly
                final var name1Match = NAME_REGEX.matcher(f1.getName());
                final var name2Match = NAME_REGEX.matcher(f2.getName());

                if(!name1Match.find() || !name2Match.find()) {
                    return f1.getName().compareTo(f2.getName());
                }
                
                final var name1 = name1Match.group();
                final var name2 = name2Match.group();

                if(!name1.equalsIgnoreCase(name2)) {
                    return f1.getName().compareTo(f2.getName());
                }

                final var num1Match = NUMBER_SORT_REGEX.matcher(f1.getName());
                final var num2Match = NUMBER_SORT_REGEX.matcher(f2.getName());
                
                if(!num1Match.find() || !num2Match.find()) {
                    return f1.getName().compareTo(f2.getName());
                }

                final var num1 = Integer.parseInt(num1Match.group());
                final var num2 = Integer.parseInt(num2Match.group());

                return Integer.compare(num1, num2);
            })
            .toList();
    }

    protected TextureData buildMegaTexture(String output, int unicodeStart) throws IOException {
        final var loaded = new ArrayList<BufferedImage>();

        final var paths = textures();
        for(var i = 0; i < paths.size(); i++) {
            final var file = paths.get(i);

            loaded.add(ImageIO.read(file));
            characters().add(new Character(unicodeStart + unicodeOffset() + i));
        }

        if(loaded.isEmpty()) {
            return new TextureData(0, 0, 0);
        }

        final var textureData = createMegaTexture(loaded);

        final var writer = new FileOutputStream(
            new File(PackWriter.texturePath(output, name() + ".png"))
        );
        
        ImageIO.write(
            textureData.first(),
            "PNG",
            writer
        );

        writer.close();

        return textureData.second();
    }

    public static record TextureData(int width, int height, int megaDimensions) { }

    protected Pair<BufferedImage, TextureData> createMegaTexture(List<BufferedImage> textures) throws IOException {
        final var imageWidth = textures.get(0).getWidth();
        final var imageHeight = textures.get(0).getHeight();

        final var imageCount = textures.size();

        final var megaDimensions = (int) Math.ceil(Math.sqrt(imageCount));
        final var glyphCount = Math.pow(megaDimensions, 2);

        final var mega = new BufferedImage(
            megaDimensions * imageWidth,
            megaDimensions * imageHeight,
            BufferedImage.TYPE_INT_ARGB
        );

        final var canvas = mega.createGraphics();

        for(var i = 0; i < glyphCount; i++) {
            if(i >= textures.size()) {
                characters().add(new Character(0));
                continue;
            }

            var image = textures.get(i);

            var x = (i % megaDimensions);
            var y = (i / megaDimensions);

            canvas.drawImage(
                image,
                null,
                x * imageWidth,
                y * imageHeight
            );
        }

        canvas.dispose();

        return new Pair<>(mega, new TextureData(imageWidth, imageHeight, megaDimensions));
    }
}