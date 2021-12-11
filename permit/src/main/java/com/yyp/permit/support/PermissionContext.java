package com.yyp.permit.support;

import java.util.List;

public interface PermissionContext {

    SecurityDept getSecurityDept();

    PermissionInfo getPermissionInfo();

    List<String> getPermits();

    VerifyReport getReport(String permit);

    Object getValidData(String permit);

    boolean getValidResult(String permit);

    List getValidResultObject(String permit);


}
