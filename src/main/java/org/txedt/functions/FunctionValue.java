package org.txedt.functions;

import org.jetbrains.annotations.NotNull;
import org.txedt.errors.TxedtThrowable;
import org.txedt.parser.Backtrace;

import java.util.List;
import java.util.Map;

public abstract class FunctionValue {
    public final @NotNull FunctionSignature signature;

    protected FunctionValue(@NotNull FunctionSignature signature) {
        this.signature = signature;
    }

    public abstract Object call(Backtrace backtrace, Map<String, Object> args) throws TxedtThrowable;

    public final Object call(Backtrace backtrace, List<Object> args) throws TxedtThrowable {
        var map = signature.mapArgs(args);
        return call(backtrace, map);
    }
}
