package org.txedt.functions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.contexts.Context;
import org.txedt.errors.TxedtThrowable;
import org.txedt.interpreter.CallData;
import org.txedt.interpreter.Interpreter;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Node;

import java.util.List;
import java.util.Map;

public final class UserFunction extends FunctionValue {
    public final @NotNull List<Node> nodes;
    public final @NotNull Context context;
    public final @Nullable String name;

    public UserFunction(@NotNull FunctionSignature signature, @NotNull List<Node> nodes, @NotNull Context context, @Nullable String name) {
        super(signature);
        this.nodes = nodes;
        this.context = context;
        this.name = name;
    }

    @Override
    public @Nullable Object call(Backtrace backtrace, Map<String, Object> args) throws TxedtThrowable {
        if (nodes.isEmpty()) {
            return null;
        }
        var childCtx = new Context().parent(context);
        childCtx.vars.putAll(args);
        // TODO: when using 'return-with', this should also catch the function name (or 'anon' if there is none)
        return Interpreter.exec(nodes, new CallData(
                new Backtrace(
                        backtrace.parent(),
                        name == null ? null : "function " + name,
                        backtrace.bounds()
                ),
                childCtx
        ));
    }
}
