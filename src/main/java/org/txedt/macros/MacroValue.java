package org.txedt.macros;

import org.jetbrains.annotations.NotNull;
import org.txedt.errors.TxedtThrowable;
import org.txedt.interpreter.CallData;
import org.txedt.parser.Node;

public interface MacroValue {
    Object call(@NotNull CallData data, @NotNull Node.Lst args) throws TxedtThrowable;
}
