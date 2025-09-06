package org.txedt.interpreter.funcs;

import org.jetbrains.annotations.NotNull;

public record FunctionArg(@NotNull String name, ArgType type) {
}
