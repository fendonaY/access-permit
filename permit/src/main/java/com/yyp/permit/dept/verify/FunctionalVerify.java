package com.yyp.permit.dept.verify;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface FunctionalVerify extends VerifyExecutorHandle<List<Map<String, Object>>> {

    /**
     * @param verifyReport
     * @return {@link ValidExecutor#execute(VerifyExecutorHandle)}
     */
    int verify(FunctionalReport verifyReport);

    @Override
    default List<Map<String, Object>> handle(ValidExecutor validExecutor) {
        return null;
    }
}
