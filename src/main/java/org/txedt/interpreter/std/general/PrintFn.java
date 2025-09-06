package org.txedt.interpreter.std.general;

import org.jetbrains.annotations.NotNull;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.funcs.ArgType;
import org.txedt.interpreter.funcs.FunctionSignature;
import org.txedt.interpreter.funcs.FunctionValue;

import java.util.Map;

public class PrintFn extends FunctionValue {
    public static @NotNull PrintFn func = new PrintFn();

    private PrintFn() {
        super(new FunctionSignature()
                .arg(ArgType.Normal, "msg"));
    }

    @Override
    public Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        System.out.println(args.get("msg"));
        return null;
    }
}
