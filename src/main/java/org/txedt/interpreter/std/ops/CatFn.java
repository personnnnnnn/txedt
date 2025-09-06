package org.txedt.interpreter.std.ops;

import org.txedt.TxedtThrowable;

public class CatFn extends GenOp {
    public static CatFn func = new CatFn();
    private CatFn() { }

    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        return a + "" + b;
    }

    @Override
    public Object single(Object x) throws TxedtThrowable {
        return x + "";
    }
}
