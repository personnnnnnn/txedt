package org.txedt.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Backtrace;

public class TxedtError extends TxedtThrowable {
    public TxedtError(@Nullable Backtrace backtrace, @NotNull String message) {
        super(backtrace, message);
    }
}
