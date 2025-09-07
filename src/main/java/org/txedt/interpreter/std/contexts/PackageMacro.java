package org.txedt.interpreter.std.contexts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.Package;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.SymbolNode;

public class PackageMacro extends MacroValue {
    public static @NotNull PackageMacro macro = new PackageMacro();
    private PackageMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context context) throws TxedtThrowable {
        if (args.children.isEmpty() || !(args.children.get(1) instanceof SymbolNode symbol)) {
            var rest = args.children.subList(1, args.children.size());
            var pkg = new Package(context);
            Interpreter.exec(rest, pkg);
            return pkg;
        }
        var rest = args.children.subList(2, args.children.size());
        var pkg = new Package(context);
        Interpreter.exec(rest, pkg);
        return context.put(symbol.s, pkg);
    }
}
