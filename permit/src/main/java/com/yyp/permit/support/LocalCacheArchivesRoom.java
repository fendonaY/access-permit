package com.yyp.permit.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.yyp.permit.util.ParamUtil;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LocalCacheArchivesRoom extends AbstractArchivesRoom {

    private Map<String, VerifyReport> recordStore;

    private Map<String, Set<String>> permitReportIdMap;

    @Override
    public List<VerifyReport> register(PermissionInfo permissionInfo) {
        return permissionInfo.getAnnotationInfoList().stream().map(info -> {
            VerifyReport report = getReport(permissionInfo, info);
            setRecordStore(info.getPermit(), report);
            return report;
        }).collect(Collectors.toList());
    }

    @Override
    public String getReportId(VerifyReport verifyReport) {
        return ParamUtil.getKeyMD5(verifyReport.getPermit(), verifyReport.getArguments());
    }

    @Override
    public void archive(String permit) {
        VerifyReport verifyReport = getVerifyReport(permit);
        Assert.notNull(verifyReport.getAnnotationInfo(), "unknown report");
        if (verifyReport.isArchive())
            return;
        synchronized (verifyReport) {
            if (verifyReport.isArchive())
                return;
            verifyReport.setArchive(true);
            verifyReport.setCurrent(false);
        }
    }

    public Map<String, VerifyReport> getRecordStore() {
        if (this.recordStore == null) {
            synchronized (this) {
                if (this.recordStore == null)
                    this.recordStore = new ConcurrentHashMap<>();
            }
        }
        return recordStore;
    }

    public Map<String, Set<String>> getPermitReportIdMap() {
        if (this.permitReportIdMap == null) {
            synchronized (this) {
                if (this.permitReportIdMap == null)
                    this.permitReportIdMap = new ConcurrentHashMap<>();
            }
        }
        return permitReportIdMap;
    }

    public Set<String> getPermitReportIdMap(String permit) {
        Set<String> permitReportIds = this.getPermitReportIdMap().get(permit);
        if (permitReportIds == null) {
            synchronized (this.permitReportIdMap) {
                return this.permitReportIdMap.computeIfAbsent(permit, key -> new ConcurrentHashSet<>());
            }
        }
        return permitReportIds;
    }
}
