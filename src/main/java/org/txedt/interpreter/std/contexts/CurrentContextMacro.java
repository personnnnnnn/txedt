package org.txedt.interpreter.std.contexts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;

public class CurrentContextMacro extends MacroValue {
    public static @NotNull CurrentContextMacro macro = new CurrentContextMacro();
    private CurrentContextMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context context) throws TxedtThrowable {
        if (args.children.size() != 1) {
            throw new TxedtThrowable(args.bounds, "expected no arguments");
        }
        return context;
    }
}
