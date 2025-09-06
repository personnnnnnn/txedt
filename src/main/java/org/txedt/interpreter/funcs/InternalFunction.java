package org.txedt.interpreter.funcs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.parser.nodes.Node;

import java.util.List;
import java.util.Map;

public class InternalFunction extends FunctionValue {
    public final @NotNull List<Node> nodes;
    public final @NotNull Context ctx;

    public InternalFunction(@NotNull FunctionSignature signature, @NotNull List<Node> nodes, @NotNull Context ctx) {
        super(signature);
        this.nodes = nodes;
        this.ctx = ctx;
    }

    @Override
    public @Nullable Object callFn(@NotNull Map<String, Object> args) throws TxedtThrowable {
        var childCtx = new Context(ctx);
        childCtx.putFrom(args);
        return Interpreter.exec(nodes, childCtx, null);
    }
}
