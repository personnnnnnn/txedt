package org.txedt.parser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract sealed class Node {
    public final @NotNull Bounds bounds;

    protected Node(@NotNull Bounds bounds) {
        this.bounds = bounds;
    }

    public static final class Int extends Node {
        public final long n;

        public Int(@NotNull Bounds bounds, long n) {
            super(bounds);
            this.n = n;
        }

        @Override
        public String toString() {
            return n + "";
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
    }
}
