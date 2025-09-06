package org.txedt.interpreter.std.vars;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.interpreter.std.general.TrueValue;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.SymbolNode;

public class GivenMacro extends MacroValue {
    public static @NotNull GivenMacro macro = new GivenMacro();
    private GivenMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        if (args.children.size() != 2) {
            throw new TxedtError(args.bounds, "expected var name and expression");
        }
        if (!(args.children.get(1) instanceof SymbolNode s)) {
            throw new TxedtError(args.children.get(1).bounds, "expected var name");
        }
        return ctx.has(s.s) ? TrueValue.tru : null;
    }
}
