package org.txedt.interpreter.std.general;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtError;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.interpreter.funcs.ArgType;
import org.txedt.interpreter.funcs.FunctionSignature;
import org.txedt.interpreter.funcs.UserFunction;
import org.txedt.interpreter.macros.MacroValue;
import org.txedt.parser.nodes.ListNode;
import org.txedt.parser.nodes.Node;
import org.txedt.parser.nodes.SymbolNode;

import java.util.List;

public class FnMacro extends MacroValue {
    public static FnMacro macro = new FnMacro();
    private FnMacro() { }

    @Override
    public @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable {
        if (args.children.size() < 2) {
            throw new TxedtError(args.bounds, "expected function name (optional) and arguments");
        }

        int argsI = 1;
        if (args.children.get(1) instanceof SymbolNode symbol) {
            argsI++;
        }
        if (!(args.children.get(argsI) instanceof ListNode arguments)) {
            throw new TxedtError(args.children.get(2).bounds, "expected arguments");
        }
        argsI++;
        List<Node> rest = args.children.subList(argsI, args.children.size());

        ArgType argType = ArgType.Normal;
        FunctionSignature signature = new FunctionSignature();

        for (var argDef : arguments.children) {
            if (!(argDef instanceof SymbolNode argSymbol)) {
                throw new TxedtError(argDef.bounds, "expected arg name of modifier");
            }
            if (argSymbol.s.equals("&rest")) {
                if (argType != ArgType.Normal) {
                    throw new TxedtError(argDef.bounds, "cannot apply more than one modifier on a single argument");
                }
                argType = ArgType.Rest;
                continue;
            }
            if (argSymbol.s.equals("&optional")) {
                if (argType != ArgType.Normal) {
                    throw new TxedtError(argDef.bounds, "cannot apply more than one modifier on a single argument");
                }
                argType = ArgType.Optional;
                continue;
            }

            signature.arg(argType, argSymbol.s);
            if (signature.fucked) {
                throw new TxedtError(argDef.bounds, "invalid argument");
            }
            argType = ArgType.Normal;
        }

        var fn = new UserFunction(signature, rest, ctx);

        if (args.children.get(1) instanceof SymbolNode symbol) {
            ctx.put(symbol.s, fn);
        }

        return fn;
    }
}
