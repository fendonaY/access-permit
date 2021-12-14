package com.yyp.permit.dept.verify;

@FunctionalInterface
public interface VerifyExecutorHandle<T> {

    T handle(ValidExecutor validExecutor);

}
