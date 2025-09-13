package org.txedt.contexts;

public sealed interface ContextResult {
    record Value(Object v) implements ContextResult { }
    record NotFound() implements ContextResult { }
}
