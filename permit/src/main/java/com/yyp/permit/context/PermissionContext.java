package com.yyp.permit.context;

import com.yyp.permit.dept.room.SecurityDept;
import com.yyp.permit.dept.room.VerifyReport;

import java.util.List;

public interface PermissionContext {

    SecurityDept getSecurityDept();

    PermissionInfo getPermissionInfo();

    List<String> getPermits();

    VerifyReport getReport(String permit);

    Object[] getValidData(String permit);

    boolean getValidResult(String permit);

    <E> List<E> getValidResultObject(String permit);


}
