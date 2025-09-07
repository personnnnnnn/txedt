package org.txedt.interpreter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.parser.Bounds;

public class Package extends Context {
    public Package(Context parent) {
        super(parent);
    }

    public Package() {
        super();
    }

    @Override
    public Object getExternal(@NotNull String name, Bounds bounds) throws TxedtThrowable {
        return get(name, bounds, parent instanceof Package);
    }

    @Override
    public Object setExternal(@NotNull String name, Object value, Bounds bounds) throws TxedtThrowable {
        return set(name, value, bounds, parent instanceof Package);
    }

    @Override
    public boolean existsPackageExternal(@NotNull String name, @Nullable Bounds bounds) throws TxedtThrowable {
        return existsPackage(name, bounds, parent instanceof Package);
    }
}
