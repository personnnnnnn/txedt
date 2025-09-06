package org.txedt.interpreter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.parser.Bounds;

import java.util.HashMap;
import java.util.Map;

public class Context {
    public final @Nullable Context parent;
    private final @NotNull Map<String, Object> vars = new HashMap<>();

    public Context() {
        parent = null;
    }

    public Context(@Nullable Context parent) {
        this.parent = parent;
    }

    public Object put(@NotNull String name, Object value) {
        vars.put(name, value);
        return value;
    }

    public Object set(@NotNull String name, Object value, Bounds bounds) throws TxedtThrowable {
        if (!vars.containsKey(name)) {
            if (parent == null) {
                throw new TxedtThrowable(bounds, "variable '" + name + "' does not exist");
            }
            return parent.set(name, value, bounds);
        }
        return put(name, value);
    }

    public Object get(@NotNull String name, Bounds bounds) throws TxedtThrowable {
        if (!vars.containsKey(name)) {
            if (parent == null) {
                throw new TxedtThrowable(bounds, "variable '" + name + "' does not exist");
            }
            return parent.get(name, bounds);
        }
        return vars.get(name);
    }
}
