package org.txedt.interpreter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.funcs.FunctionValue;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.interpreter.std.general.TrueValue;
import org.txedt.parser.nodes.*;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    public Interpreter() { }

    public static @Nullable Object eval(@NotNull Node node, @NotNull Context ctx) throws TxedtThrowable {
        return switch (node) {
            case SymbolNode s -> {
                if (s.s.charAt(0) == '\'') {
                    yield new Symbol(s.s.substring(1));
                }
                if (s.s.equals("nil")) {
                    yield null;
                }
                if (s.s.equals("t")) {
                    yield TrueValue.tru;
                }
                yield ctx.get(s.s, s.bounds);
            }
            case StringNode s -> s.s;
            case IntNode i -> i.n;
            case FloatNode f -> f.f;
            case ListNode l -> evalList(l, ctx);
        };
    }

    @Contract(pure = true)
    private static @Nullable Object evalList(@NotNull ListNode l, @NotNull Context ctx) throws TxedtThrowable {
        if (l.children.isEmpty()) {
            return null;
        }

        var fn = eval(l.children.getFirst(), ctx);
        var limit = 100;
        for (; fn instanceof Symbol(String s) && limit >= 0; limit--) {
            fn = ctx.get(s, l.children.getFirst().bounds);
        }
        if (limit < 0) {
            throw new TxedtThrowable(l.children.getFirst().bounds, "forms a cycle of over 100 symbols pointing to each-other");
        }

        if (fn instanceof FunctionValue f) {
            List<Object> vals = new ArrayList<>(l.children.size() - 1);
            for (var i = 1; i < l.children.size(); i++) {
                vals.add(eval(l.children.get(i), ctx));
            }

            return f.call(vals, l.bounds);
        }

        if (fn instanceof MacroValue m) {
            return m.call(l, ctx);
        }

        if (fn == null) {
            throw new TxedtThrowable(l.children.getFirst().bounds, "cannot call a nil value");
        }
        throw new TxedtThrowable(l.children.getFirst().bounds, "cannot call a value of type " + fn.getClass().getSimpleName());
    }

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
}
