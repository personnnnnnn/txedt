package org.txedt.interpreter.std.controlFlow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.ReturnError;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.SymbolNode;

public class ReturnFromMacro extends MacroValue {
    public static @NotNull ReturnFromMacro macro = new ReturnFromMacro();
    private ReturnFromMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        if (args.children.size() != 2 && args.children.size() != 3) {
            throw new TxedtError(args.bounds, "expected block name and optionally a return value, found nothing");
        }
        if (!(args.children.get(1) instanceof SymbolNode s)) {
            throw new TxedtError(args.children.get(1).bounds, "expected block name");
        }
        var retVal = args.children.size() == 2
                ? null
                : Interpreter.eval(args.children.getLast(), ctx);

        throw new ReturnError(args.bounds, s.s, retVal);
    }
}
