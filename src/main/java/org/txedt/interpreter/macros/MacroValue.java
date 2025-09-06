package org.txedt.interpreter.macros;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.txedt.TxedtThrowable;
import org.txedt.interpreter.Context;
import org.txedt.parser.nodes.ListNode;

// TODO: implement actual macro signatures
public abstract class MacroValue {
    public abstract @Nullable Object call(@NotNull ListNode args, @NotNull Context ctx) throws TxedtThrowable;
}
