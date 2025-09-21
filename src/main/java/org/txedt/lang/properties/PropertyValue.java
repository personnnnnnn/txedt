package org.txedt.lang.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.lang.functions.FunctionValue;
import org.txedt.lang.interpreter.NamedCallable;

public record PropertyValue(@Nullable String name, @NotNull FunctionValue get,
                            @NotNull FunctionValue set) implements NamedCallable {

    @Override
    public @NotNull String name() {
        return "property " + (name == null ? "<anon>" : name);
    }
}
