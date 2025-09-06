package org.txedt.parser.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Bounds;

public final class SymbolNode extends Node {
    public final @NotNull String s;
    public SymbolNode(final @Nullable Bounds bounds, final @NotNull String s) {
        super(bounds);
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
