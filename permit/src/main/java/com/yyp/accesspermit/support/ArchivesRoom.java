package com.yyp.accesspermit.support;

import java.util.List;

public interface ArchivesRoom {

    /**
     * 登记档案
     */
    List<VerifyReport> register(PermissionInfo permissionInfo);

    /**
     * 查询档案
     */
    VerifyReport getVerifyReport(String permit);

    /**
     * 删除档案
     */
    void remove(String permit);

    /**
     * 归档
     */
    void archive(String permit);

    /**
     * 更新档案
     */
    void update(VerifyReport verifyReport);

}

