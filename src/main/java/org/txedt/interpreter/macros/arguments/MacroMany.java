package org.txedt.interpreter.macros.arguments;

import org.jetbrains.annotations.NotNull;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.ContextPackage;
import org.txedt.interpreter.macros.MacroSignature;
import org.txedt.parser.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public final class MacroMany extends MacroArgument {
    public @NotNull String name;
    public @NotNull MacroSignature subSignature;

    public MacroMany(@NotNull final String name, final @NotNull MacroSignature subSignature) {
        this.subSignature = subSignature;
        this.name = name;
    }

    @Override
    public int match(@NotNull Context ctx, @NotNull List<Node> nodes, int i) {
        if (subSignature.arguments.isEmpty()) {
            return i;
        }

        if (subSignature.arguments.size() == 1) {
            var arg = subSignature.arguments.getFirst();
            if (arg instanceof MacroNamed named) {
                List<Object> ret = new ArrayList<>();
                for (;;) {
                    var pkg = new ContextPackage();
                    var nextI = subSignature.match(pkg, nodes, i);
                    if (nextI == -1) {
                        break;
                    }
                    ret.add(pkg.vars.get(named.name));
                    i = nextI;
                }
                ctx.put(named.name, ret);
                return i;
            }
        }

        List<Object> packages = new ArrayList<>();
        for (;;) {
            var pkg = new ContextPackage();
            var nextI = subSignature.match(pkg, nodes, i);
            if (nextI == -1) {
                break;
            }
            packages.add(pkg);
            i = nextI;
        }
        ctx.put(name, packages);
        return i;
    }
}
