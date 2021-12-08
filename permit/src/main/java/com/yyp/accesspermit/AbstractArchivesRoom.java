package com.yyp.accesspermit;

import java.util.UUID;

public abstract class AbstractArchivesRoom implements ArchivesRoom {

    /**
     * 登记档案
     */
    VerifyReport getReport(String permit) {
        VerifyReport verifyReport = new VerifyReport(permit);
        verifyReport.setId(getReportId());
        return verifyReport;
    }

    protected String getReportId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

