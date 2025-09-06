package org.txedt.interpreter.std.ops;

import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;

public class MaxFn extends GenOp {
    public static MaxFn func = new MaxFn();
    private MaxFn() { }

    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        if (a instanceof Long la) {
            if (b instanceof Long lb) {
                return la > lb ? la : lb;
            }
            if (b instanceof Double db) {
                return la > db ? la : db;
            }
        }

        if (a instanceof Double da) {
            if (b instanceof Long lb) {
                return da > lb ? da : lb;
            }
            if (b instanceof Double db) {
                return da > db ? da : db;
            }
        }

        throw new TxedtError(null, "can only compare numbers");
    }
}
