package org.txedt.interpreter.std.general;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.Bounds;
import org.txedt.parser.Position;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.Node;
import org.txedt.parser.nodes.SymbolNode;

import java.util.ArrayList;
import java.util.List;

public class AstMacro extends MacroValue {
    public static @NotNull AstMacro macro = new AstMacro();
    private AstMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context context) throws TxedtThrowable {
        if (args.children.size() != 2) {
            throw new TxedtError(args.bounds, "expected only 1 argument");
        }
        return transform(args.children.getLast(), context).getFirst();
    }

    public static @NotNull List<Node> transform(@NotNull Node node, @NotNull Context ctx) throws TxedtThrowable {
        if (node instanceof SymbolNode symbolNode) {
            return transform(symbolNode, ctx);
        }
        if (node instanceof ListNode listNode) {
            return transform(listNode, ctx);
        }
        return List.of(node);
    }

    @SuppressWarnings("unchecked")
    public static @NotNull List<Node> transform(@NotNull SymbolNode node, @NotNull Context ctx) throws TxedtThrowable {
        Position end = node.bounds == null ? null : node.bounds.end();
        Position start = node.bounds == null ? null : node.bounds.start();
        Bounds bounds = start == null ? null : new Bounds(start, end);
        if (node.s.startsWith(",@")) {
            String name = node.s.substring(2);
            if (start != null) {
                start.step();
                start.step();
            }
            var o = ctx.get(name, bounds);
            if (!(o instanceof List<?> list)) {
                throw new TxedtError(bounds, "variables with ',@' must be lists of ASTs");
            }
            for (var x : list) {
                if (!(x instanceof Node)) {
                    throw new TxedtError(bounds, "variables with ',@' must be lists of ASTs");
                }
            }
            return (List<Node>) list;
        }
        if (node.s.startsWith(",")) {
            String name = node.s.substring(1);
            if (start != null) {
                start.step();
            }
            var o = ctx.get(name, bounds);
            if (!(o instanceof Node x)) {
                throw new TxedtError(bounds, "variables with ',' must ASTs");
            }
            return List.of(x);
        }
        return List.of(node);
    }

    public static @NotNull @Unmodifiable List<Node> transform(@NotNull ListNode node, @NotNull Context ctx) throws TxedtThrowable {
        // TODO: ,(...) and ,@(...) syntax
        List<Node> nodes = new ArrayList<>();
        for (var x : node.children) {
            nodes.addAll(transform(x, ctx));
        }
        return List.of(new ListNode(node.bounds, nodes));
    }
}
