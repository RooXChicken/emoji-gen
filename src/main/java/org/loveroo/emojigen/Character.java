package org.loveroo.emojigen;

public record Character(int code) {

    private static final String CHARACTER_BASE = "\\u%s";

    public String build() {
        var hex = Integer.toHexString(code);
        hex = "0".repeat(4-hex.length()) + hex;

        return String.format(
            CHARACTER_BASE,
            hex
        );
    }
}
