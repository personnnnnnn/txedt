package org.txedt.libs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.txedt.contexts.Context;
import org.txedt.contexts.ContextPrivilege;
import org.txedt.errors.TxedtError;
import org.txedt.errors.TxedtThrowable;
import org.txedt.functions.ExternalFunction;
import org.txedt.functions.FunctionSignature;
import org.txedt.interpreter.CallData;
import org.txedt.interpreter.Interpreter;
import org.txedt.macros.MacroValue;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Node;

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
                case Long xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : xa / xb;
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case Double xa -> switch (b) {
                case Long xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : xa / xb;
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
        }));

        ctx.put("%", operation((backtrace, a, b) -> switch (a) {
            case Long xa -> switch (b) {
                case Long xb -> xb == 0 ? 0 : xa % xb;
                case Double xb -> xb == 0 ? 0 : xa % xb;
                case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
            };
            case Double xa -> switch (b) {
                case Long xb -> xb == 0 ? 0 : xa % xb;
                case Double xb -> xb == 0 ? 0 : xa % xb;
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

        ctx.put("var", (MacroValue) (callData, args) -> {
            if (args.children.size() != 1 && args.children.size() != 2) {
                var parent = callData.backtrace().parent() == null ? new Backtrace() : callData.backtrace().parent();
                throw new TxedtThrowable(new Backtrace(parent, args.bounds), "expected var name and optionally value");
            }
            var i = 0;
            var symbolNode = args.children.get(i++);
            if (!(symbolNode instanceof Node.Symbol symbol)) {
                throw new TxedtThrowable(new Backtrace(callData.backtrace(), symbolNode.bounds), "expected var name");
            }

            if (args.children.size() == 1) {
                callData.context().put(symbol.s, null);
                return null;
            }

            var valueNode = args.children.get(i);
            var parent = callData.backtrace().parent() == null ? new Backtrace() : callData.backtrace().parent();
            var value = Interpreter.eval(valueNode, new CallData(parent, callData.context()));
            callData.context().put(symbol.s, value);
            return value;
        });

        ctx.put("set", (MacroValue) (callData, args) -> {
            if (args.children.size() != 2) {
                var parent = callData.backtrace().parent() == null ? new Backtrace() : callData.backtrace().parent();
                throw new TxedtThrowable(new Backtrace(parent, args.bounds), "expected var name and value");
            }
            var i = 0;
            var symbolNode = args.children.get(i++);
            if (!(symbolNode instanceof Node.Symbol symbol)) {
                throw new TxedtThrowable(new Backtrace(callData.backtrace(), symbolNode.bounds), "expected var name");
            }

            var valueNode = args.children.get(i);
            var parent = callData.backtrace().parent() == null ? new Backtrace() : callData.backtrace().parent();
            var value = Interpreter.eval(valueNode, new CallData(parent, callData.context()));
            callData.context().set(new Backtrace(callData.backtrace().parent(), symbol.bounds), ContextPrivilege.PRIVATE, symbol.s, value);
            return value;
        });
    }
}
