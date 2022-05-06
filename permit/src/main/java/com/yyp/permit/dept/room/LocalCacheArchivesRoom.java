package com.yyp.permit.dept.room;

import com.yyp.permit.util.ParamUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCacheArchivesRoom extends AbstractArchivesRoom {

    private Map<String, VerifyReport> localCache = new ConcurrentHashMap<>(128);

    @Override
    public String getReportId(VerifyReport verifyReport) {
        return ParamUtil.getKeyMD5(verifyReport.getPermit(), verifyReport.getArguments());
    }

    @Override
    protected VerifyReport getArchiversStore(String cacheKey) {
        return localCache.get(cacheKey);
    }

    @Override
    public void putCache(VerifyReport verifyReport) {
        localCache.put(verifyReport.getId(), verifyReport);
    }

    @Override
    protected String getCacheKey(VerifyReport verifyReport) {
        return verifyReport.getId();
    }
}
