package org.txedt.interpreter.macros.arguments;

import org.jetbrains.annotations.NotNull;
import org.txedt.interpreter.Context;
import org.txedt.parser.nodes.Node;
import org.txedt.parser.nodes.SymbolNode;

import java.util.List;

public final class MacroSymbol extends MacroNamed {
    public MacroSymbol(@NotNull String name) {
        super(name);
    }

    @Override
    public int match(@NotNull Context ctx, @NotNull List<Node> nodes, int i) {
        if (i >= nodes.size()) {
            return -1;
        }
        if (!(nodes.get(i++) instanceof SymbolNode s)) {
            return -1;
        }
        ctx.put(name, s.s);
        return i;
    }
}
