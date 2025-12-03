package org.loveroo.emojigen.data;

import org.loveroo.emojigen.util.UnicodeUtil;

/**
 * Represents a single character code
 */
public record Character(int code) {

    public String build() {
        return UnicodeUtil.codeToUnicode(code());
    }
}
