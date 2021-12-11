package com.yyp.permit.support;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class LogArchivesRoom extends AbstractArchivesRoom {

    @Autowired
    private AbstractArchivesRoom delegation;

    @Override
    public List<VerifyReport> register(PermissionInfo permissionInfo) {
        log.info("start registration information：{}", JSONObject.toJSONString(permissionInfo));
        return delegation.register(permissionInfo);
    }

    @Override
    public String getReportId(VerifyReport verifyReport) {
        String reportId = delegation.getReportId(verifyReport);
        log.info("{} get id=>", JSONObject.toJSONString(verifyReport), reportId);
        return reportId;
    }

    @Override
    public VerifyReport getVerifyReport(String permit) {
        return delegation.getVerifyReport(permit);
    }

    @Override
    public void remove(String permit) {
        delegation.remove(permit);
    }

    @Override
    protected Map<String, Set<String>> getPermitReportIdMap() {
        return delegation.getPermitReportIdMap();
    }

    @Override
    protected Set<String> getPermitReportIdMap(String permit) {
        return delegation.getPermitReportIdMap(permit);
    }

    @Override
    protected Map<String, VerifyReport> getRecordStore() {
        return delegation.getRecordStore();
    }

    @Override
    public void archive(String permit) {
        delegation.archive(permit);
        VerifyReport verifyReport = getVerifyReport(permit);
        log.info("archive：{}", JSONObject.toJSONString(verifyReport));
    }

    @Override
    public void update(VerifyReport oldReport, VerifyReport newReport) {
        delegation.update(oldReport, newReport);
    }

}
