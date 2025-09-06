package org.txedt.interpreter.funcs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.parser.Bounds;

import java.util.List;
import java.util.Map;

public abstract class FunctionValue {
    public final @NotNull FunctionSignature signature;

    public FunctionValue(final @NotNull FunctionSignature signature) {
        this.signature = signature;
    }

    public final Object call(List<Object> args, Bounds bounds) throws TxedtThrowable {
        var map = signature.mapArgs(args);
        if (map == null) {
            throw new TxedtThrowable(bounds, "invalid number of arguments");
        }

        try {
            return callFn(map);
        } catch (TxedtThrowable e) {
            if (e.bounds == null) {
                e.bounds = bounds;
            }
            throw e;
        }
    }

    public abstract @Nullable Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable;
}
