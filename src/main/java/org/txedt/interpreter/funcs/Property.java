package org.txedt.interpreter.funcs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.parser.Bounds;

import java.util.Map;

public abstract class Property extends FunctionValue {
    public Property() {
        super(new FunctionSignature()
                .arg(ArgType.Normal, "value"));
    }

    @Override
    public final @Nullable Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        var value = args.get("x");
        return get(value);
    }

    public abstract Object get(Object o) throws TxedtThrowable;
    public abstract Object setDirect(Object o, Object v) throws TxedtThrowable;
    public final Object set(Object o, Object v, Bounds bounds) throws TxedtThrowable {
        try {
            return setDirect(o, v);
        } catch (TxedtThrowable e) {
            if (e.bounds == null) {
                e.bounds = bounds;
            }
            throw e;
        }
    }
}
