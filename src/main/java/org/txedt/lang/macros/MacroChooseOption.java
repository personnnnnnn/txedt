package org.txedt.lang.macros;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MacroChooseOption(@NotNull String checkName, List<MacroArgument> sub) { }
