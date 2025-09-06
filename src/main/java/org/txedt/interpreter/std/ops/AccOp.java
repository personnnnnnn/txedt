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

public abstract class AccOp extends FunctionValue {
    public AccOp() {
        super(new FunctionSignature()
                .arg(ArgType.Normal, "x")
                .arg(ArgType.Normal, "y")
                .arg(ArgType.Rest, "xs"));
    }

    public abstract Object op(Object a, Object b) throws TxedtThrowable;
    public abstract Object combine(Object a, Object b) throws TxedtThrowable;

    @Override
    public final @Nullable Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        var x = args.get("x");
        var y = args.get("y");
        if (!(args.get("xs") instanceof List<?> xs)) {
            throw new TxedtError(null, "internal error");
        }

        var acc = op(x, y);
        for (Object o : xs) {
            var res = op(y, o);
            y = o;
            acc = combine(acc, res);
        }

        return acc;
    }
}
