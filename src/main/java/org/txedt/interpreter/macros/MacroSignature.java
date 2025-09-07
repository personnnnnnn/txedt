package org.txedt.interpreter.macros;

import org.jetbrains.annotations.NotNull;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.macros.arguments.*;
import org.txedt.parser.nodes.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * While for functions pretty much anything goes, for macros
 * the order MUST be obligatory things, then optionals, then a singular many or body.
 * I would be practically making a regex but with sexprs instead of strings.
 * TODO: make this not the case (maybe).
 */
public class MacroSignature {

    public final @NotNull List<MacroArgument> arguments = new ArrayList<>();

    public MacroSignature() { }

    public MacroSignature expr(String name) {
        arguments.add(new MacroExpr(name));
        return this;
    }

    public MacroSignature symbol(String name) {
        arguments.add(new MacroSymbol(name));
        return this;
    }

    public MacroSignature token(String token) {
        arguments.add(new MacroToken(token));

        return this;
    }

    public MacroSignature many(String name, MacroSignature subSignature) {
        arguments.add(new MacroMany(name, subSignature));
        return this;
    }

    public MacroSignature many(MacroArgument argument) {
        var sig = new MacroSignature();
        sig.arguments.add(argument);
        arguments.add(new MacroMany("", sig));
        return this;
    }

    public MacroSignature subList(MacroSignature subSignature) {
        arguments.add(new MacroSubList(subSignature));
        return this;
    }

    public MacroSignature optional(MacroSignature subSignature) {
        arguments.add(new MacroOptional(subSignature));
        return this;
    }

    public MacroSignature body(String name) {
        arguments.add(new MacroBody(name));
        return this;
    }

    public boolean matches(Context ctx, List<Node> nodes) {
        var i = match(ctx, nodes, 0);
        return i == nodes.size();
    }

    public int match(Context ctx, List<Node> nodes, int i) {
        for (var arg : arguments) {
            i = arg.match(ctx, nodes, i);
            if (i == -1) {
                return -1;
            }
        }

        return i;
    }
}
