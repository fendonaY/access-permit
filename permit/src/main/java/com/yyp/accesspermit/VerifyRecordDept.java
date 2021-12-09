package com.yyp.accesspermit;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VerifyRecordDept implements SecurityDept {

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
        if (StringUtils.hasText(errorMsg))
            return PermitToken.reject(errorMsg);
        return PermitToken.pass();
    }

    public void addVerifier(Verifier verifier) {
        verifierDept.add(verifier);
    }

    public void addVerifier(Verifier verifier, String permit) {
        List<Verifier> vips = specialVerifier.computeIfAbsent(permit, key -> new ArrayList<>());
        vips.add(verifier);
    }

}
