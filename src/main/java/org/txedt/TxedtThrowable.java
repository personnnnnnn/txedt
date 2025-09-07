package org.txedt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Bounds;

public class TxedtThrowable extends Exception {
    public @Nullable Bounds bounds;
    public @NotNull String msg;

    public TxedtThrowable(final @Nullable Bounds bounds, final @NotNull String msg) {
        super(str(bounds, msg));
        this.bounds = bounds;
        this.msg = msg;
    }

    public static @NotNull String str(final @Nullable Bounds bounds, final @NotNull String msg) {
        if (bounds == null) {
            return "unknown location: " + msg;
        }
        String s = "File " + bounds.start().fileName + ", line " + bounds.start().ln;
        s += ": " + msg + "\n";
        s += stringWithArrows(bounds, "");
        return s;
    }

    public static @NotNull String getLineAt(@NotNull String text, int idx) {
        int left = idx;
        int right = idx;
        while (left > 0 && text.charAt(left) != '\n') {
            left--;
        }
        if (left < 0 || text.charAt(left) == '\n') {
            left++;
        }
        while (right < text.length() && text.charAt(right) != '\n' && text.charAt(right) != '\r') {
            right++;
        }
        return text.substring(left, right);
    }

    public static @NotNull String stringWithArrows(@NotNull Bounds bounds, String prefix) {
        var start = bounds.start();
        var end = bounds.end();
        StringBuilder s = new StringBuilder(prefix);
        var newline = "\n" + prefix;

        if (start.ln == end.ln) {
            s.append(getLineAt(start.fileText, start.idx)).append(newline);
            s.append(" ".repeat(start.col)).append("^".repeat(end.col - start.col));
            return s.toString();
        }

        int charI = start.idx;
        {
            var line = getLineAt(start.fileText, start.idx);
            s.append(line).append(newline);
            s.append(" ".repeat(start.col)).append("^".repeat(line.length() - start.col));
            charI += line.length();
            if (charI < s.length() && s.charAt(charI) == '\r') {
                charI++;
            }
        }

        for (var lineIdx = start.ln + 1; lineIdx < end.ln; lineIdx++) {
            var line = getLineAt(start.fileText, charI);
            s.append(line).append(newline);
            s.append("^".repeat(line.length()));
            charI += line.length();
            if (charI < s.length() && s.charAt(charI) == '\r') {
                charI++;
            }
        }

        {
            var line = getLineAt(start.fileText, charI);
            s.append(line).append(newline);
            s.append("^".repeat(end.col));
        }

        return s.toString();
    }
}
