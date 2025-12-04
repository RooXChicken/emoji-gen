package org.loveroo.emojigen.providers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.emojigen.data.Character;

/**
 * Controls the spacing of characters
 */
public class SpaceProvider extends Provider {

    private final List<Space> spaces = new ArrayList<>();

    public SpaceProvider() {
        super(ProviderType.SPACE);
    }

    /**
     * Gets the list of spaces
     * @return The spaces
     */
    public List<Space> spaces() {
        return spaces;
    }

    /**
     * Adds a space to the list. Comparable to {@code SpaceProvider#addSpace(...);}
     * @param space The space
     * @return This
     */
    public SpaceProvider addSpace(Space space) {
        spaces().add(space);
        return this;
    }

    @Override
    public BuildResult build(String output) throws JSONException {
        final var json = buildBase();
        final var advances = new JSONObject();

        for(var space : spaces()) {
            advances.put(space.character(), space.space());
        }

        json.put("advances", advances);

        return new BuildResult(json);
    }
    
    /**
     * Stores a spacing entry
     */
    public static record Space(String character, int space) {

        public Space(Character character, int space) {
            this(character.build(), space);
        }
    }
}
