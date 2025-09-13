package org.txedt.general;

public final class TxedtBool {
    private TxedtBool() { }

    private static final class TrueValue { }
    public static final Object tru = new TrueValue();
    public static final Object fals = null;

    public static boolean from(Object x) {
        return x != null;
    }

    public static Object to(boolean x) {
        return x ? tru : fals;
    }
}
