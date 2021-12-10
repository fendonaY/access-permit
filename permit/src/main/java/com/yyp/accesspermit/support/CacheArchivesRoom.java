package com.yyp.accesspermit.support;

import com.alibaba.fastjson.JSONObject;
import com.yyp.accesspermit.util.ParamUtil;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class CacheArchivesRoom extends AbstractArchivesRoom implements RecycleBin {

    private NamedThreadLocal<Map<String, VerifyReport>> recordStore = new NamedThreadLocal<>("archives");

    private NamedThreadLocal<Map<String, Set<String>>> permitReportIdMap = new NamedThreadLocal<>("permit report id map");

    private final String cachePrefix = "archives_$1_$2";

    private RedissonClient redissonClient;

    public CacheArchivesRoom() {
    }

    public CacheArchivesRoom(RedissonClient redissonClient) {
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
    public VerifyReport getVerifyReport(String permit) {
        Set<String> ids = getPermitReportIdMap(permit);
        Map<String, VerifyReport> recordStore = getRecordStore();
        List<VerifyReport> current = ids.stream().map(id -> recordStore.get(id)).filter(report -> report.isCurrent()).collect(Collectors.toList());
        Assert.state(!current.isEmpty(), permit + " archives doesn't exist");
        Assert.isTrue(current.size() == 1, permit + " has multiple current archives");
        return current.get(0);
    }

    @Override
    public void remove(String permit) {
        Set<String> ids = getPermitReportIdMap().remove(permit);
        if (ids != null) {
            Map<String, VerifyReport> recordStore = getRecordStore();
            ids.forEach(id -> recordStore.remove(id));
        }
    }

    @Override
    public void archive(String permit) {
        VerifyReport verifyReport = getVerifyReport(permit);
        Assert.notNull(verifyReport.getAnnotationInfo(), "unknown report");
        PermissionInfo.AnnotationInfo annotationInfo = verifyReport.getAnnotationInfo();
        if (verifyReport.isArchive())
            return;
        if (annotationInfo.isValidCache()) {
            verifyReport.setArchive(true);
            verifyReport.setCurrent(false);
            RMap<Object, Object> map = redissonClient.getMap(getCacheKey(verifyReport));
            map.putIfAbsent(verifyReport.getId(), JSONObject.toJSONString(verifyReport));
        }
    }

    @Override
    public void update(VerifyReport oldReport, VerifyReport newReport) {
        Map<String, VerifyReport> recordStore = getRecordStore();
        Set<String> permitReportIdMap = getPermitReportIdMap(oldReport.getPermit());
        permitReportIdMap.remove(oldReport.getId());
        permitReportIdMap.add(newReport.getId());
        recordStore.remove(oldReport.getId());
        recordStore.putIfAbsent(newReport.getId(), newReport);
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

    public void setRecordStore(String permit, VerifyReport verifyReport) {
        getPermitReportIdMap(permit).add(verifyReport.getId());
        getRecordStore().compute(verifyReport.getId(), (key, oldValue) -> {
            if (oldValue != null) {
                oldValue.setTargetClass(verifyReport.getTargetClass());
                oldValue.setTargetMethod(verifyReport.getTargetMethod());
                oldValue.setTargetObj(verifyReport.getTargetObj());
                oldValue.setCurrent(true);
                return oldValue;
            }
            return verifyReport;
        });
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
