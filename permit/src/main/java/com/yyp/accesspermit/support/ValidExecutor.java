package com.yyp.accesspermit.support;

import java.util.List;
import java.util.Map;

public interface ValidExecutor {

    int execute(VerifyExecutorHandle verifyExecutorHandle);

    List<Map<String, Object>> getResult();
}
