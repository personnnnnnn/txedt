package org.txedt.lang.functions.signature;

import org.jetbrains.annotations.Nullable;
import org.txedt.lang.errors.TxedtThrowable;
import org.txedt.lang.parser.Backtrace;

import java.util.Map;

public interface ExternalFunctionImpl {
    @Nullable Object call(Backtrace backtrace, Map<String, Object> args) throws TxedtThrowable;
}
