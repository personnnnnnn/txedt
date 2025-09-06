package org.txedt.interpreter;

import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.parser.Bounds;

public class ReturnError extends TxedtThrowable {
    public final @Nullable String target;
    public final Object returnValue;

    public ReturnError(final @Nullable Bounds bounds, final @Nullable String target, final Object returnValue) {
        super(bounds, target == null
                ? "return outside of block"
                : "there is no block named '" + target + "'");
        this.target = target;
        this.returnValue = returnValue;
    }
}
