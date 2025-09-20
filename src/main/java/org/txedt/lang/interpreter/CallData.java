package org.txedt.interpreter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.contexts.Context;
import org.txedt.parser.Backtrace;

public record CallData(@Nullable Backtrace backtrace, @NotNull Context context) { }
