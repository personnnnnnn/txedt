package org.txedt.functions.signature;

import org.jetbrains.annotations.NotNull;

public record FunctionArgument(@NotNull String name, FunctionArgumentType type) { }
