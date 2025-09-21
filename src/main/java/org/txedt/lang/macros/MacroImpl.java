package org.txedt.lang.macros;

import org.jetbrains.annotations.NotNull;
import org.txedt.lang.errors.TxedtThrowable;
import org.txedt.lang.interpreter.CallData;
import org.txedt.lang.parser.Node;

public interface MacroImpl {
    Object call(@NotNull CallData data, @NotNull Node.Lst args) throws TxedtThrowable;
}
