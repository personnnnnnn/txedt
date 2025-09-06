package org.txedt.interpreter.std.ops;

import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.std.general.TrueValue;

public class NeqSign extends AccOp {
    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        if (a == null) {
            return b != null;
        }
        if (b == null) {
            return true;
        }
        return !a.equals(b);
    }

    @Override
    public Object combine(Object a, Object b) throws TxedtThrowable {
        return a != null && b != null;
    }

    public static NeqSign func = new NeqSign();
    private NeqSign() { }
}
