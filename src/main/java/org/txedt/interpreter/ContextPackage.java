package org.txedt.interpreter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.parser.Bounds;

public class ContextPackage extends Context {
    public ContextPackage(Context parent) {
        super(parent);
    }

    public ContextPackage() {
        super();
    }

    @Override
    public Object getExternal(@NotNull String name, Bounds bounds) throws TxedtThrowable {
        return get(name, bounds, parent instanceof ContextPackage);
    }

    @Override
    public Object setExternal(@NotNull String name, Object value, Bounds bounds) throws TxedtThrowable {
        return set(name, value, bounds, parent instanceof ContextPackage);
    }

    @Override
    public boolean existsPackageExternal(@NotNull String name, @Nullable Bounds bounds) throws TxedtThrowable {
        return existsPackage(name, bounds, parent instanceof ContextPackage);
    }
}
