package com.yyp.permit.dept.verifier;

import com.yyp.permit.annotation.SupportVerify;
import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import com.yyp.permit.context.PermissionContext;
import com.yyp.permit.context.PermissionInfo;
import com.yyp.permit.context.PermitToken;
import com.yyp.permit.dept.room.VerifyReport;

@SupportVerify(value = "defaultVerifier")
public class DefaultVerifier implements Verifier {

    private VerifyTemplate verifyTemplate;

    public DefaultVerifier(VerifyTemplate verifyTemplate) {
        this.verifyTemplate = verifyTemplate;
    }

    @Override
    public void prepareVerify(PermissionContext permissionContext, String permit) {
        PermissionInfo permissionInfo = permissionContext.getPermissionInfo();
        PermissionAnnotationInfo annotationInfo = permissionInfo.getAnnotationInfo(permit);
        VerifyReport verifyReport = permissionContext.getReport(permit);
        if (verifyReport.getValidResult() == null)
            VerifierHelper.findValidData(verifyReport, permissionInfo, annotationInfo);
    }

    @Override
    public boolean verify(VerifyReport verifyReport, PermissionInfo permissionInfo, String permit) {
        if (verifyReport.getValidResult() == null)
            verifyTemplate.validParams(verifyReport);
        return verifyReport.getValidResult();
    }

    @Override
    public void finishVerify(PermissionContext permissionContext, String permit) {
        VerifyReport report = permissionContext.getReport(permit);
        report.setPhase(PermitToken.PermissionPhase.VERIFIED);
    }
}
