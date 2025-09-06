package org.txedt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Bounds;

public class TxedtError extends TxedtThrowable {
    public TxedtError(@Nullable Bounds bounds, @NotNull String msg) {
        super(bounds, msg);
    }
}
