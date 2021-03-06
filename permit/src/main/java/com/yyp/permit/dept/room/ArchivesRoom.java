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
     * 查询档案
     */
    VerifyReport getVerifyReport(String permit, String reportId);

    /**
     * 删除档案
     */
    void remove(String permit);

    /**
     * 归档
     */
    void archive(VerifyReport verifyReport);

    /**
     * 更新档案
     */
    void update(VerifyReport oldReport, VerifyReport newReport);

    /**
     * 获取档案id
     */
    String getReportId(VerifyReport verifyReport);

}

