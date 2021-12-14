package com.yyp.permit.dept.verifier.repository;

import com.yyp.permit.dept.room.VerifyReport;
import com.yyp.permit.dept.verifier.ValidExecutor;

public interface VerifyRepository {

    VerifyRepository initRepository();

    Object getPermission(String permit);

    ValidExecutor getExecutor(VerifyReport verifyReport);
}
