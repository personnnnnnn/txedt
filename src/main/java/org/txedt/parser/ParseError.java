package org.txedt.parser;

import org.jetbrains.annotations.NotNull;
import org.txedt.TxedtError;

public class ParseError extends TxedtError {
    public ParseError(@NotNull Bounds bounds, @NotNull String msg) {
        super(bounds, msg);
    }
}
