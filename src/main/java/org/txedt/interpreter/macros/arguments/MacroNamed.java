package org.txedt.interpreter.macros.arguments;

import org.jetbrains.annotations.NotNull;

public abstract non-sealed class MacroNamed extends MacroArgument {
    public @NotNull String name;

    public MacroNamed(final @NotNull String name) {
        this.name = name;
    }
}
