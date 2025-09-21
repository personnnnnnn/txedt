package org.txedt.lang.general;

public final class TxedtBool {
    private TxedtBool() { }

    public static final TxedtBool tru = new TxedtBool();
    public static final TxedtBool fals = null;

    public static boolean from(Object x) {
        return x != null;
    }

    public static TxedtBool to(boolean x) {
        return x ? tru : fals;
    }
}
