package org.txedt.interpreter.std.ops;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.funcs.ArgType;
import org.txedt.interpreter.funcs.FunctionSignature;
import org.txedt.interpreter.funcs.FunctionValue;
import org.txedt.interpreter.std.general.TrueValue;

import java.util.Map;

public class NotFn extends FunctionValue {
    public static NotFn func = new NotFn();
    private NotFn() {
        super(new FunctionSignature()
                .arg(ArgType.Normal, "x"));
    }

    @Override
    public @Nullable Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        var x = args.get("x");
        return x == null ? TrueValue.tru : null;
    }
}
