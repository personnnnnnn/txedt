package org.txedt.lang.interpreter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.lang.contexts.Context;
import org.txedt.lang.parser.Backtrace;

public record CallData(@Nullable Backtrace backtrace, @NotNull Context context) { }
