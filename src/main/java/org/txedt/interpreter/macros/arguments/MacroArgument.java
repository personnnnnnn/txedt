package org.txedt.interpreter.macros.arguments;

import org.jetbrains.annotations.NotNull;
import org.txedt.interpreter.Context;
import org.txedt.parser.nodes.Node;

import java.util.List;

public sealed abstract class MacroArgument
        permits MacroMany, MacroNamed, MacroOptional, MacroSubList, MacroToken {

    // returns -1 when failure, returns the next index when successful
    public abstract int match(@NotNull Context ctx, @NotNull List<Node> nodes, int i);
}
