package org.txedt.functions;

import org.jetbrains.annotations.Contract;
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

    public FunctionSignature restFail(@Nullable Backtrace debug, @NotNull String name) throws TxedtError {
        if (restIdx != -1) {
            throw new TxedtError(debug, "cannot have more than one arguments with &rest!");
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

    @Contract(value = "_, _, _ -> this", pure = true)
    public FunctionSignature dynamic(@Nullable Backtrace debug, @NotNull FunctionArgumentType type, @NotNull String name) throws TxedtError {
        switch (type) {
            case NORMAL -> arg(name);
            case OPTIONAL -> opt(name);
            case REST -> restFail(debug, name);
        }
        return this;
    }

    public boolean argcMatches(int argc) {
        return minArgc <= argc && (restIdx != -1 || argc <= maxArgc);
    }

    private final Set<String> ignore = new HashSet<>();

    public @NotNull String repr() {
        return
                (minArgc == maxArgc
                        ? minArgc + ""
                        : minArgc + "-" + maxArgc
                ) + (restIdx != -1
                        ? "+"
                        : ""
                );
    }

    public boolean isSingular() {
        return minArgc == maxArgc && minArgc == 1 && restIdx == -1;
    }

    public @NotNull Map<String, Object> mapArgs(@NotNull List<Object> argv, @Nullable Backtrace debug) throws TxedtThrowable {
        int argc = argv.size();
        if (!argcMatches(argc)) {
            throw new TxedtError(
                    debug,
                    "invalid argument count -- expected "
                            + repr() + " argument"
                            + (isSingular() ? "" : "s")
                            + ", not " + argc
            );
        }

        if (restIdx == -1 || argc <= maxArgc) {
            final var ret = new HashMap<String, Object>();

            ignore.clear();
            if (restIdx != -1) {
                ret.put(sigArgs.get(restIdx).name(), new ArrayList<>());
                ignore.add(sigArgs.get(restIdx).name());
            }

            var rmCount = maxArgc - argc;
            for (final var arg : sigArgs.reversed()) {
                if (rmCount <= 0) {
                    break;
                }
                if (arg.type() == FunctionArgumentType.OPTIONAL) {
                    rmCount--;
                    ignore.add(arg.name());
                }
            }

            var argvI = 0;
            for (final var arg : sigArgs) {
                if (ignore.contains(arg.name()) || argvI >= argc) {
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
