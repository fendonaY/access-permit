package com.yyp.permit.dept.verifier;

import com.yyp.permit.dept.room.ArchivesRoom;
import com.yyp.permit.context.PermissionContext;
import com.yyp.permit.context.PermissionInfo;

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
