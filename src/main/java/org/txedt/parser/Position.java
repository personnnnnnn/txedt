package org.txedt.parser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Position {
    public int col, ln, idx;
    public @NotNull String src, text;

    public Position(@NotNull String src, @NotNull String text, int col, int ln, int idx) {
        this.src = src;
        this.text = text;
        this.col = col;
        this.ln = ln;
        this.idx = idx;
    }

    public Position(@NotNull String src, @NotNull String text) {
        this(src, text, 0, 0, 0);
    }

    public Position(@NotNull Position position) {
        this(position.src, position.text, position.col, position.ln, position.idx);
    }

    @Contract(" -> new")
    public @NotNull Position copy() {
        return new Position(this);
    }

    public char getChar() {
        return idx >= text.length() ? '\0' : text.charAt(idx);
    }

    public Position step() {
        switch (getChar()) {
            case '\0' -> { }
            case '\n' -> {
                idx++;
                ln++;
                col = 0;
            }
            default -> {
                idx++;
                col++;
            }
        }
        return this;
    }
}
