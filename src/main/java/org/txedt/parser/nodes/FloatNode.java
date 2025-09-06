package org.txedt.parser.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Bounds;

public final class FloatNode extends Node {
    public final double f;
    public FloatNode(final @Nullable Bounds bounds, final double f) {
        super(bounds);
        this.f = f;
    }

    @Override
    public String toString() {
        return "" + f;
    }
}
