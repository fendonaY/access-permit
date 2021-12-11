package com.yyp.permit.support;

import com.alibaba.fastjson.JSONObject;
import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import com.yyp.permit.util.ParamUtil;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class RedisCacheArchivesRoom extends AbstractArchivesRoom implements RecycleBin {

    private NamedThreadLocal<Map<String, VerifyReport>> recordStore = new NamedThreadLocal<>("archives");

    private NamedThreadLocal<Map<String, Set<String>>> permitReportIdMap = new NamedThreadLocal<>("permit report id map");

    private final String cachePrefix = "ARCHIVES_ROOM@$1_$2";

    @Autowired
    private RedissonClient redissonClient;

    public RedisCacheArchivesRoom() {
    }

    public RedisCacheArchivesRoom(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public List<VerifyReport> register(PermissionInfo permissionInfo) {
        Assert.isTrue(permissionInfo.getPhase() == PermitToken.PermissionPhase.ACCESS, "permissionInfo invalid");
        permissionInfo.setPhase(PermitToken.PermissionPhase.REGISTER);
        putInRecycleBin();
        return permissionInfo.getAnnotationInfoList().stream().map(info -> {
            VerifyReport report = getReport(info);
            report.setTargetClass(permissionInfo.getTargetClass());
            report.setTargetMethod(permissionInfo.getTargetMethod());
            report.setTargetObj(permissionInfo.getTargetObj());
            RMap<String, String> cacheMap = redissonClient.getMap(getCacheKey(report));
            cacheMap.forEach((key, value) -> setRecordStore(parseId(key), JSONObject.parseObject(value, VerifyReport.class)));
            setRecordStore(info.getPermit(), report);
            return report;
        }).collect(Collectors.toList());
    }

    @Override
    public String getReportId(VerifyReport verifyReport) {
        return ParamUtil.getKeyMD5(verifyReport.getPermit(), verifyReport.getValidData());
    }

    @Override
    public void archive(String permit) {
        VerifyReport verifyReport = getVerifyReport(permit);
        Assert.notNull(verifyReport.getAnnotationInfo(), "unknown report");
        PermissionAnnotationInfo annotationInfo = verifyReport.getAnnotationInfo();
        if (verifyReport.isArchive())
            return;
        if (annotationInfo.isValidCache()) {
            verifyReport.setArchive(true);
            verifyReport.setCurrent(false);
            RMap<Object, Object> map = redissonClient.getMap(getCacheKey(verifyReport));
            map.putIfAbsent(verifyReport.getId(), JSONObject.toJSONString(verifyReport));
        }
    }

    public Map<String, VerifyReport> getRecordStore() {
        if (this.recordStore.get() == null)
            this.recordStore.set(new HashMap<>());
        return recordStore.get();
    }

    public Map<String, Set<String>> getPermitReportIdMap() {
        if (this.permitReportIdMap.get() == null)
            this.permitReportIdMap.set(new HashMap<>());
        return permitReportIdMap.get();
    }

    public Set<String> getPermitReportIdMap(String permit) {
        getPermitReportIdMap().putIfAbsent(permit, new HashSet<>());
        return getPermitReportIdMap().get(permit);
    }


    private String getCacheKey(VerifyReport verifyReport) {
        String name = verifyReport.getTargetClass().getName();
        return cachePrefix.replace("$1", verifyReport.getPermit()).replace("$2", name + "." + verifyReport.getTargetMethod().getName());
    }

    private static String parseId(String id) {
        Objects.requireNonNull(id);
        return id.split("\\$\\$")[0];
    }

    @Override
    public Rubbish produceRubbish() {
        return () -> {
            try {
                Map<String, Set<String>> permitOfIdMap = permitReportIdMap.get();
                if (permitOfIdMap != null) {
                    Set<String> strings = permitOfIdMap.keySet();
                    strings.forEach(permit -> archive(permit));
                }
            } finally {
                permitReportIdMap.remove();
                recordStore.remove();
            }

        };
    }
}
