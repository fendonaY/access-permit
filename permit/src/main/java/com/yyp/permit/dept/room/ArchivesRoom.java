package com.yyp.permit.dept.room;

import com.yyp.permit.context.PermitInfo;

import java.util.List;

public interface ArchivesRoom {

    /**
     * 登记档案
     */
    List<VerifyReport> register(PermitInfo permitInfo);

    /**
     * 查询档案
     */
    VerifyReport getVerifyReport(String permit);

    /**
     * 登记所有档案
     */
    List<VerifyReport> getVerifyReportList();

    /**
     * 删除档案
     */
    void remove(String permit);

    /**
     * 归档
     */
    void archive(VerifyReport verifyReport);
}

