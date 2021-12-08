package com.yyp.accesspermit;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LogArchivesRoom extends AbstractArchivesRoom {

    private CacheArchivesRoom cacheArchivesRoom = new CacheArchivesRoom();

    @Override
    public List<VerifyReport> register(PermissionInfo permissionInfo) {
        return cacheArchivesRoom.register(permissionInfo);
    }

    @Override
    protected String getReportId() {
        return "log@" + super.getReportId();
    }

    @Override
    public VerifyReport getVerifyReport(String permit) {
        return cacheArchivesRoom.getReport(permit);
    }

    @Override
    public void remove(String permit) {
        cacheArchivesRoom.remove(permit);
    }

    @Override
    public void archive(VerifyReport verifyReport) {
        log.info("id:{} permit:{} archive", verifyReport.getId(), verifyReport.getPermit());
    }

    @Override
    public void update(VerifyReport verifyReport) {
        cacheArchivesRoom.update(verifyReport);
    }

}
