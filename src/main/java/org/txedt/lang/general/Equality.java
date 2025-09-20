package org.txedt.general;

public final class Equality {
    private Equality() { }

    public static boolean equals(Object a, Object b) {
        if (a == null ^ b == null) {
            return false;
        }
        if (a == null) {
            return true;
        }
        return switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xa.equals(xb);
                case Double xb -> xb.equals((double) xa);
                default -> false;
            };
            case Double xa -> switch (b) {
                case Integer xb -> xa.equals((double) xb);
                case Double xb -> xb.equals(xa);
                default -> false;
            };
            default -> a.equals(b);
        };
    }
}
