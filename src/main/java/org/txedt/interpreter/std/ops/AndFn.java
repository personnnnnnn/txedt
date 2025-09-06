package org.txedt.interpreter.std.ops;

import org.txedt.TxedtThrowable;
import org.txedt.interpreter.std.general.TrueValue;

public class AndFn extends GenOp {
    public static AndFn func = new AndFn();
    private AndFn() { }

    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        return a != null && b != null ? TrueValue.tru : null;
    }
}
