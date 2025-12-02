package org.loveroo.emojigen;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.emojigen.providers.Provider;

public class Font {
    
    private static final String FONT_BASE = """
    {
        "providers": [
    %s
        ]
    }
    """;
    
    private final List<Provider> providers = new ArrayList<>();

    private String name;
    private int unicodeStart = 0xE000;

    public Font() {
        this("default");
    }

    public Font(String fontFile) {
        fontFile(fontFile);
    }

    public List<Provider> providers() {
        return providers;
    }

    public void addProvider(Provider provider) {
        providers().add(provider);
    }

    public String name() {
        return name;
    }

    public void fontFile(String fontFile) {
        this.name = fontFile;
    }

    public int unicodeStart() {
        return unicodeStart;
    }

    public void unicodeStart(int unicodeStart) {
        this.unicodeStart = unicodeStart;
    }

    public String build(String output) {
        final var providerBuilder = new StringBuilder();
        var unicode = unicodeStart();

        for(var i = 0; i < providers().size(); i++) {
            final var provider = providers().get(i);
            
            final var result = provider.build(output, unicode);
            unicode += result.icons();

            providerBuilder.append(result.output());

            if(i != providers().size()-1) {
                providerBuilder.append(",\n");
            }
        }

        return String.format(FONT_BASE, providerBuilder);
    }
}
