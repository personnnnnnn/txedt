package org.txedt.interpreter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.parser.Bounds;
import org.txedt.parser.Position;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Context {
    public @Nullable Context parent;
    public final @NotNull Map<String, Object> vars = new HashMap<>();

    interface ThingDoer<T, R> {
        R doThing(@NotNull Context ctx, @NotNull String name, @Nullable Bounds bounds, T extra) throws TxedtThrowable;
    }

    public Context() {
        parent = null;
    }

    public Context(@Nullable Context parent) {
        this.parent = parent;
    }

    @Contract(pure = true)
    private<T, R> @Nullable R checkForColon(@NotNull String name, Bounds bounds, T extra, ThingDoer<T, R> thingDoer) throws TxedtThrowable {
        if (!name.contains(":")) {
            return null;
        }

        var matches = name.split(":", 2);
        var otherCtxName = matches[0];
        var varName = matches[1];

        Bounds ctxBounds = new Bounds(new Position(bounds.start()), new Position(bounds.end()));
        Bounds varBounds = new Bounds(new Position(bounds.start()), new Position(bounds.end()));

        for (int i = 0; i < otherCtxName.length(); i++) {
            ctxBounds.end().step();
            varBounds.start().step();
        }
        varBounds.start().step(); // to skip colon

        var otherCtx = get(otherCtxName, ctxBounds);

        if (!(otherCtx instanceof Context ctx)) {
            throw new TxedtError(ctxBounds, "can only get variables from contexts");
        }

        return thingDoer.doThing(ctx, varName, varBounds, extra);
    }

    public Object putPackage(@NotNull String name, Bounds bounds, Object value) throws TxedtThrowable {
        if (name.contains(":")) {
            return checkForColon(name, bounds, value, Context::putPackage);
        }
        return put(name, value);
    }

    public Object put(@NotNull String name, Object value) {
        vars.put(name, value);
        return value;
    }

    public Object setExternal(@NotNull String name, Object value, Bounds bounds) throws TxedtThrowable {
        return set(name, value, bounds);
    }

    public Object set(@NotNull String name, Object value, Bounds bounds, boolean handleParent) throws TxedtThrowable {
        if (name.contains(":")) {
            return checkForColon(name, bounds, value, (
                    ctx,
                    name_,
                    bounds_,
                    value_
            ) -> ctx.setExternal(name_, value_, bounds_));
        }

        if (!vars.containsKey(name)) {
            if (parent == null) {
                throw new TxedtThrowable(bounds, "variable '" + name + "' does not exist");
            }
            return parent.set(name, value, bounds);
        }
        return put(name, value);
    }

    public Object set(@NotNull String name, Object value, Bounds bounds) throws TxedtThrowable {
        return set(name, value, bounds, true);
    }

    public Object getExternal(@NotNull String name, Bounds bounds) throws TxedtThrowable {
        return get(name, bounds);
    }

    public Object get(@NotNull String name, Bounds bounds, boolean handleParent) throws TxedtThrowable {
        if (name.contains(":")) {
            return checkForColon(name, bounds, null, (
                    ctx,
                    name_,
                    bounds_,
                    _
            ) -> ctx.getExternal(name_, bounds_));
        }

        if (!vars.containsKey(name)) {
            if (parent == null || !handleParent) {
                throw new TxedtThrowable(bounds, "variable '" + name + "' does not exist");
            }
            return parent.get(name, bounds);
        }
        return vars.get(name);
    }

    public Object get(@NotNull String name, Bounds bounds) throws TxedtThrowable {
        return get(name, bounds, true);
    }

    public boolean exists(@NotNull String name) {
        return exists(name, true);
    }

    public boolean exists(@NotNull String name, boolean handleParent) {
        if (vars.containsKey(name)) {
            return true;
        }
        if (parent == null || !handleParent) {
            return false;
        }
        return parent.exists(name);
    }

    public boolean existsPackage(@NotNull String name, @Nullable Bounds bounds) throws TxedtThrowable {
        return existsPackage(name, bounds, true);
    }

    public boolean existsPackageExternal(@NotNull String name, @Nullable Bounds bounds) throws TxedtThrowable {
        return existsPackage(name, bounds);
    }

    public boolean existsPackage(@NotNull String name, @Nullable Bounds bounds, boolean handlesParent) throws TxedtThrowable {
        if (name.contains(":")) {
            return Boolean.TRUE.equals(this.<Object, Boolean>checkForColon(name, bounds, null, (
                    ctx,
                    name_,
                    bounds_,
                    _
            ) -> ctx.existsPackageExternal(name_, bounds_)));
        }

        return exists(name, handlesParent);
    }

    public boolean has(@NotNull String name) {
        return vars.containsKey(name);
    }

    public void putInto(@NotNull Context context) throws TxedtThrowable {
        context.vars.putAll(vars);
    }

    public void putFrom(@NotNull Context context) throws TxedtThrowable {
        context.putInto(this);
    }

    public void putFrom(@NotNull Map<String, Object> vars) {
        this.vars.putAll(vars);
    }

    @Override
    public String toString() {
        Set<Context> contexts = new HashSet<>();
        return toString(contexts);
    }

    private @NotNull String toString(@NotNull Set<Context> contexts) {
        StringBuilder s = new StringBuilder("{");
        String join = "";
        contexts.add(this);

        for (var entry : vars.entrySet()) {
            s.append(join);
            join = ", ";
            s.append(entry.getKey()).append(": ");
            if (entry.getValue() instanceof Context ctx) {
                if (contexts.contains(ctx)) {
                    s.append("(...)");
                } else {
                    s.append(ctx.toString(contexts));
                }
            } else {
                s.append(entry.getValue());
            }
        }

        return s + "}";
    }
}
