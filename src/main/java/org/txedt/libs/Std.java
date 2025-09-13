package org.txedt.libs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.txedt.contexts.Context;
import org.txedt.errors.TxedtError;
import org.txedt.errors.TxedtThrowable;
import org.txedt.functions.ExternalFunction;
import org.txedt.functions.FunctionSignature;
import org.txedt.parser.Backtrace;

import java.util.List;

public final class Std {
    public static final Context ctx;
    private Std() { }

    private interface Operation {
        Object op(Backtrace backtrace, Object a, Object b) throws TxedtThrowable;
    }
    @Contract("_ -> new")
    private static @NotNull ExternalFunction operation(Operation operation) {
        return new ExternalFunction(
            new FunctionSignature()
                    .arg("x")
                    .rest("xs"),
            (backtrace, args) -> {
                var x = args.get("x");
                if (!(args.get("xs") instanceof List<?> xs)) {
                    return null; // this normally shouldn't happen
                }
                for (var v : xs) {
                    x = operation.op(backtrace, x, v);
                }
                return x;
            }
        );
    }

    static {
        ctx = new Context();

        ctx.put("+", operation((backtrace, a, b) -> switch (a) {
            case Long xa -> switch (b) {
                case Long xb -> xa + xb;
                case Double xb -> xa + xb;
                case null, default -> throw new TxedtError(backtrace, "can only add numbers");
            };
            case Double xa -> switch (b) {
                case Long xb -> xa + xb;
                case Double xb -> xa + xb;
                case null, default -> throw new TxedtError(backtrace, "can only add numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only add numbers");
        }));

        ctx.put("-", operation((backtrace, a, b) -> switch (a) {
            case Long xa -> switch (b) {
                case Long xb -> xa - xb;
                case Double xb -> xa - xb;
                case null, default -> throw new TxedtError(backtrace, "can only subtract numbers");
            };
            case Double xa -> switch (b) {
                case Long xb -> xa - xb;
                case Double xb -> xa - xb;
                case null, default -> throw new TxedtError(backtrace, "can only subtract numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only subtract numbers");
        }));

        ctx.put("*", operation((backtrace, a, b) -> switch (a) {
            case Long xa -> switch (b) {
                case Long xb -> xa * xb;
                case Double xb -> xa * xb;
                case null, default -> throw new TxedtError(backtrace, "can only multiply numbers");
            };
            case Double xa -> switch (b) {
                case Long xb -> xa * xb;
                case Double xb -> xa * xb;
                case null, default -> throw new TxedtError(backtrace, "can only multiply numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only multiply numbers");
        }));

        ctx.put("/", operation((backtrace, a, b) -> switch (a) {
            case Long xa -> switch (b) {
                case Long xb -> {
                    if (xb == 0) {
                        throw new TxedtError(backtrace, "cannot divide by 0");
                    }
                    yield xa / xb;
                }
                case Double xb -> {
                    if (xb == 0) {
                        throw new TxedtError(backtrace, "cannot divide by 0");
                    }
                    yield xa / xb;
                }
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case Double xa -> switch (b) {
                case Long xb -> {
                    if (xb == 0) {
                        throw new TxedtError(backtrace, "cannot divide by 0");
                    }
                    yield xa / xb;
                }
                case Double xb -> {
                    if (xb == 0) {
                        throw new TxedtError(backtrace, "cannot divide by 0");
                    }
                    yield xa / xb;
                }
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
        }));

        ctx.put("%", operation((backtrace, a, b) -> switch (a) {
            case Long xa -> switch (b) {
                case Long xb -> {
                    if (xb == 0) {
                        throw new TxedtError(backtrace, "cannot mod by 0");
                    }
                    yield xa % xb;
                }
                case Double xb -> {
                    if (xb == 0) {
                        throw new TxedtError(backtrace, "cannot mod by 0");
                    }
                    yield xa % xb;
                }
                case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
            };
            case Double xa -> switch (b) {
                case Long xb -> {
                    if (xb == 0) {
                        throw new TxedtError(backtrace, "cannot mod by 0");
                    }
                    yield xa % xb;
                }
                case Double xb -> {
                    if (xb == 0) {
                        throw new TxedtError(backtrace, "cannot mod by 0");
                    }
                    yield xa % xb;
                }
                case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
        }));

        ctx.put("cat", new ExternalFunction(
                new FunctionSignature()
                        .arg("x")
                        .rest("xs"),
                (backtrace, args) -> {
                    StringBuilder str = new StringBuilder(args.get("x") + "");
                    if (!(args.get("xs") instanceof List<?> xs)) {
                        return null; // this normally shouldn't happen
                    }
                    for (var x : xs) {
                        str.append(x);
                    }
                    return str.toString();
                }
        ));
    }
}
