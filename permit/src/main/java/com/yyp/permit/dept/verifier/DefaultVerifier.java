package com.yyp.permit.dept.verifier;

import com.yyp.permit.annotation.SupportVerify;
import com.yyp.permit.annotation.parser.PermitAnnotationInfo;
import com.yyp.permit.context.PermitContext;
import com.yyp.permit.context.PermitInfo;
import com.yyp.permit.context.PermitToken;
import com.yyp.permit.dept.room.VerifyReport;

@SupportVerify(value = "defaultVerifier")
public class DefaultVerifier implements Verifier {

    private VerifyTemplate verifyTemplate;

    public DefaultVerifier(VerifyTemplate verifyTemplate) {
        this.verifyTemplate = verifyTemplate;
    }

    @Override
    public void prepareVerify(PermitContext permitContext, String permit) {
        PermitInfo permitInfo = permitContext.getPermissionInfo();
        PermitAnnotationInfo annotationInfo = permitInfo.getAnnotationInfo(permit);
        VerifyReport verifyReport = permitContext.getReport(permit);
        if (verifyReport.getValidResult() == null)
            VerifierHelper.findValidData(verifyReport, permitInfo, annotationInfo);
    }

    @Override
    public boolean verify(VerifyReport verifyReport, PermitInfo permitInfo, String permit) {
        if (verifyReport.getValidResult() == null)
            verifyTemplate.validParams(verifyReport);
        return verifyReport.getValidResult();
    }

    @Override
    public void finishVerify(PermitContext permitContext, String permit) {
        VerifyReport report = permitContext.getReport(permit);
        report.setPhase(PermitToken.PermissionPhase.VERIFIED);
    }
}
