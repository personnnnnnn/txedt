package org.txedt.interpreter.macros.arguments;

import org.jetbrains.annotations.NotNull;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.macros.MacroSignature;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.Node;

import java.util.List;

public final class MacroSubList extends MacroArgument {
    public @NotNull MacroSignature subSignature;

    public MacroSubList(final @NotNull MacroSignature subSignature) {
        this.subSignature = subSignature;
    }

    @Override
    public int match(@NotNull Context ctx, @NotNull List<Node> nodes, int i) {
        if (i >= nodes.size()) {
            return -1;
        }
        var node = nodes.get(i++);
        if (!(node instanceof ListNode l)) {
            return -1;
        }
        if (subSignature.match(ctx, l.children, 0) == -1) {
            return -1;
        }
        return i;
    }
}
