package org.txedt.functions;

import org.jetbrains.annotations.NotNull;

public record FunctionArgument(@NotNull String name, FunctionArgumentType type) { }
