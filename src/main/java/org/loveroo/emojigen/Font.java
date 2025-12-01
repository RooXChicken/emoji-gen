package org.loveroo.emojigen;

import java.util.ArrayList;
import java.util.List;

public class Font {
    
    private static final String FONT_BASE = """
            {
                "providers": [
                    %s
                ]
            }
            """;
    
    private final List<Provider> providers = new ArrayList<>();

    public Font() {

    }

    public List<Provider> providers() {
        return providers;
    }

    public void addProvider(Provider provider) {
        providers().add(provider);
    }

    public String build() {
        var providerBuilder = new StringBuilder();

        for(var i = 0; i < providers().size(); i++) {
            var provider = providers().get(i);
            
            providerBuilder.append(provider.build());

            if(i != providers().size()-1) {
                providerBuilder.append(",\n");
            }
        }

        return String.format(FONT_BASE, providerBuilder);
    }
}
