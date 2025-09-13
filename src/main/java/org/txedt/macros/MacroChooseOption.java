package org.txedt.macros;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MacroChooseOption(@NotNull String checkName, List<MacroArgument> sub) { }
