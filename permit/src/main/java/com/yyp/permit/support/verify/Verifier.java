package com.yyp.permit.support.verify;

import com.yyp.permit.support.ArchivesRoom;
import com.yyp.permit.support.PermissionContext;
import com.yyp.permit.support.PermissionInfo;

public interface Verifier {

    /**
     * 准备验证
     */
    void prepareVerify(PermissionContext permissionContext, String permit);

    /**
     * 验证
     */
    boolean verify(ArchivesRoom archivesRoom, PermissionInfo permissionInfo, String permit);

    /**
     * 验证完成
     */
    void finishVerify(PermissionContext permissionContext, String permit);

}
