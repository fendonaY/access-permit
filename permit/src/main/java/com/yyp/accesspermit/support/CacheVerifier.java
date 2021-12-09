package com.yyp.accesspermit.support;

import java.util.List;

public class CacheVerifier implements Verifier {

    private VerifyTemplate verifyTemplate;

    public CacheVerifier(VerifyTemplate verifyTemplate) {
        this.verifyTemplate = verifyTemplate;
    }

    @Override
    public PermissionInfo prepareVerify(PermissionContext permissionContext) {
        return permissionContext.getPermissionInfo();
    }

    @Override
    public boolean verify(ArchivesRoom archivesRoom, PermissionInfo permissionInfo) {
        List<PermissionInfo.AnnotationInfo> annotationInfoList = permissionInfo.getAnnotationInfoList();
        boolean pass = true;
        int size = annotationInfoList.size();
        for (int i = 0; i < size; i++) {
            PermissionInfo.AnnotationInfo annotationInfo = annotationInfoList.get(i);
            VerifyReport verifyReport = archivesRoom.getVerifyReport(annotationInfo.getPermit());
            VerifierHelper.findValidData(verifyReport, permissionInfo, annotationInfo);
            if (verifyReport.getValidResult() == null) {
                VerifierHelper.findValidData(verifyReport, permissionInfo, annotationInfo);
                if (verifyReport.getValidResult() == null)
                    verifyTemplate.validParams(verifyReport);
            }
            pass = pass && verifyReport.getValidResult();
        }
        return pass;
    }

    @Override
    public void finishVerify(PermissionContext permissionContext) {
        permissionContext.getPermissionInfo().setPhase(PermitToken.PermissionPhase.VERIFIED);
    }
}
