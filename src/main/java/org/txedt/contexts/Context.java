package org.txedt.contexts;

import org.jetbrains.annotations.NotNull;
import org.txedt.errors.TxedtError;
import org.txedt.errors.TxedtThrowable;
import org.txedt.parser.Backtrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {
    public final Map<String, Object> vars = new HashMap<>();

    public final List<Context> parents = new ArrayList<>();
    public final List<Context> parameters = new ArrayList<>();

    public Context() { }

    public Context parent(Context ctx) {
        parents.add(ctx);
        return this;
    }

    public Context param(Context ctx) {
        parameters.add(ctx);
        return this;
    }

    public void put(@NotNull String name, Object value) {
        vars.put(name, value);
    }

    //                                                                                  for inheritance
    public ContextResult getOk(ContextPrivilege privilege, @NotNull String name) throws TxedtThrowable {
        if (vars.containsKey(name)) {
            return new ContextResult.Value(vars.get(name));
        }

        for (var parent : parents.reversed()) {
            switch (parent.getOk(ContextPrivilege.PUBLIC, name)) {
                case ContextResult.NotFound ignored -> { }
                case ContextResult.Value v -> {
                    return v;
                }
            }
        }

        if (privilege == ContextPrivilege.PRIVATE) {
            for (var parameter : parameters.reversed()) {
                switch (parameter.getOk(ContextPrivilege.PUBLIC, name)) {
                    case ContextResult.NotFound ignored -> { }
                    case ContextResult.Value v -> {
                        return v;
                    }
                }
            }
        }

        return new ContextResult.NotFound();
    }

    //                                                                                   for inheritance
    public boolean setOk(ContextPrivilege privilege, @NotNull String name, Object value) throws TxedtThrowable {
        if (vars.containsKey(name)) {
            put(name, value);
            return true;
        }

        for (var parent : parents.reversed()) {
            if (parent.setOk(ContextPrivilege.PUBLIC, name, value)) {
                return true;
            }
        }

        if (privilege == ContextPrivilege.PRIVATE) {
            for (var parameter : parameters.reversed()) {
                if (parameter.setOk(ContextPrivilege.PUBLIC, name, value)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasImm(@NotNull String name) {
        return vars.containsKey(name);
    }

    public boolean has(ContextPrivilege privilege, @NotNull String name) {
        if (hasImm(name)) {
            return true;
        }

        for (var parent : parents.reversed()) {
            if (parent.has(ContextPrivilege.PUBLIC, name)) {
                return true;
            }
        }

        if (privilege == ContextPrivilege.PRIVATE) {
            for (var parameter : parameters.reversed()) {
                if (parameter.has(ContextPrivilege.PUBLIC, name)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Object get(Backtrace backtrace, ContextPrivilege privilege, @NotNull String name) throws TxedtThrowable {
        switch (getOk(privilege, name)) {
            case ContextResult.NotFound ignore -> throw new TxedtError(backtrace, "variable " + name + " does not exist");
            case ContextResult.Value v -> {
                return v.v();
            }
        }
    }

    public void set(Backtrace backtrace, ContextPrivilege privilege, @NotNull String name, Object value) throws TxedtThrowable {
        if (!setOk(privilege, name, value)) {
            throw new TxedtError(backtrace, "variable " + name + "does not exist");
        }
    }
}
