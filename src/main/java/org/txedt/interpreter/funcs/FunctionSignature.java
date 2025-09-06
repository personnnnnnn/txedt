package org.txedt.interpreter.funcs;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FunctionSignature {
    public final List<FunctionArg> args;
    private boolean hasRest = false;
    public boolean fucked = false;
    private int restIdx = -1;

    private int minArgc = 0;
    private int maxArgc = 0; // excludes args with &rest

    public int getMinArgc() {
        return minArgc;
    }

    public int getMaxArgc() {
        return maxArgc;
    }

    public boolean doesHaveRest() {
        return hasRest;
    }

    public boolean argcMatches(int argc) {
        return minArgc <= argc && (hasRest || argc <= maxArgc);
    }

    public FunctionSignature() {
        args = new ArrayList<>();
    }

    public FunctionSignature arg(ArgType type, String name) {
        if (type == ArgType.Rest) {
            if (hasRest) {
                fucked = true;
                return this;
            }
            restIdx = args.size();
            hasRest = true;
        } else {
            maxArgc++;
            if (type == ArgType.Normal) {
                minArgc++;
            }
        }

        FunctionArg arg = new FunctionArg(name, type);
        args.add(arg);

        return this;
    }

    private final Set<Integer> ignore = new HashSet<>();

    /**
     * Returns null if the argument count is outside its range.
     * An argument key is missing if that argument is optional and it was omitted.
     * If there is a rest argument, it will always have a value of type <code>ArrayList&lt;Object&gt;</code>.
     */
    public Map<String, Object> mapArgs(@NotNull List<Object> argv) {
        final int sz = argv.size();
        if (!argcMatches(sz)) {
            return null;
        }
        final Map<String, Object> map = new HashMap<>();

        if (!hasRest || sz <= maxArgc) {
            if (hasRest) {
                map.put(args.get(restIdx).name(), new ArrayList<>());
            }

            if (sz == maxArgc) {
                int argvI = 0;
                for (var arg : args) {
                    if (arg.type() == ArgType.Rest) {
                        continue;
                    }
                    map.put(arg.name(), argv.get(argvI));
                    argvI++;
                }
                return map;
            }

            ignore.clear();
            int x = maxArgc;
            for (int i = args.size() - 1; x > sz && i >= 0; i--) {
                if (args.get(i).type() == ArgType.Optional) {
                    ignore.add(i);
                    x--;
                }
            }
            if (hasRest) ignore.add(restIdx);

            int argvI = 0;
            for (int i = 0; i < args.size() && argvI < sz; i++) {
                if (ignore.contains(i)) {
                    continue;
                }
                map.put(args.get(i).name(), argv.get(argvI));
                argvI++;
            }

            return map;
        }

        int restStart = 0;
        for (; restStart < sz && restStart < args.size() && args.get(restStart).type() != ArgType.Rest; restStart++) {
            map.put(args.get(restStart).name(), argv.get(restStart));
        }

        int restEnd = sz - 1;
        for (int argI = args.size() - 1; restEnd >= 0 && argI >= 0 && args.get(argI).type() != ArgType.Rest; restEnd--, argI--) {
            map.put(args.get(argI).name(), argv.get(restEnd));
        }

        List<Object> rest = new ArrayList<>();
        for (int i = restStart; i <= restEnd; i++) {
            rest.add(argv.get(i));
        }
        map.put(args.get(restStart).name(), rest);

        return map;
    }
}
