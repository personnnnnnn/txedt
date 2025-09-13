package org.txedt.parser;

import org.jetbrains.annotations.NotNull;
import org.txedt.Context;

public record CallData(@NotNull Backtrace backtrace, @NotNull Context context) { }
