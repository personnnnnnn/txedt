package org.txedt.interpreter.macros.arguments;

import org.jetbrains.annotations.NotNull;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.ContextPackage;
import org.txedt.interpreter.macros.MacroSignature;
import org.txedt.parser.nodes.Node;

import java.util.List;

public final class MacroOptional extends MacroArgument {
    public @NotNull MacroSignature subSignature;

    public MacroOptional(final @NotNull MacroSignature subSignature) {
        this.subSignature = subSignature;
    }

    @Override
    public int match(@NotNull Context ctx, @NotNull List<Node> nodes, int i) {
        var subPkg = new ContextPackage();
        var nextI = subSignature.match(ctx, nodes, i);
        if (nextI == -1) {
            return i;
        }
        ctx.putFrom(subPkg.vars);
        return nextI;
    }
}
