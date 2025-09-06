package org.txedt.interpreter.std.ops;

import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;

public class DivFn extends GenOp {
    public static DivFn func = new DivFn();
    private DivFn() { }

    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        if (a instanceof Long la) {
            if (b instanceof Long lb) {
                if (lb == 0) {
                    throw new TxedtError(null, "cannot divide by 0");
                }
                return la / lb;
            }
            if (b instanceof Double db) {
                if (db == 0) {
                    throw new TxedtError(null, "cannot divide by 0");
                }
                return la / db;
            }
        }

        if (a instanceof Double da) {
            if (b instanceof Long lb) {
                if (lb == 0) {
                    throw new TxedtError(null, "cannot divide by 0");
                }
                return da / lb;
            }
            if (b instanceof Double db) {
                if (db == 0) {
                    throw new TxedtError(null, "cannot divide by 0");
                }
                return da / db;
            }
        }

        throw new TxedtError(null, "can only add numbers");
    }
}
