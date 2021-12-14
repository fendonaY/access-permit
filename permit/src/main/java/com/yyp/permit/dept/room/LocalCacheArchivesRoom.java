package com.yyp.permit.dept.room;

import com.alibaba.fastjson.JSONObject;
import com.yyp.permit.util.ParamUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCacheArchivesRoom extends AbstractArchivesRoom {

    private Map<String, String> localCache = new ConcurrentHashMap<>(128);

    @Override
    public String getReportId(VerifyReport verifyReport) {
        return ParamUtil.getKeyMD5(verifyReport.getPermit(), verifyReport.getArguments());
    }

    @Override
    Map<String, VerifyReport> getArchiversStore(String cacheKey) {
        Map<String, VerifyReport> result = new HashMap<>(localCache.size());
        localCache.forEach((key, value) -> result.put(key, JSONObject.parseObject(value, VerifyReport.class)));
        return result;
    }

    @Override
    public void putCache(VerifyReport verifyReport) {
        localCache.put(verifyReport.getId(), JSONObject.toJSONString(verifyReport));
    }
}
