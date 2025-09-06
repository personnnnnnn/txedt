package org.txedt.interpreter.std.ops;

import org.txedt.TxedtThrowable;
import org.txedt.interpreter.std.general.TrueValue;

public class NeqFn extends AccOp {
    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        if (a == null) {
            return b != null ? TrueValue.tru : null;
        }
        if (b == null) {
            return TrueValue.tru;
        }
        return a.equals(b) ? null : TrueValue.tru;
    }

    @Override
    public Object combine(Object a, Object b) throws TxedtThrowable {
        return a != null && b != null;
    }

    public static NeqFn func = new NeqFn();
    private NeqFn() { }
}
