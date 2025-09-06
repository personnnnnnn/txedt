package org.txedt.parser.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Bounds;

public final class IntNode extends Node {
    public final long n;
    public IntNode(final @Nullable Bounds bounds, final long n) {
        super(bounds);
        this.n = n;
    }

    @Override
    public String toString() {
        return "" + n;
    }
}
