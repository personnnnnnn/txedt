package org.txedt.parser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Position {
    public final @NotNull String fileText;
    public final @NotNull String fileName;
    public int idx;
    public int col;
    public int ln;

    public Position(final @NotNull String fileName, final @NotNull String fileText) {
        this.fileName = fileName;
        this.fileText = fileText;
        this.idx = 0;
        this.col = 0;
        this.ln = 0;
    }

    public Position(final @NotNull String fileName, final @NotNull String fileText, int idx, int col, int ln) {
        this.fileName = fileName;
        this.fileText = fileText;
        this.idx = idx;
        this.col = col;
        this.ln = ln;
    }

    @Contract(pure = true)
    public Position(final @NotNull Position pos) {
        this.fileName = pos.fileName;
        this.fileText = pos.fileText;
        this.idx = pos.idx;
        this.col = pos.col;
        this.ln = pos.ln;
    }

    public void step() {
        col++;
        if (fileText.charAt(idx++) == '\n') {
            ln++;
            col = 0;
        }
    }

    public char getChar() {
        return idx >= fileText.length() ? '\0' : fileText.charAt(idx);
    }

    @Override
    public String toString() {
        return "file '" + this.fileName + "', idx " + idx + ", line " + ln + ", col " + col;
    }
}
