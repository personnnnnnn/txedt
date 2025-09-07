package org.txedt.interpreter.macros;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.Interpreter;
import org.txedt.interpreter.ReturnError;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.Node;

import java.util.List;

public class UserMacro extends MacroValue {
    public final @NotNull MacroSignature signature;
    public final @NotNull List<Node> nodes;
    public final @NotNull Context context;

    public UserMacro(@NotNull MacroSignature signature, @NotNull List<Node> nodes, @NotNull Context context) {
        this.signature = signature;
        this.nodes = nodes;
        this.context = context;
    }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        var childCtx = new Context(ctx);
        var ok = signature.matches(childCtx, args.children.subList(1, args.children.size()));
        if (!ok) {
            throw new TxedtError(args.bounds, "invalid arguments");
        }

        Node node;
        try {
            var x = Interpreter.exec(nodes, childCtx, null);
            if (!(x instanceof Node n)) {
                throw new TxedtError(args.bounds, "macros must return ASTs");
            }
            node = n;
        } catch (ReturnError e) {
            throw new TxedtError(e.bounds, e.msg);
        }

        return Interpreter.eval(node, ctx);
    }
}
