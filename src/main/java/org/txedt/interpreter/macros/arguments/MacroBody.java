package org.txedt.interpreter.macros.arguments;

import org.jetbrains.annotations.NotNull;
import org.txedt.interpreter.Context;
import org.txedt.parser.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public final class MacroBody extends MacroNamed {

    public MacroBody(@NotNull String name) {
        super(name);
    }

    @Override
    public int match(@NotNull Context ctx, @NotNull List<Node> nodes, int i) {
        if (i >= nodes.size()) {
            return -1;
        }

        List<Object> ret = new ArrayList<>();

        for (; i < nodes.size(); i++) {
            ret.add(nodes.get(i));
        }

        ctx.put(name, ret);
        return i;
    }
}
