package org.txedt.libs;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.contexts.Context;
import org.txedt.contexts.ContextPrivilege;
import org.txedt.errors.TxedtError;
import org.txedt.errors.TxedtThrowable;
import org.txedt.functions.*;
import org.txedt.functions.signature.FunctionArgumentType;
import org.txedt.functions.signature.FunctionSignature;
import org.txedt.general.Equality;
import org.txedt.general.TxedtBool;
import org.txedt.general.TxedtStr;
import org.txedt.interpreter.CallData;
import org.txedt.interpreter.Interpreter;
import org.txedt.macros.ExternalMacro;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Node;
import org.txedt.properties.PropertyValue;

import java.util.ArrayList;
import java.util.List;

public final class Std {
    public static final Context ctx;
    private Std() { }

    public interface BinaryOperation {
        Object op(Backtrace backtrace, Object a, Object b) throws TxedtThrowable;
    }

    public interface GroupOperation {
        Object op(Backtrace backtrace, Object a, Object b) throws TxedtThrowable;
        Object combine(Backtrace backtrace, Object a, Object b) throws TxedtThrowable;
        boolean done(Object x);
    }

    @Contract("_, _ -> new")
    public static @NotNull ExternalFunction binaryOperation(String name, BinaryOperation operation) {
        return new ExternalFunction(
            name,
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

    @Contract("_, _ -> new")
    public static @NotNull ExternalFunction groupOperation(String name, GroupOperation operation) {
        return new ExternalFunction(
                name,
                new FunctionSignature()
                        .arg("x")
                        .arg("y")
                        .rest("xs"),
                (backtrace, args) -> {
                    var x = args.get("x");
                    var y = args.get("y");
                    if (!(args.get("xs") instanceof List<?> xs)) {
                        return null; // this normally shouldn't happen
                    }
                    var ret = operation.op(backtrace, x, y);
                    for (var v : xs) {
                        if (operation.done(ret)) {
                            return ret;
                        }
                        var res = operation.op(backtrace, y, v);
                        y = v;
                        ret = operation.combine(backtrace, ret, res);
                    }
                    return ret;
                }
        );
    }

    static {
        ctx = new Context();

        ctx.put("~", new ExternalFunction(
                "~",
                new FunctionSignature().arg("x"),
                (backtrace, args) -> {
                    if (!(args.get("x") instanceof Integer x)) {
                        throw new TxedtError(backtrace, "can only '~' ints");
                    }
                    return ~x;
                }
        ));

        ctx.put("&", binaryOperation("&", (backtrace, a, b) -> {
            if (!(a instanceof Integer xa)) {
                throw new TxedtError(backtrace, "can only '&' ints");
            }
            if (!(b instanceof Integer xb)) {
                throw new TxedtError(backtrace, "can only '&' ints");
            }
            return xa & xb;
        }));


        ctx.put("&~", binaryOperation("&~", (backtrace, a, b) -> {
            if (!(a instanceof Integer xa)) {
                throw new TxedtError(backtrace, "can only '&~' ints");
            }
            if (!(b instanceof Integer xb)) {
                throw new TxedtError(backtrace, "can only '&~' ints");
            }
            return xa & ~xb;
        }));

        ctx.put("|", binaryOperation("|", (backtrace, a, b) -> {
            if (!(a instanceof Integer xa)) {
                throw new TxedtError(backtrace, "can only '|' ints");
            }
            if (!(b instanceof Integer xb)) {
                throw new TxedtError(backtrace, "can only '|' ints");
            }
            return xa | xb;
        }));

        ctx.put("^", binaryOperation("^", (backtrace, a, b) -> {
            if (!(a instanceof Integer xa)) {
                throw new TxedtError(backtrace, "can only '^' ints");
            }
            if (!(b instanceof Integer xb)) {
                throw new TxedtError(backtrace, "can only '^' ints");
            }
            return xa ^ xb;
        }));

        ctx.put("<<", binaryOperation("<<", (backtrace, a, b) -> {
            if (!(a instanceof Integer xa)) {
                throw new TxedtError(backtrace, "can only '<<' ints");
            }
            if (!(b instanceof Integer xb)) {
                throw new TxedtError(backtrace, "can only '<<' ints");
            }
            return xa << xb;
        }));

        ctx.put(">>", binaryOperation(">>", (backtrace, a, b) -> {
            if (!(a instanceof Integer xa)) {
                throw new TxedtError(backtrace, "can only '>>' ints");
            }
            if (!(b instanceof Integer xb)) {
                throw new TxedtError(backtrace, "can only '>>' ints");
            }
            return xa >>> xb;
        }));

        ctx.put(">>-", binaryOperation(">>>", (backtrace, a, b) -> {
            if (!(a instanceof Integer xa)) {
                throw new TxedtError(backtrace, "can only '>>-' ints");
            }
            if (!(b instanceof Integer xb)) {
                throw new TxedtError(backtrace, "can only '>>-' ints");
            }
            return xa >> xb;
        }));

        ctx.put("+", binaryOperation("+", (backtrace, a, b) -> switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xa + xb;
                case Double xb -> xa + xb;
                case null, default -> throw new TxedtError(backtrace, "can only add numbers");
            };
            case Double xa -> switch (b) {
                case Integer xb -> xa + xb;
                case Double xb -> xa + xb;
                case null, default -> throw new TxedtError(backtrace, "can only add numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only add numbers");
        }));

        ctx.put("-", binaryOperation("-", (backtrace, a, b) -> switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xa - xb;
                case Double xb -> xa - xb;
                case null, default -> throw new TxedtError(backtrace, "can only subtract numbers");
            };
            case Double xa -> switch (b) {
                case Integer xb -> xa - xb;
                case Double xb -> xa - xb;
                case null, default -> throw new TxedtError(backtrace, "can only subtract numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only subtract numbers");
        }));

        ctx.put("*", binaryOperation("*", (backtrace, a, b) -> switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xa * xb;
                case Double xb -> xa * xb;
                case null, default -> throw new TxedtError(backtrace, "can only multiply numbers");
            };
            case Double xa -> switch (b) {
                case Integer xb -> xa * xb;
                case Double xb -> xa * xb;
                case null, default -> throw new TxedtError(backtrace, "can only multiply numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only multiply numbers");
        }));

        ctx.put("/", binaryOperation("/", (backtrace, a, b) -> switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : xa / xb;
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case Double xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : xa / xb;
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
        }));

        ctx.put("floor/", binaryOperation("floor/", (backtrace, a, b) -> switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : (int) (xa / xb);
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case Double xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : (int) (xa / xb);
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
        }));

        ctx.put("ceil/", binaryOperation("ceil/", (backtrace, a, b) -> switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : (int) Math.ceil(xa / xb);
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case Double xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : (int) Math.ceil(xa / xb);
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
        }));

        ctx.put("round/", binaryOperation("round/", (backtrace, a, b) -> switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : (int) Math.round(xa / xb);
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case Double xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa / xb;
                case Double xb -> xb == 0 ? 0 : (int) Math.round(xa / xb);
                case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only divide numbers");
        }));

        ctx.put("%", binaryOperation("%", (backtrace, a, b) -> switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : Math.abs(xa % xb);
                case Double xb -> xb == 0 ? 0 : Math.abs(xa % xb);
                case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
            };
            case Double xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : Math.abs(xa % xb);
                case Double xb -> xb == 0 ? 0 : Math.abs(xa % xb);
                case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
        }));

        ctx.put("%-", binaryOperation("%-", (backtrace, a, b) -> switch (a) {
            case Integer xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa % xb;
                case Double xb -> xb == 0 ? 0 : xa % xb;
                case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
            };
            case Double xa -> switch (b) {
                case Integer xb -> xb == 0 ? 0 : xa % xb;
                case Double xb -> xb == 0 ? 0 : xa % xb;
                case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
            };
            case null, default -> throw new TxedtError(backtrace, "can only mod numbers");
        }));

        ctx.put("fraction", new ExternalFunction(
                "fraction",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> 0.0;
                    case Double d -> Math.abs(d % 1);
                    case null, default -> throw new TxedtError(backt, "can only get the fraction of numbers");
                }
        ));

        ctx.put("cat", new ExternalFunction(
                "cat",
                new FunctionSignature()
                        .arg("x")
                        .rest("xs"),
                (backtrace, args) -> {
                    StringBuilder str = new StringBuilder(TxedtStr.toString(args.get("x")));
                    if (!(args.get("xs") instanceof List<?> xs)) {
                        return null; // this normally shouldn't happen
                    }
                    for (var x : xs) {
                        str.append(TxedtStr.toString(x));
                    }
                    return str.toString();
                }
        ));

        ctx.put("is", groupOperation("is", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a == b);
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public boolean done(Object x) {
                return x == null;
            }
        }));

        ctx.put("isnt", groupOperation("isnt", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != b);
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public boolean done(Object x) {
                return x == null;
            }
        }));

        ctx.put("=", groupOperation("=", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(Equality.equals(a, b));
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public boolean done(Object x) {
                return x == null;
            }
        }));

        ctx.put("!=", groupOperation("!=", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(!Equality.equals(a, b));
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public boolean done(Object x) {
                return x == null;
            }
        }));

        ctx.put("<", groupOperation("<", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) throws TxedtError {
                return switch (a) {
                    case Integer xa -> switch (b) {
                        case Integer xb -> TxedtBool.to(xa < xb);
                        case Double xb -> TxedtBool.to(xa < xb);
                        case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                    };
                    case Double xa -> switch (b) {
                        case Integer xb -> TxedtBool.to(xa < xb);
                        case Double xb -> TxedtBool.to(xa < xb);
                        case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                    };
                    case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                };
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public boolean done(Object x) {
                return x == null;
            }
        }));

        ctx.put(">", groupOperation(">", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) throws TxedtError {
                return switch (a) {
                    case Integer xa -> switch (b) {
                        case Integer xb -> TxedtBool.to(xa > xb);
                        case Double xb -> TxedtBool.to(xa > xb);
                        case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                    };
                    case Double xa -> switch (b) {
                        case Integer xb -> TxedtBool.to(xa > xb);
                        case Double xb -> TxedtBool.to(xa > xb);
                        case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                    };
                    case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                };
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public boolean done(Object x) {
                return x == null;
            }
        }));

        ctx.put("<=", groupOperation("<=", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) throws TxedtError {
                return switch (a) {
                    case Integer xa -> switch (b) {
                        case Integer xb -> TxedtBool.to(xa <= xb);
                        case Double xb -> TxedtBool.to(xa <= xb);
                        case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                    };
                    case Double xa -> switch (b) {
                        case Integer xb -> TxedtBool.to(xa <= xb);
                        case Double xb -> TxedtBool.to(xa <= xb);
                        case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                    };
                    case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                };
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public boolean done(Object x) {
                return x == null;
            }
        }));

        ctx.put(">=", groupOperation(">=", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) throws TxedtError {
                return switch (a) {
                    case Integer xa -> switch (b) {
                        case Integer xb -> TxedtBool.to(xa >= xb);
                        case Double xb -> TxedtBool.to(xa >= xb);
                        case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                    };
                    case Double xa -> switch (b) {
                        case Integer xb -> TxedtBool.to(xa >= xb);
                        case Double xb -> TxedtBool.to(xa >= xb);
                        case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                    };
                    case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                };
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public boolean done(Object x) {
                return x == null;
            }
        }));

        ctx.put("and", groupOperation("and", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null && b != null);
            }

            @Override
            public boolean done(Object x) {
                return x == null;
            }
        }));

        ctx.put("or", groupOperation("or", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null || b != null);
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null || b != null);
            }

            @Override
            public boolean done(Object x) {
                return x != null;
            }
        }));

        ctx.put("xor", groupOperation("xor", new GroupOperation() {
            @Override
            public Object op(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null ^ b != null);
            }

            @Override
            public Object combine(Backtrace backtrace, Object a, Object b) {
                return TxedtBool.to(a != null ^ b != null);
            }

            @Override
            public boolean done(Object x) {
                return false;
            }
        }));

        ctx.put("not", new ExternalFunction(
                "not",
                new FunctionSignature().arg("x"),
                (backtrace, args) -> TxedtBool.to(args.get("x") == null)
        ));

        ctx.put("zero?", new ExternalFunction(
                "zero?",
                new FunctionSignature().arg("x"),
                (backtrace, args) -> {
                    if (!(args.get("x") instanceof Number x)) {
                        throw new TxedtError(backtrace, "can only compare numbers");
                    }
                    return TxedtBool.to(Equality.equals(x, 0));
                }
        ));

        ctx.put("non-zero?", new ExternalFunction(
                "non-zero?",
                new FunctionSignature().arg("x"),
                (backtrace, args) -> {
                    if (!(args.get("x") instanceof Number x)) {
                        throw new TxedtError(backtrace, "can only compare numbers");
                    }
                    return TxedtBool.to(!Equality.equals(x, 0));
                }
        ));

        ctx.put("<0?", new ExternalFunction(
                "<0?",
                new FunctionSignature().arg("x"),
                (backtrace, args) -> switch (args.get("x")) {
                    case Integer i -> i < 0;
                    case Double d -> d < 0;
                    case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                }
        ));

        ctx.put("<=0?", new ExternalFunction(
                "<=0?",
                new FunctionSignature().arg("x"),
                (backtrace, args) -> switch (args.get("x")) {
                    case Integer i -> i <= 0;
                    case Double d -> d <= 0;
                    case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                }
        ));

        ctx.put(">0?", new ExternalFunction(
                ">0?",
                new FunctionSignature().arg("x"),
                (backtrace, args) -> switch (args.get("x")) {
                    case Integer i -> i > 0;
                    case Double d -> d > 0;
                    case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                }
        ));

        ctx.put(">=0?", new ExternalFunction(
                ">=0?",
                new FunctionSignature().arg("x"),
                (backtrace, args) -> switch (args.get("x")) {
                    case Integer i -> i >= 0;
                    case Double d -> d >= 0;
                    case null, default -> throw new TxedtError(backtrace, "can only compare numbers");
                }
        ));

        ctx.put("list", new ExternalFunction(
                "list",
                new FunctionSignature().rest("items"),
                (backtrace, args) -> args.get("items")
        ));

        ctx.put("var", new ExternalMacro(
                "var",
                (callData, args) -> {
                    if (args.children.size() != 1 && args.children.size() != 2) {
                        throw new TxedtError(callData.backtrace(), "expected variable name and optionally value");
                    }
                    var varNameNode = args.children.getFirst();
                    if (!(varNameNode instanceof Node.Symbol varSymbol)) {
                        throw new TxedtError(Backtrace.sameWithParent(callData.backtrace(), varNameNode.bounds), "expected symbol");
                    }
                    if (args.children.size() == 1) {
                        callData.context().putLib(callData.backtrace(), ContextPrivilege.PRIVATE, varSymbol.s, null);
                        return null;
                    }
                    var exprNode = args.children.getLast();
                    var expr = Interpreter.eval(exprNode, new CallData(Backtrace.sameWithParent(callData.backtrace(), exprNode.bounds), callData.context()));
                    callData.context().putLib(callData.backtrace(), ContextPrivilege.PRIVATE, varSymbol.s, expr);
                    return expr;
                }
        ));

        ctx.put("set", new ExternalMacro(
                "set",
                (callData, args) -> {
                    if (args.children.size() != 2) {
                        throw new TxedtError(callData.backtrace(), "expected variable name and value");
                    }
                    var varNameNode = args.children.getFirst();
                    if (varNameNode instanceof Node.Lst list) {
                        if (list.children.isEmpty()) {
                            throw new TxedtError(Backtrace.sameWith(callData.backtrace(), list.bounds), "expected property");
                        }

                        var propNode = list.children.getFirst();
                        var propBackt = Backtrace.sameWith(callData.backtrace(), propNode.bounds);
                        var propValue = Interpreter.eval(propNode, new CallData(propBackt, callData.context()));
                        if (!(propValue instanceof PropertyValue prop)) {
                            throw new TxedtError(propBackt, "expected property");
                        }

                        var rest = list.sublist(1);
                        rest.children.add(args.children.getLast());
                        var argv = Interpreter.toArgv(rest.children, new CallData(Backtrace.sameWith(callData.backtrace(), rest.bounds), callData.context()));
                        return prop.set().call(new Backtrace(callData.backtrace(), prop.name(), list.bounds), argv);
                    }

                    if (!(varNameNode instanceof Node.Symbol varSymbol)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), varNameNode.bounds), "expected symbol");
                    }
                    var exprNode = args.children.getLast();
                    var expr = Interpreter.eval(exprNode, new CallData(Backtrace.sameWithParent(callData.backtrace(), exprNode.bounds), callData.context()));
                    callData.context().setLib(callData.backtrace(), ContextPrivilege.PRIVATE, varSymbol.s, expr);
                    return expr;
                }
        ));

        ctx.put("if", new ExternalMacro(
                "if",
                (callData, args) -> {
                    if (args.children.size() != 3) {
                        throw new TxedtError(callData.backtrace(), "expected condition, true value and false value");
                    }
                    var condNode = args.children.getFirst();
                    var cond = Interpreter.eval(condNode, new CallData(Backtrace.sameWithParent(callData.backtrace(), condNode.bounds), callData.context()));
                    var checkNode = cond == null ? args.children.getLast() : args.children.get(1);
                    return Interpreter.eval(checkNode, new CallData(Backtrace.sameWithParent(callData.backtrace(), checkNode.bounds), callData.context()));
                }
        ));

        ctx.put("when", new ExternalMacro(
                "when",
                (callData, args) -> {
                    if (args.children.isEmpty()) {
                        throw new TxedtError(callData.backtrace(), "expected condition and body");
                    }
                    var condNode = args.children.getFirst();
                    var cond = Interpreter.eval(condNode, new CallData(Backtrace.sameWithParent(callData.backtrace(), condNode.bounds), callData.context()));
                    if (cond == null) {
                        return null;
                    }

                    var rest = args.sublist(1);
                    return Interpreter.exec(rest.children, new CallData(Backtrace.sameWithParent(callData.backtrace(), rest.bounds), callData.context()), null);
                }
        ));

        ctx.put("unless", new ExternalMacro(
                "unless",
                (callData, args) -> {
                    if (args.children.isEmpty()) {
                        throw new TxedtError(callData.backtrace(), "expected condition and body");
                    }
                    var condNode = args.children.getFirst();
                    var cond = Interpreter.eval(condNode, new CallData(Backtrace.sameWithParent(callData.backtrace(), condNode.bounds), callData.context()));
                    if (cond != null) {
                        return null;
                    }

                    var rest = args.sublist(1);
                    return Interpreter.exec(rest.children, new CallData(Backtrace.sameWithParent(callData.backtrace(), rest.bounds), callData.context()), null);
                }
        ));

        ctx.put("cond", new ExternalMacro(
                "cond",
                (callData, args) -> {
                    for (var arg : args.children) {
                        if (!(arg instanceof Node.Lst list)) {
                            throw new TxedtError(Backtrace.sameWith(callData.backtrace(), args.bounds), "expected list");
                        }
                        if (list.children.isEmpty()) {
                            throw new TxedtError(Backtrace.sameWith(callData.backtrace(), args.bounds), "expected condition and body");
                        }
                        var condNode = list.children.getFirst();
                        var cond = Interpreter.eval(condNode, new CallData(Backtrace.sameWithParent(callData.backtrace(), condNode.bounds), callData.context()));
                        if (cond == null) {
                            continue;
                        }
                        var rest = list.sublist(1);
                        return Interpreter.exec(rest.children, new CallData(Backtrace.sameWithParent(callData.backtrace(), rest.bounds), callData.context()));
                    }
                    return null;
                }
        ));

        ctx.put("given", new ExternalMacro(
                "given",
                (callData, args) -> {
                    if (args.children.size() != 1) {
                        throw new TxedtError(callData.backtrace(), "expected variable name");
                    }
                    var symbolNode = args.children.getFirst();
                    var backt = Backtrace.sameWithParent(callData.backtrace(), symbolNode.bounds);
                    if (!(symbolNode instanceof Node.Symbol symbol)) {
                        throw new TxedtError(backt, "expected symbol");
                    }
                    return TxedtBool.to(callData.context().hasImmLib(backt, ContextPrivilege.PRIVATE, symbol.s));
                }
        ));

        ctx.put("exists", new ExternalMacro(
                "exists",
                (callData, args) -> {
                    if (args.children.size() != 1) {
                        throw new TxedtError(callData.backtrace(), "expected variable name");
                    }
                    var symbolNode = args.children.getFirst();
                    var backt = Backtrace.sameWithParent(callData.backtrace(), symbolNode.bounds);
                    if (!(symbolNode instanceof Node.Symbol symbol)) {
                        throw new TxedtError(backt, "expected symbol");
                    }
                    return TxedtBool.to(callData.context().hasLib(backt, ContextPrivilege.PRIVATE, symbol.s));
                }
        ));

        ctx.put("prog", new ExternalMacro(
                "prog",
                (callData, args) -> Interpreter.exec(args.children, new CallData(Backtrace.sameWithParent(callData.backtrace(), args.bounds), callData.context()))
        ));

        ctx.put("progn", new ExternalMacro(
                "progn",
                (callData, args)
                        -> Interpreter.exec(args.children, new CallData(Backtrace.sameWithParent(callData.backtrace(), args.bounds), new Context().parent(callData.context())))
        ));

        ctx.put("progp", new ExternalMacro(
                "progp",
                (callData, args)
                        -> Interpreter.exec(args.children, new CallData(Backtrace.sameWithParent(callData.backtrace(), args.bounds), new Context().param(callData.context())))
        ));

        ctx.put("block", new ExternalMacro(
                "block",
                (callData, args) -> {
                    if (args.children.isEmpty()) {
                        throw new TxedtError(callData.backtrace(), "expected block name and body");
                    }
                    var blockNameNode = args.children.getFirst();
                    if (!(blockNameNode instanceof Node.Symbol blockSymbol)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), blockNameNode.bounds), "expected block name");
                    }
                    var rest = args.sublist(1);
                    return Interpreter.exec(rest.children, new CallData(Backtrace.sameWithParent(callData.backtrace(), rest.bounds), callData.context()), blockSymbol.s);
                }
        ));

        ctx.put("blockn", new ExternalMacro(
                "blockn",
                (callData, args) -> {
                    if (args.children.isEmpty()) {
                        throw new TxedtError(callData.backtrace(), "expected block name and body");
                    }
                    var blockNameNode = args.children.getFirst();
                    if (!(blockNameNode instanceof Node.Symbol blockSymbol)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), blockNameNode.bounds), "expected block name");
                    }
                    var rest = args.sublist(1);
                    return Interpreter.exec(rest.children, new CallData(Backtrace.sameWithParent(callData.backtrace(), rest.bounds), new Context().parent(callData.context())), blockSymbol.s);
                }
        ));
        ctx.put("blockp", new ExternalMacro(
                "blockp",
                (callData, args) -> {
                    if (args.children.isEmpty()) {
                        throw new TxedtError(callData.backtrace(), "expected block name and body");
                    }
                    var blockNameNode = args.children.getFirst();
                    if (!(blockNameNode instanceof Node.Symbol blockSymbol)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), blockNameNode.bounds), "expected block name");
                    }
                    var rest = args.sublist(1);
                    return Interpreter.exec(rest.children, new CallData(Backtrace.sameWithParent(callData.backtrace(), rest.bounds), new Context().param(callData.context())), blockSymbol.s);
                }
        ));

        ctx.put("this-ctx", new ExternalMacro(
                "this-ctx",
                (callData, args) -> {
                    if (!args.children.isEmpty()) {
                        throw new TxedtError(callData.backtrace(), "expected no arguments");
                    }
                    return callData.context();
                }
        ));

        ctx.put("return", new ExternalFunction(
                "return",
                new FunctionSignature().opt("return-value"),
                (backt, args) -> {
                    var ret = args.getOrDefault("return-value", null);
                    throw new ReturnThrowable(backt, ret, null);
                }
        ));

        ctx.put("return-from", new ExternalMacro(
                "return-from",
                (callData, args) -> {
                    if (args.children.size() != 1 && args.children.size() != 2) {
                        throw new TxedtError(callData.backtrace(), "expected block name and optionally return value");
                    }
                    var symbolNode = args.children.getFirst();
                    if (!(symbolNode instanceof Node.Symbol symbol)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), symbolNode.bounds), "expected block name");
                    }
                    if (args.children.size() == 1) {
                        throw new ReturnThrowable(callData.backtrace(), null, symbol.s);
                    }
                    var exprNode = args.children.getLast();
                    var expr = Interpreter.eval(exprNode, new CallData(Backtrace.sameWithParent(callData.backtrace(), exprNode.bounds), callData.context()));
                    throw new ReturnThrowable(callData.backtrace(), expr, symbol.s);
                }
        ));

        ctx.put("list-idx", new PropertyValue(
                "list-idx",
                new ExternalFunction("list-idx", new FunctionSignature().arg("list").arg("idx"), (backt, args) -> {
                    if (!(args.get("list") instanceof List<?> list)) {
                        throw new TxedtError(backt, "'list' must be a list");
                    }
                    if (!(args.get("idx") instanceof Integer idx)) {
                        throw new TxedtError(backt, "'idx' must be an int");
                    }
                    if (idx >= list.size()) {
                        throw new TxedtError(backt, "can't index over list length (idx " + idx + ", len " + list.size() + ")");
                    }
                    if (idx < 0) {
                        throw new TxedtError(backt, "can't index with negatives (" + idx + ")");
                    }
                    return list.get(idx);
                }),
                new ExternalFunction("list-idx", new FunctionSignature().arg("list").arg("idx").arg("value"), (backt, args) -> {
                    if (!(args.get("list") instanceof List<?> rawList)) {
                        throw new TxedtError(backt, "'list' must be a list");
                    }
                    @SuppressWarnings("unchecked")
                    var list = (List<Object>) rawList;
                    if (!(args.get("idx") instanceof Integer idx)) {
                        throw new TxedtError(backt, "'idx' must be an int");
                    }
                    var value = args.get("value");
                    if (idx >= list.size()) {
                        throw new TxedtError(backt, "can't index over list length (idx " + idx + ", len " + list.size() + ")");
                    }
                    if (idx < 0) {
                        throw new TxedtError(backt, "can't index with negatives (" + idx + ")");
                    }
                    list.set(idx, value);
                    return value;
                })
        ));

        ctx.put("list-len", new PropertyValue(
                "list-len",
                new ExternalFunction("list-len", new FunctionSignature().arg("list"), (backt, args) -> {
                    if (!(args.get("list") instanceof List<?> list)) {
                        throw new TxedtError(backt, "'list' must be a list");
                    }
                    return list.size();
                }),
                new ExternalFunction("list-len",
                        new FunctionSignature().arg("list").opt("fill").arg("value"),
                        (backt, args) -> {
                            if (!(args.get("list") instanceof List<?> rawList)) {
                                throw new TxedtError(backt, "'list' must be a list");
                            }
                            if (!(args.get("value") instanceof Integer value)) {
                                throw new TxedtError(backt, "'list-len' value must be an int");
                            }

                            @SuppressWarnings("unchecked")
                            var list = (List<Object>) rawList;

                            var currentSize = list.size();

                            if (currentSize == value) {
                                return value;
                            }

                            if (currentSize > value) {
                                list.subList(value, currentSize).clear();
                            } else {
                                if (list instanceof ArrayList<Object> arrayList) {
                                    arrayList.ensureCapacity(value);
                                }
                                Object fill = args.getOrDefault("fill", null);
                                for (int i = currentSize; i < value; i++) {
                                    list.add(fill);
                                }
                            }

                            return value;
                        }
                )
        ));

        ctx.put("loop", new ExternalMacro(
                "loop",
                (callData, args) -> {
                    var backt = Backtrace.sameWithParent(callData.backtrace(), args.bounds);
                    var callDat = new CallData(backt, callData.context());
                    for (;;) {
                        try {
                            Interpreter.exec(args.children, callDat, null);
                        } catch (ReturnThrowable e) {
                            if (e.target == null) {
                                return e.returnValue;
                            }
                            throw e;
                        }
                    }
                }
        ));

        ctx.put("loopb", new ExternalMacro(
                "loopb",
                (callData, args) -> {
                    if (!(!args.children.isEmpty() && args.children.getFirst() instanceof Node.Symbol symbol)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), args.children.getFirst().bounds), "expected block name");
                    }
                    var body = args.sublist(1);
                    var backt = Backtrace.sameWithParent(callData.backtrace(), body.bounds);
                    var callDat = new CallData(backt, callData.context());
                    for (;;) {
                        try {
                            Interpreter.exec(body.children, callDat);
                        } catch (ReturnThrowable e) {
                            if (e.target == null || e.target.equals(symbol.s)) {
                                return e.returnValue;
                            }
                            throw e;
                        }
                    }
                }
        ));

        ctx.put("str-len", new ExternalFunction(
                "str-len",
                new FunctionSignature().arg("s"),
                (backt, args) -> {
                    if (!(args.get("s") instanceof String s)) {
                        throw new TxedtError(backt, "expected 's' to be a string");
                    }
                    return s.length();
                }
        ));

        ctx.put("str-idx", new ExternalFunction(
                "str-idx",
                new FunctionSignature().arg("s").arg("i"),
                (backt, args) -> {
                    if (!(args.get("s") instanceof String s)) {
                        throw new TxedtError(backt, "expected 's' to be a string");
                    }
                    if (!(args.get("i") instanceof Integer i)) {
                        throw new TxedtError(backt, "expected 'i' to be an int");
                    }
                    if (i < 0) {
                        throw new TxedtError(backt, "cannot index with negative numbers (" + i + ")");
                    }
                    if (i >= s.length()) {
                        throw new TxedtError(backt, "cannot index with numbers >= the length of the string (" + i + ", length " + s.length() + ")");
                    }
                    return s.charAt(i) + "";
                }
        ));

        ctx.put("str-code", new ExternalFunction(
                "str-code",
                new FunctionSignature().arg("s").opt("i"),
                (backt, args) -> {
                    if (!(args.get("s") instanceof String s)) {
                        throw new TxedtError(backt, "expected 's' to be a string");
                    }
                    if (!(args.getOrDefault("i", 0) instanceof Integer i)) {
                        throw new TxedtError(backt, "expected 'i' to be an int");
                    }
                    if (i < 0) {
                        throw new TxedtError(backt, "cannot index with negative numbers (" + i + ")");
                    }
                    if (i >= s.length()) {
                        throw new TxedtError(backt, "cannot index with numbers >= the length of the string (" + i + ", length " + s.length() + ")");
                    }
                    return s.codePointAt(i);
                }
        ));

        ctx.put("code-str", new ExternalFunction(
                "code-str",
                new FunctionSignature().arg("i"),
                (backt, args) -> {
                    if (!(args.getOrDefault("i", 0) instanceof Integer i)) {
                        throw new TxedtError(backt, "expected 'i' to be an int");
                    }
                    return "" + (char) (int) i;
                }
        ));

        ctx.put("round", new ExternalFunction(
                "round",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> i;
                    case Double d -> Math.round(d);
                    case null, default -> throw new TxedtError(backt, "can only round numbers");
                }
        ));

        ctx.put("floor", new ExternalFunction(
                "floor",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> i;
                    case Double d -> Math.floor(d);
                    case null, default -> throw new TxedtError(backt, "can only floor numbers");
                }
        ));

        ctx.put("ceil", new ExternalFunction(
                "ceil",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> i;
                    case Double d -> Math.ceil(d);
                    case null, default -> throw new TxedtError(backt, "can only ceil numbers");
                }
        ));

        ctx.put("int", new ExternalFunction(
                "int",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> i;
                    case Double d -> (int) (double) d;
                    case null, default -> throw new TxedtError(backt, "can only convert numbers to ints");
                }
        ));

        ctx.put("float", new ExternalFunction(
                "float",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> (double) (int) i;
                    case Double d -> (double) d;
                    case null, default -> throw new TxedtError(backt, "can only convert numbers to floats");
                }
        ));

        ctx.put("t", TxedtBool.tru);
        ctx.put("nil", null);

        ctx.put("nil?", new ExternalFunction(
                "nil?",
                new FunctionSignature().arg("x"),
                (backt, args) -> TxedtBool.to(args.get("x") == null)
        ));

        ctx.put("non-nil?", new ExternalFunction(
                "non-nil?",
                new FunctionSignature().arg("x"),
                (backt, args) -> TxedtBool.to(args.get("x") != null)
        ));

        ctx.put("int?", new ExternalFunction(
                "int?",
                new FunctionSignature().arg("x"),
                (backt, args) -> args.get("x") instanceof Integer
        ));

        ctx.put("float?", new ExternalFunction(
                "float?",
                new FunctionSignature().arg("x"),
                (backt, args) -> args.get("x") instanceof Double
        ));

        ctx.put("number?", new ExternalFunction(
                "number?",
                new FunctionSignature().arg("x"),
                (backt, args) -> args.get("x") instanceof Double || args.get("x") instanceof Integer
        ));

        ctx.put("string?", new ExternalFunction(
                "string?",
                new FunctionSignature().arg("x"),
                (backt, args) -> args.get("x") instanceof String
        ));

        ctx.put("context?", new ExternalFunction(
                "context?",
                new FunctionSignature().arg("x"),
                (backt, args) -> args.get("x") instanceof Context
        ));

        ctx.put("list?", new ExternalFunction(
                "list?",
                new FunctionSignature().arg("x"),
                (backt, args) -> args.get("x") instanceof List<?>
        ));

        ctx.put("1+", new ExternalFunction(
                "1+",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> i + 1;
                    case Double d -> d + 1;
                    case null, default -> throw new TxedtError(backt, "can only add 1 to numbers");
                }
        ));

        ctx.put("1-", new ExternalFunction(
                "1-",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> i - 1;
                    case Double d -> d - 1;
                    case null, default -> throw new TxedtError(backt, "can only subtract 1 from numbers");
                }
        ));

        ctx.put("-1*", new ExternalFunction(
                "-1*",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> -i;
                    case Double d -> -d;
                    case null, default -> throw new TxedtError(backt, "can only negate numbers");
                }
        ));

        ctx.put("abs", new ExternalFunction(
                "abs",
                new FunctionSignature().arg("x"),
                (backt, args) -> switch (args.get("x")) {
                    case Integer i -> Math.abs(i);
                    case Double d -> Math.abs(d);
                    case null, default -> throw new TxedtError(backt, "can only abs numbers");
                }
        ));
    }

    private static @NotNull FunctionSignature parseArgs(@Nullable Backtrace backtrace, @NotNull List<Node> args) throws TxedtError {
        var sig = new FunctionSignature();
        var argType = FunctionArgumentType.NORMAL;
        for (var arg : args) {
            if (!(arg instanceof Node.Symbol symb)) {
                throw new TxedtError(Backtrace.sameWith(backtrace, arg.bounds), "expected symbol");
            }
            if (symb.s.equals("&optional")) {
                argType = FunctionArgumentType.OPTIONAL;
                continue;
            }
            if (symb.s.equals("&rest")) {
                argType = FunctionArgumentType.REST;
                continue;
            }
            sig.dynamic(Backtrace.sameWith(backtrace, arg.bounds), argType, symb.s);
            argType = FunctionArgumentType.NORMAL;
        }

        return sig;
    }

    static {
        ctx.put("defn", new ExternalMacro(
                "defn",
                (callData, args) -> {
                    if (args.children.size() < 2) {
                        throw new TxedtError(callData.backtrace(), "expected function name, arguments and body");
                    }
                    var funcNode = args.children.getFirst();
                    if (!(funcNode instanceof Node.Symbol funcSymbol)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), funcNode.bounds), "expected function name");
                    }
                    var argsNode = args.children.get(1);
                    if (!(argsNode instanceof Node.Lst argsList)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), argsNode.bounds), "expected arguments");
                    }
                    var body = args.sublist(2);
                    var sig = parseArgs(Backtrace.sameWith(callData.backtrace(), argsList.bounds), argsList.children);
                    var fn = new UserFunction(sig, body.children, callData.context(), funcSymbol.s);
                    callData.context().putLib(callData.backtrace(), ContextPrivilege.PRIVATE, funcSymbol.s, fn);
                    return fn;
                }
        ));

        ctx.put("fn", new ExternalMacro(
                "fn",
                (callData, args) -> {
                    if (args.children.isEmpty()) {
                        throw new TxedtError(callData.backtrace(), "arguments and body");
                    }
                    var argsNode = args.children.getFirst();
                    if (!(argsNode instanceof Node.Lst argsList)) {
                        throw new TxedtError(Backtrace.sameWith(callData.backtrace(), argsNode.bounds), "expected arguments");
                    }
                    var body = args.sublist(1);
                    var sig = parseArgs(Backtrace.sameWith(callData.backtrace(), argsNode.bounds), argsList.children);
                    return new UserFunction(sig, body.children, callData.context(), null);
                }
        ));
    }
}
