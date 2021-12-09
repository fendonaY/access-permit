package com.yyp.accesspermit.support;

import com.yyp.accesspermit.aspect.RejectStrategy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VerifyRecordDept implements SecurityDept, InitializingBean {

    private List<Verifier> verifierDept = new ArrayList();

    private Map<String, List<Verifier>> specialVerifier = new HashMap<>();

    private ArchivesRoom archivesRoom;

    public VerifyRecordDept(ArchivesRoom archivesRoom) {
        this.archivesRoom = archivesRoom;
    }

    @Override
    public PermissionContext register(PermissionInfo permissionInfo) {
        archivesRoom.register(permissionInfo);
        return new DefaultPermissionContext(this, permissionInfo);
    }

    @Override
    public List<Verifier> getVerifierList() {
        return this.verifierDept;
    }

    @Override
    public List<Verifier> getVerifier(String permit) {
        List<Verifier> vipList = specialVerifier.getOrDefault(permit, new ArrayList<>());
        if (vipList.isEmpty()) {
            return this.verifierDept;
        }
        return vipList;
    }

    @Override
    public VerifyReport getReport(String permit) {
        return archivesRoom.getVerifyReport(permit);
    }

    @Override
    public PermitToken securityVerify(PermissionContext permissionContext) {
        List<String> permits = permissionContext.getPermits();
        List<VerifyReport> reports = new ArrayList<>(permits.size());
        for (String permit : permits) {
            List<Verifier> verifiers = getVerifier(permit);
            verifiers.forEach(verifier -> {
                verifier.prepareVerify(permissionContext);
                verifier.verify(this.archivesRoom, permissionContext.getPermissionInfo());
                verifier.finishVerify(permissionContext);
                reports.add(this.archivesRoom.getVerifyReport(permit));
            });
        }
        String errorMsg = reports.stream().filter(report -> !report.getValidResult()).map(report -> report.getSuggest()).collect(Collectors.joining(","));
        long count = reports.stream().filter(report -> !report.getValidResult()).filter(report -> RejectStrategy.VIOLENCE.equals(report.getAnnotationInfo().getStrategy())).count();

        PermitToken permitToken;
        if (StringUtils.hasText(errorMsg)) {
            permitToken = PermitToken.reject(errorMsg);
        } else
            permitToken = PermitToken.pass();
        permitToken.setRejectStrategy(count > 0 ? RejectStrategy.VIOLENCE : RejectStrategy.GENTLE);
        return permitToken;
    }

    public void addVerifier(Verifier verifier) {
        verifierDept.add(verifier);
    }

    public void addVerifier(Verifier verifier, String permit) {
        List<Verifier> vips = specialVerifier.computeIfAbsent(permit, key -> new ArrayList<>());
        vips.add(verifier);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(archivesRoom, "the archives room were not established");
    }
}
