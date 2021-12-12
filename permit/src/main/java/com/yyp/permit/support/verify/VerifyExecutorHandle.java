package com.yyp.permit.support.verify;

@FunctionalInterface
public interface VerifyExecutorHandle<T> {

    T handle(ValidExecutor validExecutor);

}
