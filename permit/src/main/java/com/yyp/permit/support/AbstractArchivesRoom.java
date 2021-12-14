package com.yyp.permit.support;

import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractArchivesRoom implements ArchivesRoom, RecycleBin {

    private NamedThreadLocal<Map<String, VerifyReport>> currentRecordStore = new NamedThreadLocal<>("currentArchives");

    private NamedThreadLocal<Map<String, VerifyReport>> recordStore = new NamedThreadLocal<>("archives");

    private NamedThreadLocal<Map<String, Set<String>>> permitReportIdMap = new NamedThreadLocal<>("permit report id map");

    private final String cachePrefix = "ARCHIVES_ROOM@$1_$2";

    @Override
    public List<VerifyReport> register(PermissionInfo permissionInfo) {
        putInRecycleBin();
        return permissionInfo.getAnnotationInfoList().stream().map(info -> {
            VerifyReport report = getReport(permissionInfo, info);
            Map<String, VerifyReport> archiversStore = getArchiversStore(report);
            archiversStore.forEach((key, value) -> setCurrentRecordStore(info.getPermit(), value));
            setCurrentRecordStore(info.getPermit(), report);
            return report;
        }).collect(Collectors.toList());
    }

    /**
     * 登记档案
     */
    VerifyReport getReport(PermissionInfo permissionInfo, PermissionAnnotationInfo annotationInfo) {
        VerifyReport verifyReport = new VerifyReport(annotationInfo.getPermit());
        verifyReport.setAnnotationInfo(annotationInfo);
        verifyReport.setSuggest(annotationInfo.getMessage());
        verifyReport.setCurrent(true);
        verifyReport.setTargetClass(permissionInfo.getTargetClass());
        verifyReport.setTargetMethod(permissionInfo.getTargetMethod());
        verifyReport.setTargetObj(permissionInfo.getTargetObj());
        verifyReport.setArguments(permissionInfo.getArguments());
        verifyReport.setId(getReportId(verifyReport));
        return verifyReport;
    }

    @Override
    public VerifyReport getVerifyReport(String permit) {
        Set<String> ids = getPermitReportIdMap(permit);
        Map<String, VerifyReport> currentRecordStore = getCurrentRecordStore();
        List<VerifyReport> current = ids.stream().map(id -> currentRecordStore.get(id)).filter(report -> report != null && report.isCurrent()).collect(Collectors.toList());
        if (current.isEmpty()) {
            Map<String, VerifyReport> recordStore = getRecordStore();
            current = ids.stream().map(id -> recordStore.get(id)).filter(report -> report != null && report.isCurrent()).collect(Collectors.toList());
        }
        Assert.state(!current.isEmpty(), permit + " archives doesn't exist");
        Assert.isTrue(current.size() == 1, permit + " has multiple(" + current.size() + ") current archives");
        return current.get(0);
    }

    @Override
    public VerifyReport getVerifyReport(String permit, String reportId) {
        VerifyReport verifyReport = getCurrentRecordStore().get(reportId);
        if (verifyReport == null) {
            verifyReport = getRecordStore().get(reportId);
        }
        Assert.notNull(verifyReport, permit + " archives doesn't exist");
        return verifyReport;
    }

    @Override
    public void update(VerifyReport oldReport, VerifyReport newReport) {
        Map<String, VerifyReport> currentRecordStore = getCurrentRecordStore();
        Set<String> permitReportIdMap = getPermitReportIdMap(oldReport.getPermit());
        permitReportIdMap.remove(oldReport.getId());
        currentRecordStore.remove(oldReport.getId());
        setCurrentRecordStore(newReport.getPermit(), newReport);
    }

    @Override
    public void archive(VerifyReport verifyReport) {
        Assert.notNull(verifyReport.getAnnotationInfo(), "unknown report");
        PermissionAnnotationInfo annotationInfo = verifyReport.getAnnotationInfo();
        if (verifyReport.isArchive())
            return;
        if (annotationInfo.isValidCache()) {
            verifyReport.setArchive(true);
            verifyReport.setCurrent(false);
            putCache(verifyReport);
        }
    }

    public abstract void putCache(VerifyReport verifyReport);

    @Override
    public void remove(String permit) {
        Set<String> ids = getPermitReportIdMap().remove(permit);
        if (ids != null) {
            Map<String, VerifyReport> recordStore = getRecordStore();
            ids.forEach(id -> recordStore.remove(id));
        }
    }

    @Override
    public Rubbish produceRubbish() {
        return () -> {
            try {
                doArchive();
            } finally {
                permitReportIdMap.remove();
                recordStore.remove();
                currentRecordStore.remove();
            }
        };
    }

    protected final void doArchive() {
        Map<String, VerifyReport> recordStore = this.getRecordStore();
        if (recordStore != null && !recordStore.isEmpty()) {
            recordStore.forEach((permit, report) -> archive(report));
        }
    }

    public void setCurrentRecordStore(String permit, VerifyReport verifyReport) {
        Set<String> permitReportIdMap = getPermitReportIdMap(permit);
        permitReportIdMap.add(verifyReport.getId());
        Map<String, VerifyReport> currentRecordStore = getCurrentRecordStore();
        currentRecordStore.compute(verifyReport.getId(), (key, oldValue) -> {
            if (oldValue != null) {
                BeanUtils.copyProperties(verifyReport, oldValue, "archive", "validResult");
                oldValue.setCurrent(true);
                return oldValue;
            }
            return verifyReport;
        });
    }


    public void setRecordStore(String permit, VerifyReport verifyReport) {
        getCurrentRecordStore().remove(verifyReport.getId());
        verifyReport.setCurrent(false);
        getRecordStore().putIfAbsent(verifyReport.getId(), verifyReport);
    }


    public Map<String, VerifyReport> getCurrentRecordStore() {
        if (this.currentRecordStore.get() == null)
            this.currentRecordStore.set(new HashMap<>());
        return currentRecordStore.get();
    }

    protected Map<String, VerifyReport> getRecordStore() {
        if (this.recordStore.get() == null)
            this.recordStore.set(new HashMap<>());
        return recordStore.get();
    }

    protected Map<String, Set<String>> getPermitReportIdMap() {
        if (this.permitReportIdMap.get() == null)
            this.permitReportIdMap.set(new HashMap<>());
        return permitReportIdMap.get();
    }

    protected Set<String> getPermitReportIdMap(String permit) {
        getPermitReportIdMap().putIfAbsent(permit, new HashSet<>());
        return getPermitReportIdMap().get(permit);
    }

    public String getReportId(VerifyReport verifyReport) {
        return UUID.randomUUID().toString().replace("-", "");
    }

    protected final Map<String, VerifyReport> getArchiversStore(VerifyReport verifyReport) {
        return getArchiversStore(getCacheKey(verifyReport));
    }

    abstract Map<String, VerifyReport> getArchiversStore(String cacheKey);

    protected String getCacheKey(VerifyReport verifyReport) {
        String name = verifyReport.getTargetClass().getName();
        return cachePrefix.replace("$1", verifyReport.getPermit()).replace("$2", name + "." + verifyReport.getTargetMethod().getName());
    }
}

