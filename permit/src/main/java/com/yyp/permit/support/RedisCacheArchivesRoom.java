package com.yyp.permit.support;

import com.alibaba.fastjson.JSONObject;
import com.yyp.permit.util.ParamUtil;
import lombok.Getter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;

import java.util.HashMap;
import java.util.Map;

public class RedisCacheArchivesRoom extends AbstractArchivesRoom {

    private NamedThreadLocal<Map<String, CacheInfo>> cache = new NamedThreadLocal("currentArchiversStore");

    private final Integer VERIFY_PASS = 0;

    private final Integer VERIFY_REJECT = 1;

    private String cacheStrategy;

    @Autowired
    private RedissonClient redissonClient;

    public RedisCacheArchivesRoom() {
    }

    public RedisCacheArchivesRoom(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public String getCacheStrategy() {
        return cacheStrategy;
    }

    public void setCacheStrategy(String cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }

    @Override
    public String getReportId(VerifyReport verifyReport) {
        return ParamUtil.getKeyMD5(verifyReport.getPermit(), verifyReport.getValidData());
    }

    @Override
    Map<String, VerifyReport> getArchiversStore(String cacheKey) {
        Map<String, Object> currentArchiversStore = getCurrentArchiversStore(cacheKey);
        Map<String, VerifyReport> result = new HashMap<>(currentArchiversStore.size());
        currentArchiversStore.forEach((key, value) -> {
            String strValue = String.valueOf(value);
            if (strValue.length() == 1) {
                VerifyReport verifyReport = new VerifyReport();
                IdInfo idInfo = parseId(key);
                verifyReport.setId(key);
                verifyReport.setPermit(idInfo.getPermit());
                verifyReport.setArchive(true);
                verifyReport.setCurrent(false);
                verifyReport.setValidResult(VERIFY_REJECT.equals(value) ? Boolean.FALSE : Boolean.TRUE);
                result.put(key, verifyReport);
            } else
                result.put(key, JSONObject.parseObject(strValue, VerifyReport.class));
        });
        return result;
    }

    @Override
    public void putCache(VerifyReport verifyReport) {
        Map<String, Object> cache = getCurrentArchiversStore(getCacheKey(verifyReport));
        if ("simple".equals(cacheStrategy))
            cache.putIfAbsent(verifyReport.getId(), verifyReport.getValidResult() ? VERIFY_PASS : VERIFY_REJECT);
        cache.putIfAbsent(verifyReport.getId(), JSONObject.toJSONString(verifyReport));
    }

    @Override
    public Rubbish produceRubbish() {
        return () -> {
            try {
                super.produceRubbish().clear();
            } finally {
                cache.remove();
            }
        };
    }

    private Map<String, Object> getCurrentArchiversStore(String cacheKey) {
        if (cache.get() == null) {
            cache.set(new HashMap<>());
        }
        if (!cache.get().containsKey(cacheKey)) {
            CacheInfo cacheInfo = new CacheInfo(cacheKey, redissonClient.getMap(cacheKey));
            cache.get().put(cacheKey, cacheInfo);
        }
        return cache.get().get(cacheKey).getCache();
    }

    private IdInfo parseId(String reportId) {
        String[] split = reportId.split("\\$\\$");
        return new IdInfo(split[0], split[1]);
    }

    @Getter
    private class CacheInfo {

        private String cacheKey;

        private Map<String, Object> cache;

        public CacheInfo(String cacheKey, Map<String, Object> cache) {
            this.cacheKey = cacheKey;
            this.cache = cache;
        }

        public String getCacheKey() {
            return cacheKey;
        }

        public Map<String, Object> getCache() {
            return cache;
        }
    }

    @Getter
    private class IdInfo {

        private String permit;

        private String id;

        public IdInfo(String permit, String id) {
            this.permit = permit;
            this.id = id;
        }
    }

}
