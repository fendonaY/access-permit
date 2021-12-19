package com.yyp.permit.dept.verifier;

import com.yyp.permit.context.PermissionContext;
import com.yyp.permit.context.PermissionInfo;
import com.yyp.permit.dept.room.VerifyReport;

public interface Verifier {

    /**
     * 准备验证
     */
    void prepareVerify(PermissionContext permissionContext, String permit);

    /**
     * 验证
     */
    boolean verify(VerifyReport verifyReport, PermissionInfo permissionInfo, String permit);

    /**
     * 验证完成
     */
    void finishVerify(PermissionContext permissionContext, String permit);

}
