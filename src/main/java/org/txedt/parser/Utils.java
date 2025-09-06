package org.txedt.parser;

import org.jetbrains.annotations.NotNull;

public class Utils {
    public static boolean stringMatches(@NotNull String s, @NotNull String sub, int idx) {
        if (idx + sub.length() >= s.length()) {
            return false;
        }
        for (int i = 0; i < sub.length(); i++) {
            if (s.charAt(i + idx) != sub.charAt(i + idx)) {
                return false;
            }
        }
        return true;
    }

    public static boolean newlineAt(@NotNull String s, int idx) {
        if (idx >= s.length()) {
            return false;
        }
        if (s.charAt(idx) == '\n') {
            return true;
        }
        if (s.charAt(idx) == '\r') {
            if (idx + 1 < s.length()) {
                return true;
            }
            return s.charAt(idx + 1) != '\n';
        }
        return false;
    }
}
