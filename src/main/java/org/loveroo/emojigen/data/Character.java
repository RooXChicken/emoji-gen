package org.loveroo.emojigen.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.loveroo.emojigen.util.UnicodeUtil;

/**
 * Represents a single character code
 */
public record Character(int code) {

    public String build() {
        return UnicodeUtil.codeToUnicode(code());
    }

    /**
     * Represents the grid structure of character lists
     */
    public static class CharacterList {

        private final List<List<Character>> characters = new ArrayList<>();

        public List<List<Character>> characters() {
            return characters;
        }

        public CharacterList addCharacters(List<Character> characters) {
            characters().add(characters);
            return this;
        }

        public int count() {
            var size = 0;
            for(var list : characters()) {
                size += list.size();
            }

            return size;
        }

        public JSONArray build() {
            final var json = new JSONArray();

            for(var row : characters()) {
                final var builder = new StringBuilder();

                for(var character : row) {
                    builder.append(character.build());
                }

                json.put(builder.toString());
            }

            return json;
        }
    }
}
