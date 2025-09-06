package org.txedt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Bounds;

public class TxedtThrowable extends Exception {
    public @Nullable Bounds bounds;
    public @NotNull String msg;

    public TxedtThrowable(final @Nullable Bounds bounds, final @NotNull String msg) {
        super(str(bounds, msg));
        this.bounds = bounds;
        this.msg = msg;
    }

    public static @NotNull String str(final @Nullable Bounds bounds, final @NotNull String msg) {
        // TODO: implement nicer error logging
        String str = bounds == null
                ? "unknown location: "
                : "file " + bounds.start().fileName + ", line " + (bounds.start().ln + 1) + ": ";
        return str + msg;
    }
}
