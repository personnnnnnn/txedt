package org.txedt.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.functions.FunctionValue;
import org.txedt.functions.signature.FunctionSignature;

public abstract class Property {
    public final @NotNull FunctionValue get, set;
    public final @Nullable String name;
}
