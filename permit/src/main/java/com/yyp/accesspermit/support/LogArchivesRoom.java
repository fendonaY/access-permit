package com.yyp.accesspermit.support;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LogArchivesRoom extends AbstractArchivesRoom {

    private ArchivesRoom cacheArchivesRoom = new CacheArchivesRoom();

    @Override
    public List<VerifyReport> register(PermissionInfo permissionInfo) {
        return cacheArchivesRoom.register(permissionInfo);
    }

    @Override
    protected String getReportId(VerifyReport verifyReport) {
        return "log@" + super.getReportId(verifyReport);
    }

    @Override
    public VerifyReport getVerifyReport(String permit) {
        return cacheArchivesRoom.getVerifyReport(permit);
    }

    @Override
    public void remove(String permit) {
        cacheArchivesRoom.remove(permit);
    }

    @Override
    public void archive(String permit) {
        VerifyReport verifyReport = getVerifyReport(permit);
        log.info("id:{} permit:{} archive", verifyReport.getId(), verifyReport.getPermit());
    }

    @Override
    public void update(VerifyReport verifyReport) {
        cacheArchivesRoom.update(verifyReport);
    }

}
