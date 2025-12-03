package org.loveroo.emojigen.providers;

import java.awt.Color;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.loveroo.emojigen.PackWriter;
import org.loveroo.emojigen.data.Character;
import org.loveroo.emojigen.util.Pair;

/**
 * A provider for using images as characters in a font
 * Images are combined into a megatexture for loading and size benefits
 */
public class BitmapProvider extends Provider {

    private static final String FILE_FORMAT = "minecraft:icons/%s.png";

    private static final Pattern NAME_REGEX = Pattern.compile("[^0-9]+");
    private static final Pattern NUMBER_SORT_REGEX = Pattern.compile("[0-9]+(?=\\.)");

    private static int unicodeValue = 0xE000;

    private final String name;

    private String imagePath = "";

    private Optional<Integer> height = Optional.empty();
    private Optional<Integer> ascent = Optional.empty();

    private Optional<Integer> manualUnicode = Optional.empty();

    public BitmapProvider(String name) {
        super(ProviderType.BITMAP);

        this.name = name;
    }

    /**
     * The name of the provider. Used as the name of the exported megatexture
     * @return The name
     */
    public String name() {
        return name;
    }

    /**
     * Gets the path this providers images are stored. Set this to the folder where your source bitmaps are stored. Each image is stiched into a megatexture
     * @return The path
     */
    public String imagePath() {
        return imagePath;
    }

    /**
     * Sets the image path. See {@link BitmapProvider#imagePath()}
     * @param imagePath The new path
     */
    public void imagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Gets the bitmap height. If empty, this will be the size of the bitmaps
     * @return The height
     */
    public Optional<Integer> height() {
        return height;
    }

    /**
     * Sets the bitmap height. See {@link BitmapProvider#height()}
     * @param height The new height
     */
    public void height(Optional<Integer> height) {
        this.height = height;
    }

    /**
     * Sets the bitmap height. See {@link BitmapProvider#height()}
     * @param height The new height
     */
    public void height(int height) {
        height(Optional.of(height));
    }

    /**
     * The ascent of the bitmaps. If empty, this will be {@link BitmapProvider#height()} - 1
     * @return
     */
    public Optional<Integer> ascent() {
        return ascent;
    }

    /**
     * Sets the bitmap ascent. See {@link BitmapProvider#height()}
     * @param ascent The new ascent
     */
    public void ascent(Optional<Integer> ascent) {
        this.ascent = ascent;
    }

    /**
     * Sets the bitmap ascent. See {@link BitmapProvider#ascent()}
     * @param ascent The new ascent
     */
    public void ascent(int ascent) {
        ascent(Optional.of(ascent));
    }

    /**
     * Sets a manual starting unicode index. If empty, the unicode is automatically managed
     * @return The unicode
     */
    public Optional<Integer> manualUnicode() {
        return manualUnicode;
    }

    /**
     * Sets the manual unicode start index. See {@link BitmapProvider#manualUnicode()}
     * @param manualUnicode The new manualUnicode
     */
    public void manualUnicode(Optional<Integer> manualUnicode) {
        this.manualUnicode = manualUnicode;
    }

    /**
     * Sets the manual unicode start index. See {@link BitmapProvider#manualUnicode()}
     * @param manualUnicode The new manualUnicode
     */
    public void manualUnicode(int manualUnicode) {
        manualUnicode(Optional.of(manualUnicode));
    }

    @Override
    public BuildResult build(String output) throws JSONException {
        TextureData textureData;
        try {
            textureData = buildMegaTexture(output);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        final var unicode = manualUnicode().orElse(unicodeValue);

        System.out.println(name() + " -> " + unicode);
        
        final var json = buildBase();
        json.put("chars", buildBitmapChars(textureData));

        if(manualUnicode().isEmpty()) {
            unicodeValue += textureData.chars().size();
        }

        final var height = height().orElse(textureData.height());
        final var ascent = ascent().orElse(height - 1);

        json.put("file", String.format(
            FILE_FORMAT,
            name()
        ));

        json.put("height", height);
        json.put("ascent", ascent);

        return new BuildResult(json);
    }

    /**
     * Builds the characters section of the provider
     * @param data The texture data
     * @return The array of characters
     * @throws JSONException
     */
    protected JSONArray buildBitmapChars(TextureData data) throws JSONException {
        final var json = new JSONArray();
        
        var charBuilder = new StringBuilder();

        for(var i = 0; i < data.chars().size(); i++) {
            final var character = data.chars().get(i);

            charBuilder.append(character.build());
            
            if(i % data.megaDimensions() == data.megaDimensions() - 1) {
                json.put(charBuilder.toString());
                charBuilder = new StringBuilder();
            }
        }

        return json;
    }

    /**
     * Gets a list of textures from the {@link BitmapProvider#imagePath()}
     * @return
     */
    private List<File> textures() {
        final var list = new File(imagePath()).listFiles();

        return Arrays.stream(list)
            .sorted((f1, f2) -> {
                // use a custom sort to have numbers sorted properly
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

    /**
     * Builds a mega texture and fills in the characters
     * @param output The root pack folder
     * @return The texture data
     * @throws IOException
     */
    protected TextureData buildMegaTexture(String output) throws IOException {
        final var loaded = new ArrayList<BufferedImage>();
        final var chars = new ArrayList<Character>();

        final var unicode = manualUnicode().orElse(unicodeValue);

        final var paths = textures();
        for(var i = 0; i < paths.size(); i++) {
            final var file = paths.get(i);

            loaded.add(ImageIO.read(file));
            chars.add(new Character(unicode + i));
        }

        if(loaded.isEmpty()) {
            return new TextureData(List.of(), 0, 0, 0);
        }

        final var textureData = createMegaTexture(chars, loaded);

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

    /**
     * Texture data from built 
     */
    public static record TextureData(List<Character> chars, int width, int height, int megaDimensions) { }

    /**
     * See {@link BitmapProvider#createMegaTexture(ArrayList, List)}
     * @param textures
     * @return
     * @throws IOException
     */
    protected Pair<BufferedImage, TextureData> createMegaTexture(List<BufferedImage> textures) throws IOException {
        return createMegaTexture(new ArrayList<>(), textures);
    }

    /**
     * Creates a megatexture from a list of images
     * @param characters A character list (used to append empty characters)
     * @param textures The list of textures to stitch together
     * @return The megatexture itself and new texture data
     * @throws IOException
     */
    protected Pair<BufferedImage, TextureData> createMegaTexture(ArrayList<Character> characters, List<BufferedImage> textures) throws IOException {
        final var chars = new ArrayList<>(characters);

        var imageWidth = 0;
        var imageHeight = 0;

        for(var texture : textures) {
            imageWidth = Math.max(imageWidth, texture.getWidth());
            imageHeight = Math.max(imageHeight, texture.getHeight());
        }

        final var imageCount = textures.size();

        final var megaDimensions = (int) Math.ceil(Math.sqrt(imageCount));
        final var glyphCount = Math.pow(megaDimensions, 2);

        final var mega = new BufferedImage(
            megaDimensions * imageWidth,
            megaDimensions * imageHeight,
            BufferedImage.TYPE_INT_ARGB
        );

        final var canvas = mega.createGraphics();

        canvas.setColor(new Color(0, 0, 0, 1));
        canvas.fillRect(
            0,
            0,
            mega.getWidth(),
            mega.getHeight()
        );

        for(var i = 0; i < glyphCount; i++) {
            if(i >= textures.size()) {
                // if there are less bitmaps than required to fit in a square, fill the remaining characters with \\u0000 (empty unicode)
                chars.add(new Character(0));
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

        return new Pair<>(mega, new TextureData(chars, imageWidth, imageHeight, megaDimensions));
    }
}