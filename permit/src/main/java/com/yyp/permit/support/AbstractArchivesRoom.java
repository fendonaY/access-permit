package com.yyp.permit.support;

import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractArchivesRoom implements ArchivesRoom {

    /**
     * 登记档案
     */
    VerifyReport getReport(PermissionAnnotationInfo annotationInfo) {
        VerifyReport verifyReport = new VerifyReport(annotationInfo.getPermit());
        verifyReport.setAnnotationInfo(annotationInfo);
        verifyReport.setSuggest(annotationInfo.getMessage());
        verifyReport.setId(getReportId(verifyReport));
        verifyReport.setCurrent(true);
        return verifyReport;
    }

    @Override
    public VerifyReport getVerifyReport(String permit) {
        Set<String> ids = getPermitReportIdMap(permit);
        Map<String, VerifyReport> recordStore = getRecordStore();
        List<VerifyReport> current = ids.stream().map(id -> recordStore.get(id)).filter(report -> report.isCurrent()).collect(Collectors.toList());
        Assert.state(!current.isEmpty(), permit + " archives doesn't exist");
        Assert.isTrue(current.size() == 1, permit + " has multiple current archives");
        return current.get(0);
    }

    @Override
    public void update(VerifyReport oldReport, VerifyReport newReport) {
        Map<String, VerifyReport> recordStore = getRecordStore();
        Set<String> permitReportIdMap = getPermitReportIdMap(oldReport.getPermit());
        permitReportIdMap.remove(oldReport.getId());
        permitReportIdMap.add(newReport.getId());
        recordStore.remove(oldReport.getId());
        recordStore.putIfAbsent(newReport.getId(), newReport);
    }

    @Override
    public void remove(String permit) {
        Set<String> ids = getPermitReportIdMap().remove(permit);
        if (ids != null) {
            Map<String, VerifyReport> recordStore = getRecordStore();
            ids.forEach(id -> recordStore.remove(id));
        }
    }

    public void setRecordStore(String permit, VerifyReport verifyReport) {
        getPermitReportIdMap(permit).add(verifyReport.getId());
        getRecordStore().compute(verifyReport.getId(), (key, oldValue) -> {
            if (oldValue != null) {
                oldValue.setTargetClass(verifyReport.getTargetClass());
                oldValue.setTargetMethod(verifyReport.getTargetMethod());
                oldValue.setTargetObj(verifyReport.getTargetObj());
                oldValue.setCurrent(true);
                return oldValue;
            }
            return verifyReport;
        });
    }

    protected abstract Map<String, Set<String>> getPermitReportIdMap();

    protected abstract Set<String> getPermitReportIdMap(String permit);

    protected abstract Map<String, VerifyReport> getRecordStore();

    public String getReportId(VerifyReport verifyReport) {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

