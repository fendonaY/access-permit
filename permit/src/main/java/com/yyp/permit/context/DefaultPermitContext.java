package com.yyp.permit.context;

import com.yyp.permit.dept.room.SecurityDept;
import com.yyp.permit.dept.room.VerifyReport;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultPermitContext implements PermitContext {

    private SecurityDept securityDept;

    private PermitInfo permitInfo;

    public DefaultPermitContext() {
    }

    public DefaultPermitContext(SecurityDept securityDept, PermitInfo permitInfo) {
        this.securityDept = securityDept;
        this.permitInfo = permitInfo;
    }

    @Override
    public List<String> getPermits() {
        return getPermissionInfo().getAnnotationInfoList().stream().map(info -> info.getPermit()).collect(Collectors.toList());
    }

    @Override
    public VerifyReport getReport(String permit) {
        return securityDept.getReport(permit);
    }

    @Override
    public Object[]  getValidData(String permit) {
        VerifyReport report = getReport(permit);
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

    public PermitInfo getPermissionInfo() {
        return this.permitInfo;
    }

    public void setPermissionInfo(PermitInfo permitInfo) {
        this.permitInfo = permitInfo;
    }
}
