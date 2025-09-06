package org.txedt.interpreter.std.controlFlow;

import org.jetbrains.annotations.NotNull;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.ReturnError;
import org.txedt.interpreter.funcs.ArgType;
import org.txedt.interpreter.funcs.FunctionSignature;
import org.txedt.interpreter.funcs.FunctionValue;

import java.util.Map;

public class ReturnFn extends FunctionValue {
    public static @NotNull ReturnFn func = new ReturnFn();

    private ReturnFn() {
        super(new FunctionSignature()
                .arg(ArgType.Optional, "value"));
    }

    @Override
    public Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        throw new ReturnError(null, null, args.getOrDefault("value", null));
    }
}
