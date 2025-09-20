package org.txedt.parser;

import org.jetbrains.annotations.NotNull;

public record Bounds(@NotNull Position start, @NotNull Position end) {
    public Bounds(@NotNull Position pos) {
        this(pos, pos.copy().step());
    }

    public Bounds(@NotNull Bounds start, @NotNull Bounds end) {
        this(start.start.copy(), end.end.copy());
    }
}
