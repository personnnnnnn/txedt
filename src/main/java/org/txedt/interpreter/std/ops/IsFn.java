package org.txedt.interpreter.std.ops;

import org.txedt.TxedtThrowable;

public class IsFn extends AccOp {
    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        return a == b;
    }

    @Override
    public Object combine(Object a, Object b) throws TxedtThrowable {
        return a != null && b != null;
    }

    public static IsFn func = new IsFn();
    private IsFn() { }
}
