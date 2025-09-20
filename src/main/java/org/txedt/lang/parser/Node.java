package org.txedt.parser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class Node {
    public final @NotNull Bounds bounds;

    protected Node(@NotNull Bounds bounds) {
        this.bounds = bounds;
    }

    public static final class Int extends Node {
        public final int n;

        public Int(@NotNull Bounds bounds, int n) {
            super(bounds);
            this.n = n;
        }

        @Override
        public String toString() {
            return n + "";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Int i)) {
                return false;
            }
            return i.n == n;
        }
    }

    public static final class Flt extends Node {
        public final double n;

        public Flt(@NotNull Bounds bounds, double n) {
            super(bounds);
            this.n = n;
        }

        @Override
        public String toString() {
            return n + "";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Flt f)) {
                return false;
            }
            return f.n == n;
        }
    }

    public static final class Str extends Node {
        public final @NotNull String s;

        public Str(@NotNull Bounds bounds, @NotNull String s) {
            super(bounds);
            this.s = s;
        }

        @Override
        public String toString() {
            return "\"" + s + "\"";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Str str)) {
                return false;
            }
            return str.s.equals(s);
        }
    }

    public static final class Symbol extends Node {
        public final @NotNull String s;

        public Symbol(@NotNull Bounds bounds, @NotNull String s) {
            super(bounds);
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Symbol symb)) {
                return false;
            }
            return symb.s.equals(s);
        }
    }

    public static final class Lst extends Node {
        public final @NotNull List<Node> children;

        public Lst(@NotNull Bounds bounds, @NotNull List<Node> children) {
            super(bounds);
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

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Lst l)) {
                return false;
            }
            return l.children.equals(children);
        }

        public @NotNull Lst sublist(int start, int end) {
            if (start >= this.children.size()) {
                return new Lst(new Bounds(bounds.end().copy().stepBack()), new ArrayList<>());
            }
            return new Lst(new Bounds(children.get(start).bounds, children.get(end - 1).bounds), children.subList(start, end));
        }

        public @NotNull Lst sublist(int start) {
            return sublist(start, children.size());
        }
    }
}
