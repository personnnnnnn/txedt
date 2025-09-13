package org.txedt.functions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.errors.TxedtError;
import org.txedt.errors.TxedtThrowable;
import org.txedt.parser.Backtrace;

import java.util.*;

public final class FunctionSignature {
    public List<FunctionArgument> sigArgs = new ArrayList<>();

    public FunctionSignature() { }

    private int minArgc = 0;
    private int maxArgc = 0;
    private int restIdx = -1;

    public FunctionSignature arg(@NotNull String name) {
        sigArgs.add(new FunctionArgument(name, FunctionArgumentType.NORMAL));
        minArgc++;
        maxArgc++;
        return this;
    }

    public FunctionSignature opt(@NotNull String name) {
        sigArgs.add(new FunctionArgument(name, FunctionArgumentType.OPTIONAL));
        maxArgc++;
        return this;
    }

    public FunctionSignature restFail(@Nullable Backtrace backtrace, @NotNull String name) throws TxedtError {
        if (restIdx != -1) {
            throw new TxedtError(backtrace, "cannot have more than one arguments with &rest!");
        }
        restIdx = sigArgs.size();
        sigArgs.add(new FunctionArgument(name, FunctionArgumentType.REST));
        return this;
    }

    public FunctionSignature rest(@NotNull String name) {
        try {
            return restFail(null, name);
        } catch (TxedtError e) {
            throw new RuntimeException(e);
        }
    }

    public boolean argcMatches(int argc) {
        return minArgc <= argc && (restIdx != -1 || argc <= maxArgc);
    }

    private final Set<String> ignore = new HashSet<>();

    public @NotNull Map<String, Object> mapArgs(@NotNull List<Object> argv) throws TxedtThrowable {
        int argc = argv.size();
        if (!argcMatches(argc)) {
            throw new TxedtError(null, "invalid argument count");
        }

        if (restIdx == -1 || argc <= maxArgc) {
            final var ret = new HashMap<String, Object>();

            ignore.clear();
            var rmCount = maxArgc - minArgc;
            for (final var arg : sigArgs.reversed()) {
                if (rmCount <= 0) {
                    break;
                }
                if (arg.type() == FunctionArgumentType.OPTIONAL) {
                    rmCount--;
                    ignore.add(arg.name());
                    continue;
                }
                if (arg.type() == FunctionArgumentType.REST) {
                    ignore.add(arg.name());
                    ret.put(arg.name(), List.of());
                }
            }

            var argvI = 0;
            for (final var arg : sigArgs) {
                if (ignore.contains(arg.name())) {
                    continue;
                }
                ret.put(arg.name(), argv.get(argvI));
                argvI++;
            }

            return ret;
        }

        var ret = new HashMap<String, Object>();
        int restStart = 0;
        for (; restStart < argc && restStart < sigArgs.size() && sigArgs.get(restStart).type() != FunctionArgumentType.REST; restStart++) {
            ret.put(sigArgs.get(restStart).name(), argv.get(restStart));
        }

        int restEnd = argc - 1;
        for (int argI = sigArgs.size() - 1; restEnd >= 0 && argI >= 0 && sigArgs.get(argI).type() != FunctionArgumentType.REST; restEnd--, argI--) {
            ret.put(sigArgs.get(argI).name(), argv.get(restEnd));
        }

        List<Object> rest = argv.subList(restStart, restEnd + 1);
        ret.put(sigArgs.get(restStart).name(), rest);
        return ret;
    }
}
