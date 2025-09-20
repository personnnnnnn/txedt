package org.txedt.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Bounds;

public class TxedtThrowable extends Exception {
    public @NotNull String message;
    public @Nullable Backtrace debugData;

    public TxedtThrowable(@Nullable Backtrace debug, @NotNull String message) {
        super(message);
        this.message = message;
        this.debugData = debug;
    }

    public String getOutString() {
        return format(debugData, message);
    }

    public static @NotNull String format(@Nullable Backtrace debug, @NotNull String message) {
        String s = "";

        if (debug == null) {
            s += "<unknown location>: " + message;
        } else {
            s += debug.brief();
            s += ": " + message + "\n";
            if (debug.bounds() != null) {
                s += stringsWithArrows(debug.bounds()) + "\n";
            }
            s += debug.toString("    in ");
        }

        return s;
    }

    public static @NotNull String lineAt(@NotNull String s, int idx) {
        var start = idx;
        var end = idx;
        while (start > 0 && s.charAt(start - 1) != '\n') {
            start--;
        }
        while (end < s.length() && s.charAt(end) != '\n') {
            end++;
        }
        return s.substring(start, end);
    }

    public static @NotNull String stringsWithArrows(@NotNull Bounds bounds) {
        StringBuilder s = new StringBuilder();
        if (bounds.start().ln == bounds.end().ln) {
            s.append(lineAt(bounds.start().text, bounds.start().idx)).append('\n');
            s.append(" ".repeat(bounds.start().col)).append("^".repeat(bounds.end().col - bounds.start().col + 1));
            return s.toString();
        }

        var charI = bounds.start().idx;
        {
            var line = lineAt(bounds.start().text, charI);
            charI += line.length();
            s.append(line).append('\n');
            s.append(" ".repeat(bounds.start().col)).append("^".repeat(line.length() - bounds.start().col)).append('\n');
        }

        while (charI < bounds.end().idx) {
            var line = lineAt(bounds.start().text, charI);
            charI += line.length();
            s.append(line).append('\n');
            s.append("^".repeat(line.length())).append('\n');
        }

        return s.toString();
    }
}
