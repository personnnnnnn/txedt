package org.txedt.parser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract sealed class Node {
    public final @NotNull Backtrace backtrace;

    protected Node(@NotNull Backtrace backtrace) {
        this.backtrace = backtrace;
    }

    public static final class Int extends Node {
        public final long n;

        public Int(@NotNull Backtrace backtrace, long n) {
            super(backtrace);
            this.n = n;
        }

        @Override
        public String toString() {
            return n + "";
        }
    }

    public static final class Flt extends Node {
        public final double n;

        public Flt(@NotNull Backtrace backtrace, double n) {
            super(backtrace);
            this.n = n;
        }

        @Override
        public String toString() {
            return n + "";
        }
    }

    public static final class Str extends Node {
        public final @NotNull String s;

        public Str(@NotNull Backtrace backtrace, @NotNull String s) {
            super(backtrace);
            this.s = s;
        }

        @Override
        public String toString() {
            return "\"" + s + "\"";
        }
    }

    public static final class Symbol extends Node {
        public final @NotNull String s;

        public Symbol(@NotNull Backtrace backtrace, @NotNull String s) {
            super(backtrace);
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    public static final class Lst extends Node {
        public final @NotNull List<Node> children;

        public Lst(@NotNull Backtrace backtrace, @NotNull List<Node> children) {
            super(backtrace);
            this.children = children;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder("(");
            var join = "";
            for (var child : children) {
                s.append(join).append(child);
                join = " ";
            }
            return s + ")";
        }
    }
}
