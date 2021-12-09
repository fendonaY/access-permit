package com.yyp.accesspermit.support;

import com.alibaba.fastjson.JSONObject;
import com.yyp.accesspermit.util.ParamUtil;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CacheArchivesRoom extends AbstractArchivesRoom implements RecycleBin {

    private NamedThreadLocal<Map<String, VerifyReport>> recordStore = new NamedThreadLocal<>("archives");

    private final String cachePrefix = "archives_$1_$2";

    private RedissonClient redissonClient;

    public CacheArchivesRoom() {
        this.recordStore.set(new HashMap<>());
    }

    public CacheArchivesRoom(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.recordStore.set(new HashMap<>());
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
    protected String getReportId(VerifyReport verifyReport) {
        return ParamUtil.getKeyMD5(verifyReport.getPermit(), verifyReport.getValidData());
    }

    @Override
    public VerifyReport getVerifyReport(String permit) {
        VerifyReport verifyReport = getRecordStore().get(permit);
        Assert.notNull(verifyReport, permit + " archives doesn't exist");
        return verifyReport;
    }

    @Override
    public void remove(String permit) {
        getRecordStore().remove(permit);
    }

    @Override
    public void archive(String permit) {
        VerifyReport verifyReport = getVerifyReport(permit);
        Assert.notNull(verifyReport.getAnnotationInfo(), "unknown report");
        PermissionInfo.AnnotationInfo annotationInfo = verifyReport.getAnnotationInfo();
        if (verifyReport.isArchive())
            return;
        if (annotationInfo.isValidCache()) {
            RMap<Object, Object> map = redissonClient.getMap(getCacheKey(verifyReport));
            map.putIfAbsent(verifyReport.getId(), verifyReport);
            verifyReport.setArchive(true);
        }
    }

    @Override
    public void update(VerifyReport verifyReport) {
        this.recordStore.get().computeIfPresent(verifyReport.getPermit(), (key, value) -> verifyReport);
    }

    public Map<String, VerifyReport> getRecordStore() {
        return recordStore.get();
    }

    public void setRecordStore(String permit, VerifyReport verifyReport) {
        this.recordStore.get().computeIfAbsent(permit, key -> verifyReport);
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
        return () -> recordStore.remove();
    }
}
