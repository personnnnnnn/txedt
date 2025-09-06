package org.txedt.interpreter.std.controlFlow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;

public class UnlessMacro extends MacroValue {
    public static @NotNull UnlessMacro macro = new UnlessMacro();
    private UnlessMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        if (args.children.size() < 2) {
            throw new TxedtError(args.bounds, "expected condition, found nothing");
        }
        if (Interpreter.eval(args.children.get(1), ctx) == null) {
            return PrognMacro.exec(args.children.subList(2, args.children.size()), ctx);
        } else {
            return null;
        }
    }
}
