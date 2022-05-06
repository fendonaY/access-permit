package com.yyp.permit.dept.room;

import com.yyp.permit.annotation.parser.PermitAnnotationInfo;
import com.yyp.permit.context.*;
import com.yyp.permit.dept.verifier.VerifierHelper;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractArchivesRoom implements ArchivesRoom, RecycleBin {

    private NamedThreadLocal<List<VerifyReport>> recordStore = new NamedThreadLocal<>("archives");

    @Override
    public List<VerifyReport> register(PermitInfo permitInfo) {
        putInRecycleBin();
        return permitInfo.getAnnotationInfoList().stream().map(info -> {
            VerifyReport report = getReport(permitInfo, info);
            VerifyReport archiversStore = getArchiversStore(report);
            setCurrentRecordStore(info.getPermit(), archiversStore);
            return report;
        }).collect(Collectors.toList());
    }

    VerifyReport getReport(PermitInfo permitInfo, PermitAnnotationInfo annotationInfo) {
        VerifyReport verifyReport = new VerifyReport(annotationInfo.getPermit());
        verifyReport.setAnnotationInfo(annotationInfo);
        verifyReport.setSuggest(annotationInfo.getMessage());
        verifyReport.setTargetClass(permitInfo.getTargetClass());
        verifyReport.setTargetMethod(permitInfo.getTargetMethod());
        verifyReport.setTargetObj(permitInfo.getTargetObj());
        verifyReport.setArguments(permitInfo.getArguments());
        return verifyReport;
    }

    @Override
    public VerifyReport getVerifyReport(String permit) {
        PermitToken permitToken = PermitManager.getOfNonNullPermitToken();
        return permitToken.getVerifyReport(permit);
    }

    @Override
    public void archive(VerifyReport verifyReport) {
        Assert.notNull(verifyReport.getAnnotationInfo(), "unknown report");
        PermitAnnotationInfo annotationInfo = verifyReport.getAnnotationInfo();
        if (verifyReport.isArchive())
            return;
        if (annotationInfo.isValidCache()) {
            verifyReport.setArchive(true);
            putCache(verifyReport);
        }
    }

    @Override
    public void remove(String permit) {
        PermitToken permitToken = PermitManager.getOfNonNullPermitToken();
        Map<String, VerifyReport> recordStore = permitToken.getRecordStore();
        if (ObjectUtils.isEmpty(recordStore)) {
            recordStore.remove(permit);
        }
    }

    @Override
    public List<VerifyReport> getVerifyReportList() {
        return getRecordStore();
    }

    @Override
    public Rubbish produceRubbish() {
        return () -> {
            try {
                doArchive();
            } finally {
                recordStore.remove();
            }
        };
    }

    protected final void doArchive() {
        List<VerifyReport> recordStore = this.getRecordStore();
        if (!ObjectUtils.isEmpty(recordStore)) {
            recordStore.forEach(report -> archive(report));
        }
    }

    public void setCurrentRecordStore(String permit, VerifyReport verifyReport) {
        PermitToken permitToken = PermitManager.getOfNonNullPermitToken();
        Map<String, VerifyReport> recordStore = permitToken.getRecordStore();
        if (recordStore == null) {
            recordStore = new HashMap<>();
            permitToken.setRecordStore(recordStore);
        }
        if (verifyReport.getValidResult() == null) {
            VerifierHelper.findValidData(verifyReport);
            verifyReport.setId(getReportId(verifyReport));
        }
        recordStore.put(permit, verifyReport);
        getRecordStore().add(verifyReport);
    }

    protected List<VerifyReport> getRecordStore() {
        if (this.recordStore.get() == null)
            this.recordStore.set(new ArrayList<>());
        return recordStore.get();
    }

    private final VerifyReport getArchiversStore(VerifyReport verifyReport) {
        VerifyReport archiverReport = getArchiversStore(getCacheKey(verifyReport));
        return archiverReport != null ? archiverReport : verifyReport;
    }

    /**
     * 获取档案id
     */
    protected abstract String getReportId(VerifyReport verifyReport);

    protected abstract VerifyReport getArchiversStore(String cacheKey);

    protected abstract String getCacheKey(VerifyReport verifyReport);

    protected abstract void putCache(VerifyReport verifyReport);
}

