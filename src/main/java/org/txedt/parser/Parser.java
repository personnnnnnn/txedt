package org.txedt.parser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.nodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Parser {
    public final @NotNull String fileName;
    public final @NotNull String fileText;
    public final @NotNull Position pos;

    public final static String intRegex = "^[-+]?[0-9]+$";
    public final static String floatRegex = "^[-+]?([0-9]+\\.|\\.[0-9]+|[0-9]+\\.[0-9]+)$";

    public final static String nonSymbolChars = " \n\r\t\f;()\0\"";

    @Contract(pure = true)
    public static boolean isInt(@NotNull String x) {
        return x.matches(intRegex);
    }

    @Contract(pure = true)
    public static boolean isFloat(@NotNull String x) {
        return x.matches(floatRegex);
    }

    public static @NotNull String parseString(@NotNull String x, AtomicInteger errIdx, AtomicBoolean ok) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < x.length(); i++) {
            if (x.charAt(i) == '\\') {
                i++;
                if (i >= x.length()) {
                    ok.set(false);
                    errIdx.set(i);
                    return "expected character, not end of string";
                }

                switch (x.charAt(i)) {
                    case 'n': s.append('\n'); break;
                    case 'r': s.append('\r'); break;
                    case 't': s.append('\t'); break;
                    case '\\': s.append('\\'); break;
                    case '\'': s.append('\''); break;
                    case '"': s.append('"'); break;
                    case '0': s.append('\0'); break;
                    case '1': s.append('\1'); break;
                    case '2': s.append('\2'); break;
                    case '3': s.append('\3'); break;
                    case '4': s.append('\4'); break;
                    case '5': s.append('\5'); break;
                    case '6': s.append('\6'); break;
                    case '7': s.append('\7'); break;
                    case 'x': {
                        i++;
                        if (i >= x.length()) {
                            ok.set(false);
                            errIdx.set(i);
                            return "expected character, not end of string";
                        }
                        char first = x.charAt(i);
                        if (!"0123456789abcdefABCDEF".contains("" + first)) {
                            ok.set(false);
                            errIdx.set(i);
                            return "expected hex digit";
                        }
                        i++;
                        if (i >= x.length()) {
                            ok.set(false);
                            errIdx.set(i);
                            return "expected character, not end of string";
                        }
                        char second = x.charAt(i);
                        if (!"0123456789abcdefABCDEF".contains("" + second)) {
                            ok.set(false);
                            errIdx.set(i);
                            return "expected hex digit";
                        }
                        String hex = (first + "").toLowerCase() + (second + "").toLowerCase();
                        byte value = (byte) Integer.parseInt(hex, 16);
                        s.append((char) value);
                    } break;
                    case 'b': s.append('\b'); break;
                    case 'f': s.append('\f'); break;
                }
                continue;
            }
            if (Utils.newlineAt(x, i)) {
                s.append('\n');
                continue;
            }
            if (x.charAt(i) == '\r') {
                continue;
            }
            s.append(x.charAt(i));
        }
        ok.set(true);
        return s.toString();
    }

    public Parser(final @NotNull String fileName, final @NotNull String fileText) {
        this.fileName = fileName;
        this.fileText = fileText;
        pos = new Position(fileName, fileText);
    }

    public void skipWhitespace() {
        for (;;) {
            switch (pos.getChar()) {
                case ' ': case '\t': case '\r': case '\n':
                    pos.step();
                    continue;
                case ';':
                    while (!Utils.newlineAt(fileText, pos.idx) && pos.getChar() != '\0') {
                        pos.step();
                    }
                    continue;
                default:
                    return;
            }
        }
    }

    public @Nullable Node nextNode() throws ParseError {
        skipWhitespace();
        if (pos.getChar() == '\0' || pos.getChar() == ')') {
            return null;
        }
        if (pos.getChar() == '\"') {
            return string();
        }
        if (pos.getChar() == '(') {
            return list();
        }
        return symbol();
    }

    public @NotNull Node symbol() {
        Position start = new Position(pos);

        StringBuilder symbolSB = new StringBuilder();
        while (!nonSymbolChars.contains(pos.getChar() + "")) {
            symbolSB.append(pos.getChar());
            pos.step();
        }
        String symbol = symbolSB.toString();

        Bounds bounds = new Bounds(start, new Position(pos));

        if (isInt(symbol)) {
            long n = Long.parseLong(symbol);
            return new IntNode(bounds, n);
        }

        if (isFloat(symbol)) {
            double f = Double.parseDouble(symbol);
            return new FloatNode(bounds, f);
        }

        return new SymbolNode(bounds, symbol);
    }

    public @NotNull StringNode string() throws ParseError {
        char quote = pos.getChar();
        Position start = new Position(pos);
        pos.step();

        StringBuilder stringSB = new StringBuilder();
        while (pos.getChar() != quote) {
            if (pos.getChar() == '\\') {
                stringSB.append('\\');
                pos.step();
            }
            if (pos.getChar() == '\r' || pos.getChar() == '\n' || pos.getChar() == '\0') {
                Position a = new Position(pos);
                pos.step();
                Position b = new Position(pos);
                Bounds bounds = new Bounds(a, b);
                throw new ParseError(bounds, pos.getChar() == '\0' ? "unexpected eof" : "unexpected newline");
            }
            stringSB.append(pos.getChar());
            pos.step();
        }
        String s = stringSB.toString();
        pos.step();

        AtomicBoolean ok = new AtomicBoolean(true);
        AtomicInteger errIdx = new AtomicInteger(-1);
        String res = parseString(s, errIdx, ok);
        if (!ok.get()) {
            Position p = new Position(pos);
            p.idx += errIdx.get() + 1;
            p.col += errIdx.get() + 1;
            Position next = new Position(p);
            next.step();
            throw new ParseError(new Bounds(p, next), res);
        }

        Bounds bounds = new Bounds(start, new Position(pos));
        return new StringNode(bounds, res);
    }

    public @NotNull ListNode list() throws ParseError {
        Position start = new Position(pos);
        List<Node> children = new ArrayList<>();

        if (pos.getChar() != '(') {
            Position end = new Position(pos);
            end.step();
            Bounds bounds = new Bounds(start, end);
            throw new ParseError(bounds, "expected '('");
        }
        pos.step();

        for (;;) {
            Node child = nextNode();
            if (child == null) {
                break;
            }
            children.add(child);
        }

        skipWhitespace();

        if (pos.getChar() != ')') {
            Position a = new Position(pos);
            pos.step();
            Position b = new Position(pos);
            Bounds bounds = new Bounds(a, b);
            throw new ParseError(bounds, "expected ')'");
        }

        Position end = new Position(pos);
        pos.step();
        Bounds bounds = new Bounds(start, end);
        return new ListNode(bounds, children);
    }
}
