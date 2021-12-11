package com.yyp.permit.support.verify.repository;

import com.yyp.permit.support.VerifyReport;
import com.yyp.permit.support.verify.ValidExecutor;

public interface VerifyRepository {

    VerifyRepository initRepository();

    String getPermission(String permit);

    ValidExecutor getExecutor(VerifyReport verifyReport);
}
