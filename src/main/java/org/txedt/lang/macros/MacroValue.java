package org.txedt.lang.macros;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.lang.errors.TxedtThrowable;
import org.txedt.lang.interpreter.CallData;
import org.txedt.lang.interpreter.NamedCallable;
import org.txedt.lang.parser.Node;

public abstract class MacroValue implements NamedCallable {
    public final @Nullable String name;
    public abstract Object call(@NotNull CallData data, @NotNull Node.Lst args) throws TxedtThrowable;

    public MacroValue(@Nullable String name) {
        this.name = name;
    }

    @Override
    public @NotNull String name() {
        return "macro " + (name == null ? "<anon>" : name);
    }
}
