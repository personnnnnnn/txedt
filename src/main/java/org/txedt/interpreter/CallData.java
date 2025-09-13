package org.txedt.interpreter;

import org.jetbrains.annotations.NotNull;
import org.txedt.contexts.Context;
import org.txedt.parser.Backtrace;

public record CallData(@NotNull Backtrace backtrace, @NotNull Context context) { }
