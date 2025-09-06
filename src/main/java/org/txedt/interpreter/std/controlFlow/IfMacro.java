package org.txedt.interpreter.std.controlFlow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;

public class IfMacro extends MacroValue {
    public static @NotNull IfMacro macro = new IfMacro();
    private IfMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        if (args.children.size() != 4) {
            throw new TxedtError(args.bounds, "expected condition, first and second value");
        }
        if (Interpreter.eval(args.children.get(1), ctx) != null) {
            return Interpreter.eval(args.children.get(2), ctx);
        } else {
            return Interpreter.eval(args.children.get(3), ctx);
        }
    }
}
