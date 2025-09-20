package org.txedt.functions.signature;

import org.jetbrains.annotations.Nullable;
import org.txedt.errors.TxedtThrowable;
import org.txedt.parser.Backtrace;

import java.util.Map;

public interface ExternalFunctionImpl {
    @Nullable Object call(Backtrace backtrace, Map<String, Object> args) throws TxedtThrowable;
}
