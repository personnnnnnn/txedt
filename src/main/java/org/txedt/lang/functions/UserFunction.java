package org.txedt.functions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.contexts.Context;
import org.txedt.errors.TxedtThrowable;
import org.txedt.functions.signature.FunctionSignature;
import org.txedt.interpreter.CallData;
import org.txedt.interpreter.Interpreter;
import org.txedt.parser.Backtrace;
import org.txedt.parser.Node;

import java.util.List;
import java.util.Map;

public final class UserFunction extends FunctionValue {
    public final @NotNull List<Node> nodes;
    public final @NotNull Context context;

    public UserFunction(@NotNull FunctionSignature signature, @NotNull List<Node> nodes, @NotNull Context context, @Nullable String name) {
        super(name, signature);
        this.nodes = nodes;
        this.context = context;
    }

    @Override
    public @Nullable Object call(Backtrace backtrace, Map<String, Object> args) throws TxedtThrowable {
        if (nodes.isEmpty()) {
            return null;
        }
        var childCtx = new Context().parent(context);
        childCtx.vars.putAll(args);
        return Interpreter.exec(nodes, new CallData(backtrace, childCtx), name == null ? "anon" : name);
    }
}
