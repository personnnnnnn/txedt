package org.txedt.interpreter.std.contexts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;

public class WithContextMacro extends MacroValue {
    public static @NotNull WithContextMacro macro = new WithContextMacro();
    private WithContextMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context context) throws TxedtThrowable {
        if (args.children.size() < 2) {
            throw new TxedtThrowable(args.bounds, "expected context");
        }
        var v = Interpreter.eval(args.children.get(1), context);
        if (!(v instanceof Context givenCtx)) {
            throw new TxedtError(args.children.get(1).bounds, "expected a context");
        }
        var rest = args.children.subList(2, args.children.size());
        return Interpreter.exec(rest, givenCtx, null);
    }
}
