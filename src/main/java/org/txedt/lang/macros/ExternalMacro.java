package org.txedt.macros;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.errors.TxedtThrowable;
import org.txedt.interpreter.CallData;
import org.txedt.parser.Node;

public final class ExternalMacro extends MacroValue {
    public final @NotNull MacroImpl impl;
    public ExternalMacro(@Nullable String name, @NotNull MacroImpl impl) {
        super(name);
        this.impl = impl;
    }

    @Override
    public Object call(@NotNull CallData data, Node.@NotNull Lst args) throws TxedtThrowable {
        return impl.call(data, args);
    }
}
