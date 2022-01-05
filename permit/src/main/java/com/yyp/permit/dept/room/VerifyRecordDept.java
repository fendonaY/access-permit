package com.yyp.permit.dept.room;

import com.yyp.permit.aspect.RejectStrategy;
import com.yyp.permit.context.*;
import com.yyp.permit.dept.verifier.Verifier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VerifyRecordDept implements SecurityDept, InitializingBean {

    private final List<Verifier> verifierDept = new ArrayList();

    private final Map<String, List<Verifier>> specialVerifier = new HashMap<>();

    private ArchivesRoom archivesRoom;

    public VerifyRecordDept(ArchivesRoom archivesRoom) {
        this.archivesRoom = archivesRoom;
    }

    @Override
    public PermissionContext register(PermissionInfo permissionInfo) {
        //提前签发通行证
        PermissionManager.issuedPassCheck(PermitToken.reject());
        archivesRoom.register(permissionInfo);
        return new EasyGetPermissionContext(this, permissionInfo);
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
        prepareVerify(permissionContext);
        doVerify(permissionContext);
        List<VerifyReport> reports = finishVerify(permissionContext);

        String errorMsg = reports.stream().filter(report -> !report.getValidResult()).map(report -> report.getSuggest()).collect(Collectors.joining(","));
        long count = reports.stream().filter(report -> !report.getValidResult()).filter(report -> RejectStrategy.VIOLENCE.equals(report.getAnnotationInfo().getStrategy())).count();
        PermitToken exposeToken = PermissionManager.getPermitToken();
        boolean hasExpose = exposeToken != null;
        if (StringUtils.hasText(errorMsg)) {
            exposeToken = !hasExpose ? PermitToken.reject(errorMsg) : exposeToken;
            exposeToken.setExplain(errorMsg);
        } else {
            exposeToken = !hasExpose ? PermitToken.pass() : exposeToken;
            exposeToken.setVerify(true);
        }
        exposeToken.setRejectStrategy(count > 0 ? RejectStrategy.VIOLENCE : RejectStrategy.GENTLE);
        exposeToken.putPePermissionContext(permissionContext);
        if (!hasExpose) {
            PermissionManager.issuedPassCheck(exposeToken);
        }
        return exposeToken;
    }

    protected void prepareVerify(PermissionContext permissionContext) {
        for (String permit : permissionContext.getPermits()) {
            List<Verifier> verifiers = getVerifier(permit);
            verifiers.forEach(verifier -> verifier.prepareVerify(permissionContext, permit));
        }
    }

    protected void doVerify(PermissionContext permissionContext) {
        for (String permit : permissionContext.getPermits()) {
            refreshId(permit);
            List<Verifier> verifiers = getVerifier(permit);
            verifiers.forEach(verifier -> verifier.verify(this.archivesRoom.getVerifyReport(permit), permissionContext.getPermissionInfo(), permit));
        }
    }

    protected List<VerifyReport> finishVerify(PermissionContext permissionContext) {
        List<VerifyReport> reports = new ArrayList<>(permissionContext.getPermits().size());
        for (String permit : permissionContext.getPermits()) {
            List<Verifier> verifiers = getVerifier(permit);
            verifiers.forEach(verifier -> verifier.finishVerify(permissionContext, permit));
            VerifyReport verifyReport = this.archivesRoom.getVerifyReport(permit);
            reports.add(verifyReport);
            if (this.archivesRoom instanceof AbstractArchivesRoom) {
                ((AbstractArchivesRoom) this.archivesRoom).setRecordStore(permit, verifyReport);
            }
        }
        //校验完成之后需要清除一级缓存的所有数据
        ((AbstractArchivesRoom) this.archivesRoom).getCurrentRecordStore().clear();
        return reports;
    }

    private void refreshId(String permit) {
        VerifyReport verifyReport = this.archivesRoom.getVerifyReport(permit);
        if (!verifyReport.isArchive()) {
            String reportId = this.archivesRoom.getReportId(verifyReport);
            if (!reportId.equals(verifyReport.getId())) {
                VerifyReport newReport = verifyReport.clone();
                newReport.setId(reportId);
                this.archivesRoom.update(verifyReport, newReport);
            }
        }
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
