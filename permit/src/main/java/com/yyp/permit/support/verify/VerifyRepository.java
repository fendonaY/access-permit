package com.yyp.permit.support.verify;

import com.yyp.permit.support.VerifyReport;

public interface VerifyRepository {

    VerifyRepository initRepository();

    String getPermission(String permit);

    ValidExecutor getExecutor(VerifyReport verifyReport);
}
