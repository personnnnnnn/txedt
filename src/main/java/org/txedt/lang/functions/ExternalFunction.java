package org.txedt.functions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.errors.TxedtThrowable;
import org.txedt.functions.signature.ExternalFunctionImpl;
import org.txedt.functions.signature.FunctionSignature;
import org.txedt.parser.Backtrace;

import java.util.Map;

public final class ExternalFunction extends FunctionValue {
    public final @NotNull ExternalFunctionImpl impl;
    public ExternalFunction(@Nullable String name, @NotNull FunctionSignature signature, @NotNull ExternalFunctionImpl impl) {
        super(name, signature);
        this.impl = impl;
    }

    @Override
    public @Nullable Object call(Backtrace backtrace, Map<String, Object> args) throws TxedtThrowable {
        return impl.call(backtrace, args);
    }
}
