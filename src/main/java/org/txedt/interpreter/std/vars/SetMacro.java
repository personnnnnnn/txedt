package org.txedt.interpreter.std.vars;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.funcs.Property;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.SymbolNode;

public class SetMacro extends MacroValue {
    public static @NotNull SetMacro macro = new SetMacro();
    private SetMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        if (args.children.size() != 3) {
            throw new TxedtError(args.bounds, "expected var name and expression");
        }

        if (args.children.get(1) instanceof ListNode l) {
            if (l.children.size() != 2) {
                throw new TxedtError(l.bounds, "expected property function and variable");
            }

            var propVal = Interpreter.eval(l.children.getFirst(), ctx);
            if (!(propVal instanceof Property property)) {
                throw new TxedtError(l.children.getFirst().bounds, "expected property function");
            }

            var variable = Interpreter.eval(l.children.getLast(), ctx);

            var res = Interpreter.eval(args.children.getLast(), ctx);
            return property.set(variable, res, l.bounds);
        }

        var res = Interpreter.eval(args.children.getLast(), ctx);

        if (!(args.children.get(1) instanceof SymbolNode s)) {
            throw new TxedtError(args.children.get(1).bounds, "expected var name or property");
        }

        return ctx.set(s.s, res, s.bounds);
    }
}
