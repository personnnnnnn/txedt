package org.txedt.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Backtrace(@Nullable Backtrace parent, @Nullable String description, @Nullable Bounds bounds) {
    public Backtrace(@Nullable Bounds bounds) {
        this(null, null, bounds);
    }

    public Backtrace() {
        this(null, null, null);
    }

    public Backtrace(@Nullable Backtrace parent, @Nullable Bounds bounds) {
        this(parent, null, bounds);
    }

    public Backtrace(@Nullable String description, @Nullable Bounds bounds) {
        this(null, description, bounds);
    }

    public @NotNull String toString(String prefix) {
        String s = "";
        if (parent != null) {
            s += parent.toString(prefix) + "\n";
        }
        s += brief(prefix);
        return s;
    }

    public @NotNull String brief() {
        return brief("");
    }

    public @NotNull String brief(String prefix) {
        String s = prefix;
        if (description != null) {
            s += description + ": ";
        }
        if (bounds == null) {
            s += "<unknown location>";
        } else {
            s += "file '" + bounds.start().src + "', line " + (bounds.start().ln + 1) + ", column " + (bounds.start().col + 1);
        }
        return s;
    }

    @Override
    public @NotNull String toString() {
        return toString("");
    }
}
