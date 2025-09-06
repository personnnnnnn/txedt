package org.txedt.interpreter.std.ops;

import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;

public class SubFn extends GenOp {
    public static SubFn func = new SubFn();
    private SubFn() { }

    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        if (a instanceof Long la) {
            if (b instanceof Long lb) {
                return la - lb;
            }
            if (b instanceof Double db) {
                return la - db;
            }
        }

        if (a instanceof Double da) {
            if (b instanceof Long lb) {
                return da - lb;
            }
            if (b instanceof Double db) {
                return da - db;
            }
        }

        throw new TxedtError(null, "can only subtract numbers");
    }
}
