package org.txedt.interpreter.std.ops;

import org.txedt.TxedtThrowable;
import org.txedt.interpreter.std.general.TrueValue;

public class OrFn extends GenOp {
    public static OrFn func = new OrFn();
    private OrFn() { }

    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        return a != null || b != null ? TrueValue.tru : null;
    }
}
