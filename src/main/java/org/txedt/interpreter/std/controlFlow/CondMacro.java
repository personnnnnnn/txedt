package org.txedt.interpreter.std.controlFlow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;

public class CondMacro extends MacroValue {
    public static @NotNull CondMacro macro = new CondMacro();
    private CondMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        for (int i = 1; i < args.children.size(); i++) {
            if (!(args.children.get(i) instanceof ListNode l)) {
                throw new TxedtError(args.children.get(i).bounds, "expected list of condition and one or more expressions");
            }
            if (l.children.isEmpty()) {
                throw new TxedtError(l.bounds, "expected list of condition and one or more expressions");
            }
            var cond = Interpreter.eval(l.children.getFirst(), ctx);
            if (cond == null) {
                continue;
            }
            var rest = l.children.subList(1, l.children.size());
            return Interpreter.exec(rest, ctx);
        }
        return null;
    }
}
