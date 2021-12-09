package com.yyp.accesspermit.support;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultPermissionContext implements PermissionContext {

    private SecurityDept securityDept;

    private PermissionInfo permissionInfo;

    public DefaultPermissionContext() {
    }

    public DefaultPermissionContext(SecurityDept securityDept, PermissionInfo permissionInfo) {
        this.securityDept = securityDept;
        this.permissionInfo = permissionInfo;
    }

    @Override
    public List<String> getPermits() {
        return getPermissionInfo().getAnnotationInfoList().stream().map(info -> info.getPermit()).collect(Collectors.toList());
    }

    @Override
    public Object getValidData(String permit) {
        VerifyReport report = securityDept.getReport(permit);
        return report.getValidData();
    }

    @Override
    public boolean getValidResult(String permit) {
        VerifyReport report = securityDept.getReport(permit);
        return report.getValidResult();
    }

    @Override
    public List getValidResultObject(String permit) {
        VerifyReport report = securityDept.getReport(permit);
        return report.getValidResultObject();
    }

    public SecurityDept getSecurityDept() {
        return securityDept;
    }

    public void setSecurityDept(SecurityDept securityDept) {
        this.securityDept = securityDept;
    }

    public PermissionInfo getPermissionInfo() {
        return this.permissionInfo;
    }

    public void setPermissionInfo(PermissionInfo permissionInfo) {
        this.permissionInfo = permissionInfo;
    }
}
