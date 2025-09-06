package org.txedt.interpreter.std.controlFlow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.ReturnError;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;

public class LoopMacro extends MacroValue {
    public static LoopMacro macro = new LoopMacro();
    private LoopMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        var rest = args.children.subList(1, args.children.size());

        while (true) {
            try {
                PrognMacro.exec(rest, ctx);
            } catch (ReturnError e) {
                if (e.target == null) {
                    return e.returnValue;
                }
                throw e;
            }
        }
    }
}
