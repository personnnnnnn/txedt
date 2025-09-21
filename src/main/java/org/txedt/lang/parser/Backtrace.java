package org.txedt.lang.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Backtrace(@Nullable Backtrace parent, @NotNull String description, @Nullable Bounds bounds) {
    public Backtrace(@NotNull String description, @Nullable Bounds bounds) {
        this(null, description, bounds);
    }

    public static Backtrace sameWith(@Nullable Backtrace backtrace, @Nullable Bounds bounds) {
        return backtrace == null
                ? null
                : new Backtrace(backtrace.parent, backtrace.description, bounds);
    }

    public static Backtrace sameWithParent(@Nullable Backtrace backtrace, @Nullable Bounds bounds) {
        return backtrace == null || backtrace.parent == null
                ? null
                : new Backtrace(backtrace.parent.parent, backtrace.parent.description, bounds);
    }

    public @NotNull String toString(String prefix) {
        if (parent == null) {
            return "";
        }
        String s = brief(prefix);
        s += "\n" + parent.toString(prefix);
        return s;
    }

    public @NotNull String brief() {
        return brief("");
    }

    public @NotNull String brief(String prefix) {
        String s = prefix;
        if (parent != null) {
            s += parent.description + ", ";
        }
        if (bounds != null) {
            s += "file '" + bounds.start().src + "'";
            s += ", line " + (bounds.start().ln + 1);
            s += ", column " + (bounds.start().col + 1);
        } else {
            s += "<unknown location>";
        }
        return s;
    }

    @Override
    public @NotNull String toString() {
        return toString("");
    }
}
