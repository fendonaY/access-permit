package com.yyp.permit.context;

import com.yyp.permit.dept.SecurityDept;
import com.yyp.permit.dept.room.VerifyReport;

import java.util.List;

public interface PermitContext {

    SecurityDept getSecurityDept();

    PermitInfo getPermitInfo();

    List<String> getPermits();

    VerifyReport getReport(String permit);

    Object[] getValidData(String permit);

    boolean getValidResult(String permit);

    <E> List<E> getValidResultObject(String permit);


}
