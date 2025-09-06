package org.txedt.interpreter.std.general;

import org.jetbrains.annotations.NotNull;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.funcs.ArgType;
import org.txedt.interpreter.funcs.FunctionSignature;
import org.txedt.interpreter.funcs.FunctionValue;

import java.util.Map;

public class ListFn extends FunctionValue {
    public static @NotNull ListFn func = new ListFn();

    private ListFn() {
        super(new FunctionSignature()
                .arg(ArgType.Rest, "items"));
    }

    @Override
    public Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        return args.get("items");
    }
}
