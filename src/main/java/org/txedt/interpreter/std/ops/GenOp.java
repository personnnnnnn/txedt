package org.txedt.interpreter.std.ops;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.funcs.ArgType;
import org.txedt.interpreter.funcs.FunctionSignature;
import org.txedt.interpreter.funcs.FunctionValue;

import java.util.List;
import java.util.Map;

public abstract class GenOp extends FunctionValue {
    public GenOp() {
        super(new FunctionSignature()
                .arg(ArgType.Normal, "x")
                .arg(ArgType.Rest, "xs"));
    }

    public abstract Object op(Object a, Object b) throws TxedtThrowable;
    public Object single(Object x) throws TxedtThrowable {
        return x;
    }

    @Override
    public @Nullable Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        var x = args.get("x");
        if (!(args.get("xs") instanceof List<?> xs)) {
            throw new TxedtError(null, "internal error");
        }
        if (xs.isEmpty()) {
            return single(x);
        }
        var acc = x;
        for (var val : xs) {
            acc = op(acc, val);
        }
        return acc;
    }
}
