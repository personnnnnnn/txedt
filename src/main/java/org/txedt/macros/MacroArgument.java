package org.txedt.macros;

import org.jetbrains.annotations.NotNull;
import org.txedt.contexts.Context;
import org.txedt.errors.TxedtError;
import org.txedt.errors.TxedtThrowable;
import org.txedt.general.TxedtBool;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Bounds;
import org.txedt.parser.Node;

import java.util.ArrayList;
import java.util.List;

// TODO: make a UserMacro class and actually test this, but there aren't that many places it could break (I think)
public sealed interface MacroArgument {
    int match(int i, @NotNull Node.Lst list, @NotNull Backtrace backtrace, @NotNull Context write) throws TxedtThrowable;

    record Expr(@NotNull String name) implements MacroArgument {
        @Override
        public int match(int i, Node.@NotNull Lst list, @NotNull Backtrace backtrace, @NotNull Context write) throws TxedtThrowable {
            if (i >= list.children.size()) {
                var bounds = new Bounds(list.bounds.end().copy().stepBack(), list.bounds.end().copy());
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected expression");
            }
            var node = list.children.get(i++);
            write.put(name, node);
            return i;
        }
    }

    record Symbol(@NotNull String name) implements MacroArgument {
        @Override
        public int match(int i, Node.@NotNull Lst list, @NotNull Backtrace backtrace, @NotNull Context write) throws TxedtThrowable {
            if (i >= list.children.size()) {
                var bounds = new Bounds(list.bounds.end().copy().stepBack(), list.bounds.end().copy());
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected symbol");
            }
            var node = list.children.get(i++);
            if (!(node instanceof Node.Symbol)) {
                var bounds = node.bounds;
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected symbol");
            }
            write.put(name, node);
            return i;
        }
    }

    record Str(@NotNull String name) implements MacroArgument {
        @Override
        public int match(int i, Node.@NotNull Lst list, @NotNull Backtrace backtrace, @NotNull Context write) throws TxedtThrowable {
            if (i >= list.children.size()) {
                var bounds = new Bounds(list.bounds.end().copy().stepBack(), list.bounds.end().copy());
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected string");
            }
            var node = list.children.get(i++);
            if (!(node instanceof Node.Str)) {
                var bounds = node.bounds;
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected string");
            }
            write.put(name, node);
            return i;
        }
    }

    record Token(@NotNull String symbol) implements MacroArgument {
        @Override
        public int match(int i, Node.@NotNull Lst list, @NotNull Backtrace backtrace, @NotNull Context write) throws TxedtThrowable {
            if (i >= list.children.size()) {
                var bounds = new Bounds(list.bounds.end().copy().stepBack(), list.bounds.end().copy());
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected '" + symbol + "'");
            }
            var node = list.children.get(i++);
            if (!(node instanceof Node.Symbol symb) || !symb.s.equals(symbol)) {
                var bounds = node.bounds;
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected '" + symbol + "'");
            }
            return i;
        }
    }

    record Optional(@NotNull List<MacroArgument> sub) implements MacroArgument {
        @Override
        public int match(int i, Node.@NotNull Lst list, @NotNull Backtrace backtrace, @NotNull Context write) {
            var subCtx = new Context();
            try {
                for (var x : sub) {
                    i = x.match(i, list, backtrace, subCtx);
                }
            } catch (TxedtThrowable _) { }
            write.parent(subCtx);
            return i;
        }
    }

    record Sublist(@NotNull List<MacroArgument> sub) implements MacroArgument {
        @Override
        public int match(int i, Node.@NotNull Lst list, @NotNull Backtrace backtrace, @NotNull Context write) throws TxedtThrowable {
            if (i >= list.children.size()) {
                var bounds = new Bounds(list.bounds.end().copy().stepBack(), list.bounds.end().copy());
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected list");
            }
            var node = list.children.get(i++);
            if (!(node instanceof Node.Lst subList)) {
                var bounds = node.bounds;
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected list");
            }
            var subI = 0;
            for (var x : sub) {
                subI = x.match(subI, subList, backtrace, write);
            }
            if (subI < subList.children.size()) {
                var bounds = new Bounds(subList.children.get(subI).bounds, subList.children.getLast().bounds);
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected end of list");
            }
            return i;
        }
    }

    record Many(@NotNull String name, @NotNull List<MacroArgument> sub) implements MacroArgument {
        @Override
        public int match(int i, Node.@NotNull Lst list, @NotNull Backtrace backtrace, @NotNull Context write) {
            var items = new ArrayList<>();
            for (;;) {
                try {
                    var subCtx = new Context();
                    for (var x : sub) {
                        i = x.match(i, list, backtrace, subCtx);
                    }
                    items.add(subCtx);
                } catch (TxedtThrowable _) {
                    break;
                }
            }
            write.put(name, items);
            return i;
        }
    }

    record Choose(@NotNull List<MacroChooseOption> options) implements MacroArgument {
        @Override
        public int match(int i, Node.@NotNull Lst list, @NotNull Backtrace backtrace, @NotNull Context write) throws TxedtThrowable {
            for (var opt : options) {
                try {
                    var subCtx = new Context();
                    var subI = i;
                    for (var x : opt.sub()) {
                        subI = x.match(subI, list, backtrace, subCtx);
                    }
                    write.parent(subCtx);
                    write.put(opt.checkName(), TxedtBool.tru);
                    return subI;
                } catch (TxedtThrowable _) {
                    write.put(opt.checkName(), TxedtBool.fals);
                }
            }
            throw new TxedtError(new Backtrace(backtrace.parent(), list.bounds), "invalid option");
        }
    }

    record Body(@NotNull String name) implements MacroArgument {
        @Override
        public int match(int i, Node.@NotNull Lst list, @NotNull Backtrace backtrace, @NotNull Context write) throws TxedtThrowable {
            if (i >= list.children.size()) {
                var bounds = new Bounds(list.bounds.end().copy().stepBack(), list.bounds.end().copy());
                var backt = new Backtrace(backtrace.parent(), bounds);
                throw new TxedtError(backt, "expected body");
            }
            write.put(name, list.children.subList(i, list.children.size()));
            return list.children.size();
        }
    }
}
