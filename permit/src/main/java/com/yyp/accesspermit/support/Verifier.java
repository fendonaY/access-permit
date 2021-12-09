package com.yyp.accesspermit.support;

public interface Verifier {

    /**
     * 准备验证
     */
    PermissionInfo prepareVerify(PermissionContext permissionContext);

    /**
     * 验证
     */
    boolean verify(ArchivesRoom archivesRoom,PermissionInfo permissionInfo);

    /**
     * 验证完成
     */
    void finishVerify(PermissionContext permissionContext);

}
