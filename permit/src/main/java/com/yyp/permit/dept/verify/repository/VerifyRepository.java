package com.yyp.permit.dept.verify.repository;

import com.yyp.permit.dept.room.VerifyReport;
import com.yyp.permit.dept.verify.ValidExecutor;

public interface VerifyRepository {

    VerifyRepository initRepository();

    Object getPermission(String permit);

    ValidExecutor getExecutor(VerifyReport verifyReport);
}
