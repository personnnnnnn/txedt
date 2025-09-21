package org.txedt.lang.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.lang.parser.Backtrace;

public class TxedtError extends TxedtThrowable {
    public TxedtError(@Nullable Backtrace debug, @NotNull String message) {
        super(debug, message);
    }
}
