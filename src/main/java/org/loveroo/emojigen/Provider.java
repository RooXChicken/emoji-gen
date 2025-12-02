package org.loveroo.emojigen;

import java.util.ArrayList;
import java.util.List;

public abstract class Provider {

    private static final String PROVIDER_BASE = """
            {
                "type": "%s",
                "chars": [
    %s
                ],
    %s
            }""";
    
    private final ProviderType type;

    private int unicodeOffset = 0;
    private final List<Character> characters = new ArrayList<>();

    public Provider(ProviderType type) {
        this.type = type;
    }

    public ProviderType type() {
        return type;
    }

    public int unicodeOffset() {
        return unicodeOffset;
    }

    public void unicodeOffset(int unicodeStart) {
        this.unicodeOffset = unicodeStart;
    }

    public List<Character> characters() {
        return characters;
    }

    public int characterCount() {
        return characters().size();
    }

    public static record BuildResult(String output, int icons) { }

    public abstract BuildResult build(String output, int unicodeStart);

    protected String buildBase() {
        final var chars = buildChars();

        return String.format(
            PROVIDER_BASE,
            type().id(),
            chars,
            "%s"
        );
    }

    protected String buildChars() {
        final var charBuilder = new StringBuilder();
        charBuilder.append("\"");

        for(var character : characters()) {
            charBuilder.append(character.build());
        }

        charBuilder.append("\"");
        return charBuilder.toString();
    }

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

        public String id() {
            return id;
        }
    }
}