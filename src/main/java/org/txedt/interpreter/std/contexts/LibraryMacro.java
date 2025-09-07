package org.txedt.interpreter.std.contexts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Library;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.SymbolNode;

public class LibraryMacro extends MacroValue {
    public static @NotNull LibraryMacro macro = new LibraryMacro();
    private LibraryMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context context) throws TxedtThrowable {
        if (args.children.isEmpty() || !(args.children.get(1) instanceof SymbolNode symbol)) {
            var rest = args.children.subList(1, args.children.size());
            return new Library(context, rest);
        }
        var rest = args.children.subList(2, args.children.size());
        var lib = new Library(context, rest);
        return context.put(symbol.s, lib);
    }
}
