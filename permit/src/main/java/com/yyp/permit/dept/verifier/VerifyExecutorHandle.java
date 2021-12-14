package com.yyp.permit.dept.verifier;

@FunctionalInterface
public interface VerifyExecutorHandle<T> {

    T handle(ValidExecutor validExecutor);

}
