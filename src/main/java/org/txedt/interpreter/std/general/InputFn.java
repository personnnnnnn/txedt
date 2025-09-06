package org.txedt.interpreter.std.general;

import org.jetbrains.annotations.NotNull;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.funcs.FunctionSignature;
import org.txedt.interpreter.funcs.FunctionValue;

import java.util.Map;
import java.util.Scanner;

public class InputFn extends FunctionValue {
    public static @NotNull InputFn func = new InputFn();
    private static final Scanner scanner = new Scanner(System.in);

    private InputFn() {
        super(new FunctionSignature());
    }

    @Override
    public Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        return scanner.nextLine();
    }
}
