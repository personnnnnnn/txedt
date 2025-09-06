package org.txedt.parser.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Bounds;

import java.util.List;

public final class ListNode extends Node {
    public final @NotNull List<Node> children;

    public ListNode(final @Nullable Bounds bounds, final @NotNull List<Node> children) {
        super(bounds);
        this.children = children;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("(");
        String insert = "";
        for (var child : children) {
            s.append(insert).append(child.toString());
            insert = " ";
        }
        return s + ")";
    }
}
