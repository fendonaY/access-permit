package com.yyp.permit.support;

import com.yyp.permit.support.verify.Verifier;

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
