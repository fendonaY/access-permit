package com.yyp.permit.support;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class LogArchivesRoom extends AbstractArchivesRoom {

    private AbstractArchivesRoom delegate;

    public LogArchivesRoom(AbstractArchivesRoom delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<VerifyReport> register(PermissionInfo permissionInfo) {
        log.info("start registration information：{}", JSONObject.toJSONString(permissionInfo));
        return delegate.register(permissionInfo);
    }

    @Override
    public String getReportId(VerifyReport verifyReport) {
        String reportId = delegate.getReportId(verifyReport);
        log.info("{} {} get id => {}", verifyReport.getPermit(), verifyReport.getId(), reportId);
        return reportId;
    }

    @Override
    public VerifyReport getVerifyReport(String permit) {
        return delegate.getVerifyReport(permit);
    }

    @Override
    public void remove(String permit) {
        delegate.remove(permit);
    }

    @Override
    protected Map<String, Set<String>> getPermitReportIdMap() {
        return delegate.getPermitReportIdMap();
    }

    @Override
    protected Set<String> getPermitReportIdMap(String permit) {
        return delegate.getPermitReportIdMap(permit);
    }

    @Override
    protected Map<String, VerifyReport> getRecordStore() {
        return delegate.getRecordStore();
    }

    @Override
    public void archive(String permit) {
        VerifyReport verifyReport = getVerifyReport(permit);
        delegate.archive(permit);
        log.info("archive：{}", verifyReport.getId());
    }

    @Override
    public void update(VerifyReport oldReport, VerifyReport newReport) {
        delegate.update(oldReport, newReport);
    }

}
