package com.yyp.accesspermit;

import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CacheArchivesRoom extends AbstractArchivesRoom {

    private NamedThreadLocal<Map<String, VerifyReport>> recordStore = new NamedThreadLocal<>("档案库");

    public CacheArchivesRoom() {
        this.recordStore.set(new HashMap<>());
    }

    @Override
    public List<VerifyReport> register(PermissionInfo permissionInfo) {
        Assert.isTrue(permissionInfo.getPhase() == PermitToken.PermissionPhase.ACCESS, "不可重复登记");
        permissionInfo.setPhase(PermitToken.PermissionPhase.REGISTER);
        return permissionInfo.getAnnotationInfo().stream().map(info -> {
            VerifyReport report = getReport(info.getPermit());
            getRecordStore().computeIfAbsent(info.getPermit(), key -> report);
            return report;
        }).collect(Collectors.toList());
    }

    @Override
    protected String getReportId() {
        return "cache@" + super.getReportId();
    }

    @Override
    public VerifyReport getVerifyReport(String permit) {
        VerifyReport verifyReport = getRecordStore().get(permit);
        Assert.notNull(verifyReport, "不存在" + permit + "档案");
        return verifyReport;
    }

    @Override
    public void remove(String permit) {
        getRecordStore().remove(permit);
    }

    @Override
    public void archive(VerifyReport verifyReport) {

    }

    @Override
    public void update(VerifyReport verifyReport) {

    }

    public Map<String, VerifyReport> getRecordStore() {
        return recordStore.get();
    }
}
