package org.loveroo.emojigen;

public record Character(int code) {

    private static final String CHARACTER_BASE = "\\u%s";

    public String build() {
        return String.format(CHARACTER_BASE, Integer.toHexString(code));
    }
}
