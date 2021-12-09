package com.yyp.accesspermit.support;

import java.util.UUID;

public abstract class AbstractArchivesRoom implements ArchivesRoom {

    /**
     * 登记档案
     */
    VerifyReport getReport(PermissionInfo.AnnotationInfo annotationInfo) {
        VerifyReport verifyReport = new VerifyReport(annotationInfo.getPermit());
        verifyReport.setAnnotationInfo(annotationInfo);
        verifyReport.setSuggest(annotationInfo.getMessage());
        verifyReport.setId(getReportId(verifyReport));
        return verifyReport;
    }

    protected String getReportId(VerifyReport verifyReport) {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

