package org.txedt.interpreter.std.general;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.macros.MacroSignature;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.interpreter.macros.UserMacro;
import org.txedt.parser.Bounds;
import org.txedt.parser.Position;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.Node;
import org.txedt.parser.nodes.SymbolNode;

import java.util.List;

public class MacroMacro extends MacroValue {
    public static MacroMacro macro = new MacroMacro();
    private MacroMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        if (args.children.size() < 2) {
            throw new TxedtError(args.bounds, "expected macro name name (optional) and pattern");
        }

        int argsI = 1;
        if (args.children.get(1) instanceof SymbolNode symbol) {
            argsI++;
        }
        if (!(args.children.get(argsI) instanceof ListNode arguments)) {
            throw new TxedtError(args.children.get(2).bounds, "expected pattern");
        }
        argsI++;
        List<Node> rest = args.children.subList(argsI, args.children.size());

        MacroSignature signature = new MacroSignature();
        parse(signature, arguments.children, 0);

        var mac = new UserMacro(signature, rest, ctx);

        if (args.children.get(1) instanceof SymbolNode symbol) {
            ctx.put(symbol.s, mac);
        }

        return mac;
    }

    private static int parse(MacroSignature sig, @NotNull List<Node> nodes, int i) throws TxedtThrowable {
        if (i >= nodes.size()) {
            return i;
        }
        if (nodes.get(i) instanceof ListNode listNode) {
            var subSig = new MacroSignature();
            parse(subSig, listNode.children, 0);
            sig.subList(subSig);
            return parse(sig, nodes, i + 1);
        }
        if (!(nodes.get(i) instanceof SymbolNode symbol)) {
            throw new TxedtError(nodes.get(i).bounds, "expected symbol");
        }
        return switch (symbol.s) {
            case "&body" -> parseBody(sig, nodes, i + 1);
            case "&optional" -> parseOptional(sig, nodes, i + 1);
            case "&maybe" -> parseMaybe(sig, nodes, i + 1);
            case "&many" -> parseMany(sig, nodes, i + 1);
            case "&more" -> parseMore(sig, nodes, i + 1);
            case "&symbol" -> parseSymbol(sig, nodes, i + 1);
            case "&token" -> parseToken(sig, nodes, i + 1);
            default -> {
                sig.expr(symbol.s);
                yield parse(sig, nodes, i + 1);
            }
        };
    }

    private static int parseBody(MacroSignature sig, @NotNull List<Node> nodes, int i) throws TxedtThrowable {
        if (i >= nodes.size()) {
            var start = nodes.getLast().bounds == null ? null : new Position(nodes.getLast().bounds.end());
            var end = start == null ? null : new Position(start);
            if (end != null) {
                end.step();
            }
            var errorBounds = new Bounds(start, end);
            throw new TxedtError(errorBounds, "expected symbol");
        }
        if (!(nodes.get(i) instanceof SymbolNode symbol)) {
            throw new TxedtError(nodes.get(i).bounds, "expected symbol");
        }
        if (symbol.s.startsWith("&")) {
            throw new TxedtError(symbol.bounds, "expected name");
        }
        sig.body(symbol.s);
        return parse(sig, nodes, i + 1);
    }

    private static int parseOptional(@NotNull MacroSignature sig, @NotNull List<Node> nodes, int i) throws TxedtThrowable {
        var subSig = new MacroSignature();
        i = parse(subSig, nodes, i);
        sig.optional(subSig);
        return parse(sig, nodes, i + 1);
    }

    private static int parseMaybe(@NotNull MacroSignature sig, @NotNull List<Node> nodes, int i) throws TxedtThrowable {
        if (i >= nodes.size()) {
            var start = nodes.getLast().bounds == null ? null : new Position(nodes.getLast().bounds.end());
            var end = start == null ? null : new Position(start);
            if (end != null) {
                end.step();
            }
            var errorBounds = new Bounds(start, end);
            throw new TxedtError(errorBounds, "expected symbol");
        }
        if (!(nodes.get(i) instanceof ListNode listNode)) {
            throw new TxedtError(nodes.get(i).bounds, "expected list");
        }
        var subSig = new MacroSignature();
        parse(subSig, listNode.children, 0);
        sig.optional(subSig);
        return parse(sig, nodes, i + 1);
    }

    private static int parseMany(@NotNull MacroSignature sig, @NotNull List<Node> nodes, int i) throws TxedtThrowable {
        var subSig = new MacroSignature();
        i = parse(subSig, nodes, i);
        sig.many("", subSig);
        return parse(sig, nodes, i + 1);
    }

    private static int parseMore(@NotNull MacroSignature sig, @NotNull List<Node> nodes, int i) throws TxedtThrowable {
        if (i + 1 >= nodes.size()) {
            var start = nodes.getLast().bounds == null ? null : new Position(nodes.getLast().bounds.end());
            var end = start == null ? null : new Position(start);
            if (end != null) {
                end.step();
            }
            var errorBounds = new Bounds(start, end);
            throw new TxedtError(errorBounds, "expected symbol");
        }
        if (!(nodes.get(i) instanceof SymbolNode symbol)) {
            throw new TxedtError(nodes.get(i).bounds, "expected symbol");
        }
        if (!(nodes.get(i + 1) instanceof ListNode listNode)) {
            throw new TxedtError(nodes.get(i + 1).bounds, "expected list");
        }
        var subSig = new MacroSignature();
        parse(subSig, listNode.children, 0);
        sig.many(symbol.s, subSig);
        return parse(sig, nodes, i + 2);
    }

    private static int parseSymbol(@NotNull MacroSignature sig, @NotNull List<Node> nodes, int i) throws TxedtThrowable {
        if (i >= nodes.size()) {
            var start = nodes.getLast().bounds == null ? null : new Position(nodes.getLast().bounds.end());
            var end = start == null ? null : new Position(start);
            if (end != null) {
                end.step();
            }
            var errorBounds = new Bounds(start, end);
            throw new TxedtError(errorBounds, "expected symbol");
        }
        if (!(nodes.get(i) instanceof SymbolNode symbol)) {
            throw new TxedtError(nodes.get(i).bounds, "expected symbol");
        }
        sig.symbol(symbol.s);
        return parse(sig, nodes, i + 1);
    }

    private static int parseToken(@NotNull MacroSignature sig, @NotNull List<Node> nodes, int i) throws TxedtThrowable {
        if (i >= nodes.size()) {
            var start = nodes.getLast().bounds == null ? null : new Position(nodes.getLast().bounds.end());
            var end = start == null ? null : new Position(start);
            if (end != null) {
                end.step();
            }
            var errorBounds = new Bounds(start, end);
            throw new TxedtError(errorBounds, "expected symbol");
        }
        if (!(nodes.get(i) instanceof SymbolNode symbol)) {
            throw new TxedtError(nodes.get(i).bounds, "expected symbol");
        }
        sig.token(symbol.s);
        return parse(sig, nodes, i + 1);
    }
}
