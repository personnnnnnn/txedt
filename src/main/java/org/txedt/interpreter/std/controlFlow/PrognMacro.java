package org.txedt.interpreter.std.controlFlow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.ReturnError;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.Node;

import java.util.List;

public class PrognMacro extends MacroValue {
    public static @NotNull PrognMacro macro = new PrognMacro();
    private PrognMacro() { }

    // exec assumes that no return will have the target "", and instead will put null
    // this means that this will be skipped
    public static @Nullable Object exec(@NotNull List<Node> nodes, @NotNull Context context) throws TxedtThrowable {
        return exec(nodes, context, "");
    }

    public static @Nullable Object exec(@NotNull List<Node> nodes, @NotNull Context context, @Nullable String targetName) throws TxedtThrowable {
        if (nodes.isEmpty()) {
            return null;
        }

        for (int i = 0; i < nodes.size() - 1; i++) {
            try {
                Interpreter.eval(nodes.get(i), context);
            } catch (ReturnError e) {
                boolean matchesTarget = targetName == null || e.target != null && e.target.equals(targetName);
                if (matchesTarget) {
                    return e.returnValue;
                } else {
                    throw e;
                }
            }
        }

        try {
            return Interpreter.eval(nodes.getLast(), context);
        } catch (ReturnError e) {
            boolean matchesTarget = targetName == null
                    ? e.target == null
                    : e.target != null && e.target.equals(targetName);
            if (matchesTarget) {
                return e.returnValue;
            } else {
                throw e;
            }
        }
    }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        return exec(args.children.subList(1, args.children.size()), ctx, null);
    }
}
