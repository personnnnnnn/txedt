package org.txedt.lang.parser;

import org.jetbrains.annotations.NotNull;
import org.txedt.lang.errors.TxedtError;

import java.util.ArrayList;
import java.util.List;

public final class Parser {
    private Parser() { }

    public static final String WHITESPACE = " \n\t";
    public static final String COMMENT_ENDERS = "\0\n";
    public static final String NON_SYMBOL_CHARS = WHITESPACE + "\0();\"";
    public static final String INT_REGEX = "^[\\-+]?[0-9]+$";
    public static final String FLOAT_REGEX = "^[\\-+]?([0-9]+\\.|\\.[0-9]+|[0-9]+\\.[0-9]+)$";

    public static @NotNull List<Node> parse(@NotNull Backtrace backtrace, @NotNull String src, @NotNull String text) throws TxedtError {
        var pos = new Position(src, text);
        var nodes = new ArrayList<Node>();
        while (pos.getChar() != '\0') {
            var node = nextNode(pos, backtrace);
            nodes.add(node);
            skipWhitespace(pos);
        }
        return nodes;
    }

    private static void skipWhitespace(@NotNull Position pos) {
        while (pos.getChar() != '\0') {
            if (WHITESPACE.contains(pos.getChar() + "")) {
                pos.step();
                continue;
            }
            if (pos.getChar() == ';') {
                while (!COMMENT_ENDERS.contains(pos.getChar() + "")) {
                    pos.step();
                }
                continue;
            }
            break;
        }
    }

    private static @NotNull Node nextNode(@NotNull Position pos, @NotNull Backtrace backtrace) throws TxedtError {
        skipWhitespace(pos);
        if (pos.getChar() == '(') {
            return list(pos, backtrace);
        }
        if (pos.getChar() == '"') {
            return string(pos, backtrace);
        }
        return symbol(pos, backtrace);
    }

    private static @NotNull Node symbol(@NotNull Position pos, @NotNull Backtrace backtrace) throws TxedtError {
        var start = pos.copy();

        StringBuilder sb = new StringBuilder();
        while (!NON_SYMBOL_CHARS.contains(pos.getChar() + "")) {
            sb.append(pos.getChar());
            pos.step();
        }

        var end = pos.copy().stepBack();

        String symbol = sb.toString();
        if (symbol.isEmpty()) {
            throw new TxedtError(Backtrace.sameWith(backtrace, new Bounds(start)), "expected a symbol");
        }

        if (symbol.matches(INT_REGEX)) {
            var n = Integer.parseInt(symbol);
            return new Node.Int(new Bounds(start, end), n);
        }

        if (symbol.matches(FLOAT_REGEX)) {
            var n = Double.parseDouble(symbol);
            return new Node.Flt(new Bounds(start, end), n);
        }

        return new Node.Symbol(new Bounds(start, end), symbol);
    }

    private static @NotNull Node.Lst list(@NotNull Position pos, @NotNull Backtrace backtrace) throws TxedtError {
        if (pos.getChar() != '(') {
            throw new TxedtError(Backtrace.sameWith(backtrace, new Bounds(pos)), "expected '('");
        }

        var start = pos.copy();
        pos.step();
        List<Node> children = new ArrayList<>();

        skipWhitespace(pos);
        while (pos.getChar() != ')') {
            if (pos.getChar() == '\0') {
                throw new TxedtError(Backtrace.sameWith(backtrace, new Bounds(pos)), "expected ')'");
            }
            var node = nextNode(pos, backtrace);
            children.add(node);
            skipWhitespace(pos);
        }

        var end = pos.copy();
        pos.step();

        return new Node.Lst(new Bounds(start, end), children);
    }

    private static @NotNull Node.Str string(@NotNull Position pos, @NotNull Backtrace backtrace) throws TxedtError {
        var start = pos.copy();

        if (pos.getChar() != '"') {
            throw new TxedtError(Backtrace.sameWith(backtrace, new Bounds(pos)), "expected '\"'");
        }
        pos.step();

        StringBuilder sb = new StringBuilder();
        while (pos.getChar() != '"') {
            if (pos.getChar() == '\n') {
                throw new TxedtError(Backtrace.sameWith(backtrace, new Bounds(pos)), "unexpected newline");
            }
            if (pos.getChar() == '\0') {
                throw new TxedtError(Backtrace.sameWith(backtrace, new Bounds(pos)), "unexpected eof");
            }
            sb.append(pos.getChar());
            pos.step();
        }

        var end = pos.copy();
        pos.step();

        String str;
        try {
            str = sb.toString().translateEscapes(); // thank you java :)
        } catch (IllegalArgumentException e) {
            throw new TxedtError(Backtrace.sameWith(backtrace, new Bounds(start, end)), e.toString());
        }

        return new Node.Str(new Bounds(start, end), str);
    }

}
