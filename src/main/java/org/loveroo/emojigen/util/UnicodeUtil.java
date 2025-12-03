package org.loveroo.emojigen.util;

/**
 * Utility class for Unicode and character codes
 */
public class UnicodeUtil {
    
    private static final String BASE = "__BACKSLASH__u%s";

    /**
     * Converts a Unicode codepoint to a \\uXXXX string
     * @param code The codepoint
     * @return The Unicode string
     */
    public static String codeToUnicode(int code) {
        var hex = Integer.toHexString(code);
        hex = "0".repeat(4-hex.length()) + hex;

        return String.format(
            BASE,
            hex
        );
    }
}
