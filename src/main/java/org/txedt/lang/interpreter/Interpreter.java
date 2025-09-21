package org.txedt.lang.interpreter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.lang.contexts.ContextPrivilege;
import org.txedt.lang.errors.TxedtError;
import org.txedt.lang.errors.TxedtThrowable;
import org.txedt.lang.functions.FunctionValue;
import org.txedt.lang.functions.ReturnThrowable;
import org.txedt.lang.macros.MacroValue;
import org.txedt.lang.parser.Backtrace;
import org.txedt.lang.parser.Bounds;
import org.txedt.lang.parser.Node;
import org.txedt.lang.properties.PropertyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Interpreter {
    private Interpreter() { }

    public static @Nullable Object evalWith(@NotNull Node node, @NotNull CallData callData) throws TxedtThrowable {
        return eval(node, new CallData(Backtrace.sameWith(callData.backtrace(), node.bounds), callData.context()));
    }

    public static @Nullable Object eval(@NotNull Node node, CallData callData) throws TxedtThrowable {
        return switch (node) {
            case Node.Int i -> i.n;
            case Node.Flt f -> f.n;
            case Node.Str s -> s.s;
            case Node.Symbol s ->
                    callData.context().getLib(callData.backtrace(), ContextPrivilege.PRIVATE, s.s);
            case Node.Lst l -> eval(l, callData);
        };
    }

    public static @NotNull List<Object> toArgv(@NotNull List<Node> args, CallData callData) throws TxedtThrowable {
        var argv = new ArrayList<>(args.size());
        for (var x : args) {
            var backt = Backtrace.sameWith(callData.backtrace(), x.bounds);
            argv.add(eval(x, new CallData(backt, callData.context())));
        }
        return argv;
    }

    private static @Nullable Object eval(@NotNull Node.Lst node, CallData callData) throws TxedtThrowable {
        if (node.children.isEmpty()) {
            return null;
        }
        var funcNode = node.children.getFirst();
        var func = eval(funcNode, callData);
        var args = node.children.subList(1, node.children.size());
        return switch (func) {
            case FunctionValue f -> {
                var argv = toArgv(args, callData);
                yield f.call(new Backtrace(callData.backtrace(), f.name(), node.bounds), argv);
            }
            case PropertyValue p -> {
                var argv = toArgv(args, callData);
                yield p.get().call(new Backtrace(callData.backtrace(), p.get().name(), node.bounds), argv);
            }
            case MacroValue m -> {
                var subList = args.isEmpty()
                        ? new Node.Lst(new Bounds(node.bounds.end().copy().stepBack(), node.bounds.end().copy()), args)
                        : new Node.Lst(new Bounds(args.getFirst().bounds.start().copy(), args.getLast().bounds.end().copy()), args);
                yield m.call(new CallData(new Backtrace(callData.backtrace(), m.name(), node.bounds), callData.context()), subList);
            }
            case null, default -> throw new TxedtError(Backtrace.sameWith(callData.backtrace(), funcNode.bounds), "uncallable value");
        };
    }

    public static @Nullable Object exec(@NotNull List<Node> nodes, CallData callData, String targetName) throws TxedtThrowable {
        if (nodes.isEmpty()) {
            return null;
        }
        try {
            for (int i = 0; i < nodes.size() - 1; i++) {
                var node = nodes.get(i);
                eval(node, new CallData(Backtrace.sameWith(callData.backtrace(), node.bounds), callData.context()));
            }
            var node = nodes.getLast();
            return eval(node, new CallData(Backtrace.sameWith(callData.backtrace(), node.bounds), callData.context()));
        } catch (ReturnThrowable e) {
            if (targetName != null && (e.target == null || Objects.equals(e.target, targetName))) {
                return e.returnValue;
            }
            throw e;
        }
    }

    // this works under the assumption there will be no return with the target just ""
    public static @Nullable Object exec(@NotNull List<Node> nodes, CallData callData) throws TxedtThrowable {
        return exec(nodes, callData, "");
    }
}
