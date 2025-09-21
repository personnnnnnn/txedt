package org.txedt.windowing;

import org.txedt.lang.errors.TxedtThrowable;

public interface DrawAction {
    void complete() throws TxedtThrowable;
}
