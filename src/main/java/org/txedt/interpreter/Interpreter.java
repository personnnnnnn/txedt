package org.txedt.interpreter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.contexts.ContextPrivilege;
import org.txedt.errors.TxedtError;
import org.txedt.errors.TxedtThrowable;
import org.txedt.functions.FunctionValue;
import org.txedt.macros.MacroValue;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Bounds;
import org.txedt.parser.Node;

import java.util.ArrayList;

public final class Interpreter {
    private Interpreter() { }

    public static Object eval(@NotNull Node node, CallData callData) throws TxedtThrowable {
        return switch (node) {
            case Node.Int i -> i.n;
            case Node.Flt f -> f.n;
            case Node.Str s -> s.s;
            case Node.Symbol s -> // TODO: implement package access (context:var-name) syntax, 'nil' and 't'
                    callData.context().get(new Backtrace(callData.backtrace(), s.bounds), ContextPrivilege.PRIVATE, s.s);
            case Node.Lst l -> eval(l, callData);
        };
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
                var argv = new ArrayList<>(args.size());
                for (var x : args) {
                    argv.add(eval(x, callData));
                }
                yield f.call(new Backtrace(callData.backtrace(), node.bounds), argv);
            }
            case MacroValue m -> {
                var subList = args.isEmpty()
                        ? new Node.Lst(new Bounds(node.bounds.end().copy().stepBack(), node.bounds.end().copy()), args)
                        : new Node.Lst(new Bounds(args.getFirst().bounds.start().copy(), args.getLast().bounds.end().copy()), args);
                yield m.call(new CallData(new Backtrace(callData.backtrace(), node.bounds), callData.context()), subList);
            }
            case null, default -> throw new TxedtError(callData.backtrace(), "uncallable value");
        };
    }
}
