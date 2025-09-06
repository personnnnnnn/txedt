package org.txedt.interpreter.std.vars;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.SymbolNode;

public class LetMacro extends MacroValue {
    public static @NotNull LetMacro macro = new LetMacro();
    private LetMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context context) throws TxedtThrowable {
        if (args.children.size() < 2) {
            throw new TxedtThrowable(args.bounds, "missing variable declarations");
        }

        if (!(args.children.get(1) instanceof ListNode varDefinitions)) {
            throw new TxedtThrowable(args.bounds, "missing variable declarations");
        }

        var newCtx = new Context(context);
        for (var def : varDefinitions.children) {
            switch (def) {
                case SymbolNode s -> newCtx.put(s.s, null);
                case ListNode l -> {
                    if (l.children.size() != 2) {
                        throw new TxedtThrowable(l.bounds, "variable declarations must have 2 values: the variable name and the value");
                    }
                    if (!(l.children.getFirst() instanceof SymbolNode s)) {
                        throw new TxedtThrowable(l.children.getFirst().bounds, "expected a variable name");
                    }
                    newCtx.put(s.s, Interpreter.eval(l.children.getLast(), context));
                }
                default -> throw new TxedtThrowable(def.bounds, "expected a variable declaration or name");
            }
        }

        return Interpreter.exec(args.children.subList(2, args.children.size()), newCtx);
    }
}
