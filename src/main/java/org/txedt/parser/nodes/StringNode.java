package org.txedt.parser.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Bounds;

public final class StringNode extends Node {
    public final @NotNull String s;
    public StringNode(final @Nullable Bounds bounds, final @NotNull String s) {
        super(bounds);
        this.s = s;
    }

    public @NotNull String getRepr() {
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\f", "\\f")
                .replace("\0", "\\0");
    }

    @Override
    public String toString() {
        return "\"" + getRepr() + "\"";
    }
}
