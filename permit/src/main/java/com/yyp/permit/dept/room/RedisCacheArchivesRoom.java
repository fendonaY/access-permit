package com.yyp.permit.dept.room;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.yyp.permit.spring.PermitProperties;
import com.yyp.permit.util.ParamUtil;
import lombok.Getter;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

public class RedisCacheArchivesRoom extends AbstractArchivesRoom {

    private final Integer verify_pass = 0;

    private final Integer verify_reject = 1;

    private final String cachePrefix = "ARCHIVES_ROOM:$1_$2";

    private String cacheStrategy = CacheStrategy.NORMAL.getStrategy();

    @Resource
    private RedissonClient redissonClient;

    /**
     * 缓存最小时间
     */
    private long minCacheTime;

    /**
     * 缓存最大时间
     */
    private long maxCacheTime;

    /**
     * 缓存时间单位
     */
    private TimeUnit timeUnit;

    public RedisCacheArchivesRoom() {
    }

    public RedisCacheArchivesRoom(PermitProperties permitProperties) {
        PermitProperties.ArchivesRoomProperties archivesRoom = permitProperties.getArchivesRoom();
        this.cacheStrategy = archivesRoom.getCacheStrategy();
        this.maxCacheTime = permitProperties.getMaxCacheTime();
        this.minCacheTime = permitProperties.getMinCacheTime();
        this.timeUnit = permitProperties.getTimeUnit();
    }

    public String getCacheStrategy() {
        return cacheStrategy;
    }

    public void setCacheStrategy(String cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }

    @Override
    public String getReportId(VerifyReport verifyReport) {
        if (verifyReport.getValidData() == null) {
            return IdUtil.simpleUUID();
        }
        return ParamUtil.getKeyMD5(verifyReport.getPermit(), verifyReport.getValidData());
    }

    @Override
    protected String getCacheKey(VerifyReport verifyReport) {
        return cachePrefix.replace("$1", verifyReport.getPermit()).replace("$2", verifyReport.getId());
    }

    @Override
    protected VerifyReport getArchiversStore(String cacheKey) {
        return decode(cacheKey);
    }

    @Override
    public void putCache(VerifyReport verifyReport) {
        redissonClient.getBucket(getCacheKey(verifyReport)).set(encode(verifyReport), RandomUtil.getRandom().nextLong(minCacheTime, maxCacheTime), timeUnit);
    }

    private VerifyReport decode(String cacheKey) {
        Object o = redissonClient.getBucket(cacheKey).get();
        if (o == null)
            return null;
        if (CacheStrategy.SIMPLE.getStrategy().equals(cacheStrategy)) {
            VerifyReport report = new VerifyReport();
            IdInfo idInfo = parseId(cacheKey);
            report.setId(idInfo.getId());
            report.setPermit(idInfo.getPermit());
            report.setArchive(true);
            report.setValidResult(verify_reject.equals(o) ? Boolean.FALSE : Boolean.TRUE);
            return report;
        } else {
            return JSONObject.parseObject(String.valueOf(o), VerifyReport.class);
        }
    }

    private Object encode(VerifyReport verifyReport) {
        if (CacheStrategy.SIMPLE.getStrategy().equals(cacheStrategy)) {
            return verifyReport.getValidResult() ? verify_pass : verify_reject;
        } else {
            return JSONObject.toJSONString(verifyReport);
        }
    }

    private IdInfo parseId(String reportId) {
        String[] split = reportId.split("\\$1\\$");
        return new IdInfo(split[0], split[1]);
    }

    @Getter
    private enum CacheStrategy {

        SIMPLE("simple"),

        NORMAL("normal");

        private String strategy;

        CacheStrategy(String strategy) {
            this.strategy = strategy;
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
