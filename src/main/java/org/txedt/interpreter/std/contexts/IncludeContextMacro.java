package org.txedt.interpreter.std.contexts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;

public class IncludeContextMacro extends MacroValue {
    public static @NotNull IncludeContextMacro macro = new IncludeContextMacro();
    private IncludeContextMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context context) throws TxedtThrowable {
        if (args.children.size() != 2) {
            throw new TxedtThrowable(args.bounds, "expected a context");
        }
        var v = Interpreter.eval(args.children.getLast(), context);
        if (!(v instanceof Context givenCtx)) {
            throw new TxedtError(args.children.getLast().bounds, "expected a context");
        }
        context.putFrom(givenCtx);
        return context;
    }
}
