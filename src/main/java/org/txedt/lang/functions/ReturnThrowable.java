package org.txedt.functions;

import org.jetbrains.annotations.Nullable;
import org.txedt.errors.TxedtThrowable;
import org.txedt.parser.Backtrace;

public class ReturnThrowable extends TxedtThrowable {
    public final Object returnValue;
    public final String target;

    public ReturnThrowable(@Nullable Backtrace debug, Object returnValue, String target) {
        super(debug, "cannot return outside of a context");
        this.returnValue = returnValue;
        this.target = target;
    }
}
