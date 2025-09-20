package org.txedt.functions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.errors.TxedtThrowable;
import org.txedt.functions.signature.FunctionSignature;
import org.txedt.interpreter.NamedCallable;
import org.txedt.parser.Backtrace;

import java.util.List;
import java.util.Map;

public abstract class FunctionValue implements NamedCallable {
    public final @NotNull FunctionSignature signature;
    public final @Nullable String name;

    public FunctionValue(@Nullable String name, @NotNull FunctionSignature signature) {
        this.signature = signature;
        this.name = name;
    }

    public FunctionValue(@NotNull FunctionSignature signature) {
        this.signature = signature;
        name = null;
    }

    public abstract Object call(Backtrace debug, Map<String, Object> args) throws TxedtThrowable;

    public final Object call(Backtrace debug, List<Object> args) throws TxedtThrowable {
        var map = signature.mapArgs(args, debug);
        return call(debug, map);
    }

    @Override
    public @NotNull String name() {
        return "function " + (name == null ? "<anon>" : name);
    }
}
