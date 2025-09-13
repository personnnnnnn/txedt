package org.txedt.libs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.txedt.contexts.Context;
import org.txedt.contexts.ContextPrivilege;
import org.txedt.errors.TxedtError;
import org.txedt.errors.TxedtThrowable;
import org.txedt.functions.ExternalFunction;
import org.txedt.functions.FunctionArgumentType;
import org.txedt.functions.FunctionSignature;
import org.txedt.functions.UserFunction;
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
                throw new TxedtError(new Backtrace(parent, callData.backtrace().description(), args.bounds), "expected var name and optionally value");
            }
            var i = 0;
            var symbolNode = args.children.get(i++);
            if (!(symbolNode instanceof Node.Symbol symbol)) {
                throw new TxedtError(new Backtrace(callData.backtrace(), symbolNode.bounds), "expected var name");
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
                throw new TxedtError(new Backtrace(parent, callData.backtrace().description(), args.bounds), "expected var name and value");
            }
            var i = 0;
            var symbolNode = args.children.get(i++);
            if (!(symbolNode instanceof Node.Symbol symbol)) {
                throw new TxedtError(new Backtrace(callData.backtrace(), symbolNode.bounds), "expected var name");
            }

            var valueNode = args.children.get(i);
            var parent = callData.backtrace().parent() == null ? new Backtrace() : callData.backtrace().parent();
            var value = Interpreter.eval(valueNode, new CallData(parent, callData.context()));
            callData.context().set(callData.backtrace().sameWith(symbol.bounds), ContextPrivilege.PRIVATE, symbol.s, value);
            return value;
        });


        ctx.put("prog", (MacroValue) (callData, args)
                -> Interpreter.exec(args.children, callData));
        ctx.put("progn", (MacroValue) (callData, args)
                -> Interpreter.exec(args.children, new CallData(callData.backtrace(), new Context().parent(callData.context()))));
        ctx.put("progp", (MacroValue) (callData, args)
                -> Interpreter.exec(args.children, new CallData(callData.backtrace(), new Context().param(callData.context()))));


        ctx.put("defn", (MacroValue) (callData, args) -> {
            if (args.children.size() < 2) {
                throw new TxedtError(callData.backtrace().parent(), "expected function name, arguments and body");
            }
            var i = 0;
            var fnNode = args.children.get(i++);
            if (!(fnNode instanceof Node.Symbol fnSymbol)) {
                throw new TxedtError(callData.backtrace().sameWith(fnNode.bounds), "expected function name");
            }

            var argsNode = args.children.get(i++);
            if (!(argsNode instanceof Node.Lst argList)) {
                throw new TxedtError(callData.backtrace().sameWith(argsNode.bounds), "expected arguments");
            }
            var body = args.children.subList(i, args.children.size());

            var sig = parseArgs(callData.backtrace().sameWith(argList.bounds), argList.children);
            var fn = new UserFunction(sig, body, callData.context(), fnSymbol.s);
            callData.context().put(fnSymbol.s, fn);
            return fn;
        });

        ctx.put("fn", (MacroValue) (callData, args) -> {
            if (args.children.isEmpty()) {
                throw new TxedtError(callData.backtrace().parent(), "arguments and body");
            }
            var i = 0;

            var argsNode = args.children.get(i++);
            if (!(argsNode instanceof Node.Lst argList)) {
                throw new TxedtError(callData.backtrace().sameWith(argsNode.bounds), "expected arguments");
            }
            var body = args.children.subList(i, args.children.size());

            var sig = parseArgs(callData.backtrace().sameWith(argList.bounds), argList.children);
            return new UserFunction(sig, body, callData.context(), null);
        });
    }

    private static @NotNull FunctionSignature parseArgs(@NotNull Backtrace backtrace, @NotNull List<Node> args) throws TxedtError {
        var sig = new FunctionSignature();
        var argType = FunctionArgumentType.NORMAL;
        for (var arg : args) {
            if (!(arg instanceof Node.Symbol symb)) {
                throw new TxedtError(backtrace.sameWith(arg.bounds), "expected symbol");
            }
            if (symb.s.equals("&optional")) {
                argType = FunctionArgumentType.OPTIONAL;
                continue;
            }
            if (symb.s.equals("&rest")) {
                argType = FunctionArgumentType.REST;
                continue;
            }
            sig.dynamic(backtrace.sameWith(arg.bounds), argType, symb.s);
        }
        return sig;
    }
}
