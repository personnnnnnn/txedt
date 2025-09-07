package org.txedt.interpreter.macros.arguments;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.txedt.interpreter.Context;
import org.txedt.parser.nodes.Node;

import java.util.List;

public final class MacroExpr extends MacroNamed {
    public MacroExpr(@NotNull String name) {
        super(name);
    }

    @Contract(pure = true)
    @Override
    public int match(@NotNull Context ctx, @NotNull List<Node> nodes, int i) {
        if (i >= nodes.size()) {
            return -1;
        }
        ctx.put(name, nodes.get(i++));
        return i;
    }
}
