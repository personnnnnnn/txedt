package org.txedt.interpreter.std.ops;

import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.std.general.TrueValue;

public class GtFn extends AccOp {
    @Override
    public Object op(Object a, Object b) throws TxedtThrowable {
        if (a instanceof Long al) {
            if (b instanceof Long bl) {
                return al > bl ? TrueValue.tru : null;
            }
            if (b instanceof Double bd) {
                return al > bd ? TrueValue.tru : null;
            }
        }
        if (a instanceof Double ad) {
            if (b instanceof Long bl) {
                return ad > bl ? TrueValue.tru : null;
            }
            if (b instanceof Double bd) {
                return ad > bd ? TrueValue.tru : null;
            }
        }
        throw new TxedtError(null, "can only compare numbers");
    }

    @Override
    public Object combine(Object a, Object b) throws TxedtThrowable {
        return a != null && b != null;
    }

    public static GtFn func = new GtFn();
    private GtFn() { }
}
