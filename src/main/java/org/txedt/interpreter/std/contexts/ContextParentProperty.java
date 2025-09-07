package org.txedt.interpreter.std.contexts;

import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.funcs.Property;

public class ContextParentProperty extends Property {
    public static ContextParentProperty property = new ContextParentProperty();
    private ContextParentProperty() {
        super();
    }

    @Override
    public Object get(Object o) throws TxedtThrowable {
        if (!(o instanceof Context ctx)) {
            throw new TxedtError(null, "expected context");
        }
        return ctx.parent;
    }

    @Override
    public Object setDirect(Object o, Object v) throws TxedtThrowable {
        if (!(o instanceof Context ctx)) {
            throw new TxedtError(null, "expected context");
        }
        if (v == null) {
            ctx.parent = null;
            return null;
        }
        if (!(v instanceof Context parent)) {
            throw new TxedtError(null, "parent must be a context");
        }
        ctx.parent = parent;
        return v;
    }
}
