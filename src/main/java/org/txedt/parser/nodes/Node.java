package org.txedt.parser.nodes;

import org.jetbrains.annotations.Nullable;
import org.txedt.parser.Bounds;

public abstract sealed class Node permits IntNode, FloatNode, SymbolNode, StringNode, ListNode {
    public final @Nullable Bounds bounds;

    public Node(final @Nullable Bounds bounds) {
        this.bounds = bounds;
    }
}
