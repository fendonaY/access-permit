package com.yyp.accesspermit;

import java.util.List;

public interface PermissionContext {

    PermissionInfo getPermissionInfo();

    List<String> getPermits();

    Object getValidData(String permit);

    boolean getValidResult(String permit);

    List getValidResultObject(String permit);


}
