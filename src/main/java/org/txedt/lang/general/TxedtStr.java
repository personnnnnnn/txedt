package org.txedt.lang.general;

public final class TxedtStr {
    private TxedtStr() { }

    public static String toString(Object o) {
        return switch (o) {
            case TxedtBool ignored -> "t";
            case null -> "nil";
            default -> o.toString();
        };
    }
}
