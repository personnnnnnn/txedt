package org.txedt.interpreter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.parser.Bounds;
import org.txedt.parser.nodes.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Library extends Context {
    public final Set<String> exports = new HashSet<>();
    public final List<Node> nodes;
    private int nodeIndex = 0;

    public void export(@NotNull String name) {
        exports.add(name);
    }

    public Library(Context parent, List<Node> nodes) {
        super(parent);
        this.nodes = nodes;
    }

    public Library(List<Node> nodes) {
        super();
        this.nodes = nodes;
    }

    public void eval() throws TxedtThrowable {
        for (; nodeIndex < nodes.size(); nodeIndex++) {
            Interpreter.eval(nodes.get(nodeIndex), this);
        }
    }

    public void evalUntil(@NotNull String export) throws TxedtThrowable {
        for (; nodeIndex < nodes.size() && !exports.contains(export); nodeIndex++) {
            Interpreter.eval(nodes.get(nodeIndex), this);
        }
    }

    @Override
    public Object getExternal(@NotNull String name, Bounds bounds) throws TxedtThrowable {
        evalUntil(name);
        if (!exports.contains(name.replaceFirst(":.*$", ""))) {
            throw new TxedtError(bounds, "variable '" + name + "' is not exported");
        }
        return super.get(name, bounds, false);
    }

    @Override
    public Object setExternal(@NotNull String name, Object value, Bounds bounds) throws TxedtThrowable {
        evalUntil(name);
        if (!exports.contains(name.replaceFirst(":.*$", ""))) {
            throw new TxedtError(bounds, "variable '" + name + "' is not exported");
        }
        return super.set(name, value, bounds, false);
    }

    @Override
    public boolean existsPackageExternal(@NotNull String name, @Nullable Bounds bounds) throws TxedtThrowable {
        evalUntil(name);
        return super.existsPackage(name, bounds, false);
    }

    @Override
    public void putInto(@NotNull Context context) throws TxedtThrowable {
        eval();
        super.putInto(context);
    }
}
