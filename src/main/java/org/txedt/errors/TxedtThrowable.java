package org.txedt.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Bounds;

public class TxedtThrowable extends Exception {
    public @NotNull String message;
    public @Nullable Backtrace backtrace;

    public TxedtThrowable(@Nullable Backtrace backtrace, @NotNull String message) {
        super(message);
        this.message = message;
        this.backtrace = backtrace;
    }

    public String getOutString() {
        return format(backtrace, message);
    }

    public static @NotNull String format(@Nullable Backtrace backtrace, @NotNull String message) {
        String s = "";
        if (backtrace == null) {
            s += "from unknown location: " + message;
        } else {
            s += backtrace.brief() + ": " + message;
            if (backtrace.bounds() != null) {
                s += "\n" + stringsWithArrows(backtrace.bounds(), "    ");
            }
            if (backtrace.parent() != null) {
                s += "\n" + backtrace.toString("    at ");
            }
        }


        return s;
    }

    public static @NotNull String lineAt(String s, int idx) {
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

    public static @NotNull String stringsWithArrows(@NotNull Bounds bounds, String prefix) {
        var newline = "\n" + prefix;
        StringBuilder s = new StringBuilder();
        if (bounds.start().ln == bounds.end().ln) {
            s.append(lineAt(bounds.start().text, bounds.start().idx)).append("\n");
            s.append(" ".repeat(bounds.start().col)).append("^".repeat(bounds.end().col - bounds.start().col + 1));
            return s.toString();
        }

        var charI = bounds.start().idx;
        {
            var line = lineAt(bounds.start().text, charI);
            charI += line.length() + 1;
            s.append(line).append(newline);
            s.append(" ".repeat(bounds.start().col)).append("^".repeat(line.length() - bounds.start().col));
        }

        while (charI < bounds.end().idx) {
            var line = lineAt(bounds.start().text, charI);
            charI += line.length() + 1;
            s.append(line).append(newline);
            s.append("^".repeat(line.length()));
        }

        {
            var line = lineAt(bounds.start().text, charI);
            s.append(line).append(newline);
            s.append("^".repeat(bounds.end().col));
        }

        return s.toString();
    }
}
