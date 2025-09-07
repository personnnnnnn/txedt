package org.txedt.interpreter.std.vars;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.Library;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.Bounds;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.SymbolNode;

public class ExportMacro extends MacroValue {
    public static @NotNull ExportMacro macro = new ExportMacro();
    private ExportMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        if (args.children.size() < 2) {
            throw new TxedtError(args.bounds, "expected var name");
        }

        int limit = 100;
        for (; limit >= 0 && ctx != null && !(ctx instanceof Library lib); limit--) {
            ctx = ctx.parent;
        }
        if (ctx == null || limit < 0) {
            throw new TxedtError(args.bounds, "cannot export in a non-library context");
        }

        if (args.children.size() != 2) {
            var rest = args.children.subList(1, args.children.size());
            var start = rest.getFirst().bounds == null ? null : rest.getFirst().bounds.start();
            var end = rest.getLast().bounds == null ? null : rest.getLast().bounds.end();
            var bounds = start == null || end == null ? null : new Bounds(start, end);

            int i = 0;
            int symbolCount = 0;
            for (; i < rest.size() && symbolCount < 1; i++) {
                if (rest.get(i) instanceof SymbolNode) {
                    symbolCount++;
                }
            }
            if (symbolCount != 1) {
                throw new TxedtError(bounds, "variable symbol missing");
            }
            var symbol = (SymbolNode) rest.get(i);

            var node = new ListNode(bounds, rest);

            ((Library) ctx).export(symbol.s);
            return Interpreter.eval(node, ctx);
        }

        if (!(args.children.get(1) instanceof SymbolNode s)) {
            throw new TxedtError(args.children.get(1).bounds, "expected var name");
        }

        ((Library) ctx).export(s.s);
        return null;
    }
}
