package com.yyp.accesspermit;

import java.util.List;

public interface ArchivesRoom {

    /**
     * 登记档案
     */
    public abstract List<VerifyReport> register(PermissionInfo permissionInfo);

    /**
     * 查询档案
     */
    public abstract VerifyReport getVerifyReport(String permit);

    /**
     * 删除档案
     */
    public abstract void remove(String permit);

    /**
     * 归档
     */
    public abstract void archive(VerifyReport verifyReport);

    /**
     * 更新档案
     */
    public abstract void update(VerifyReport verifyReport);

}

