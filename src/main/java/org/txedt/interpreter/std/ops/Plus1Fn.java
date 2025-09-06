package org.txedt.interpreter.std.ops;

import org.jetbrains.annotations.NotNull;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.funcs.ArgType;
import org.txedt.interpreter.funcs.FunctionSignature;
import org.txedt.interpreter.funcs.FunctionValue;

import java.util.Map;

public class Plus1Fn extends FunctionValue {
    public static @NotNull Plus1Fn func = new Plus1Fn();

    private Plus1Fn() {
        super(new FunctionSignature()
                .arg(ArgType.Normal, "x"));
    }

    @Override
    public Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        var x = args.get("x");
        if (x instanceof Long l) {
            return l + 1;
        }
        if (x instanceof Double d) {
            return d + 1;
        }
        throw new TxedtThrowable(null, "can only add 1 to number");
    }
}
