package com.yyp.permit.dept.room;

import com.yyp.permit.context.PermissionContext;
import com.yyp.permit.context.PermissionInfo;
import com.yyp.permit.context.PermitToken;
import com.yyp.permit.dept.verifier.Verifier;

import java.util.List;

public interface SecurityDept {

    /**
     * 登记
     */
    PermissionContext register(PermissionInfo permissionInfo);

    /**
     * 获取所有检查者
     */
    List<Verifier> getVerifierList();

    /**
     * 获取检查者
     */
    List<Verifier> getVerifier(String permit);

    /**
     * 获取档案
     */
    VerifyReport getReport(String permit);

    /**
     * 安全验证
     */
    PermitToken securityVerify(PermissionContext permissionContext);

}
