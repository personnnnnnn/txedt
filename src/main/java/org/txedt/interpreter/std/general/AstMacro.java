package org.txedt.interpreter.std.general;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;

public class AstMacro extends MacroValue {
    public static @NotNull AstMacro macro = new AstMacro();
    private AstMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context context) throws TxedtThrowable {
        if (args.children.size() != 2) {
            throw new TxedtThrowable(args.bounds, "expected 1 argument, not " + (args.children.size() - 1));
        }
        return args.children.getLast();
    }
}
