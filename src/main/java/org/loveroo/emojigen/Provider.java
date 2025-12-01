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
            }
            """;
    
    private final ProviderType type;

    private final List<Character> characters = new ArrayList<>();

    public Provider(ProviderType type) {
        this.type = type;
    }

    public ProviderType type() {
        return type;
    }

    public List<Character> characters() {
        return characters;
    }

    public abstract String build();

    protected String buildBase() {
        var chars = buildChars();

        return String.format(
            PROVIDER_BASE,
            type().id(),
            chars
        );
    }

    protected String buildChars() {
        var charBuilder = new StringBuilder();
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