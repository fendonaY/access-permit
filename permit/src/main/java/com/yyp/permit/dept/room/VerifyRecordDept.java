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
    public PermitContext register(PermitInfo permitInfo) {
        //提前签发通行证
        PermitManager.issuedPassCheck(PermitToken.reject());
        archivesRoom.register(permitInfo);
        return new EasyGetPermitContext(this, permitInfo);
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
    public PermitToken securityVerify(PermitContext permitContext) {
        PermitToken exposeToken = PermitManager.getPermitToken();
        exposeToken.putPePermissionContext(permitContext);
        Assert.notNull(exposeToken, "please register first");

        prepareVerify(permitContext);
        doVerify(permitContext);
        List<VerifyReport> reports = finishVerify(permitContext);

        String errorMsg = reports.stream().filter(report -> !report.getValidResult()).map(report -> report.getSuggest()).collect(Collectors.joining(","));
        long count = reports.stream().filter(report -> !report.getValidResult()).filter(report -> RejectStrategy.VIOLENCE.equals(report.getAnnotationInfo().getStrategy())).count();
        if (StringUtils.hasText(errorMsg)) {
            exposeToken.setExplain(errorMsg);
        } else {
            exposeToken.setVerify(true);
        }
        exposeToken.setRejectStrategy(count > 0 ? RejectStrategy.VIOLENCE : RejectStrategy.GENTLE);
        exposeToken.putPePermissionContext(permitContext);
        return exposeToken;
    }

    protected void prepareVerify(PermitContext permitContext) {
        for (String permit : permitContext.getPermits()) {
            List<Verifier> verifiers = getVerifier(permit);
            verifiers.forEach(verifier -> verifier.prepareVerify(permitContext, permit));
            VerifyReport report = permitContext.getReport(permit);
            if (report.getValidResult() == null)
                report.setId(archivesRoom.getReportId(report));
        }
    }

    protected void doVerify(PermitContext permitContext) {
        for (String permit : permitContext.getPermits()) {
            refreshId(permit);
            List<Verifier> verifiers = getVerifier(permit);
            verifiers.forEach(verifier -> verifier.verify(this.archivesRoom.getVerifyReport(permit), permitContext.getPermissionInfo(), permit));
        }
    }

    protected List<VerifyReport> finishVerify(PermitContext permitContext) {
        List<VerifyReport> reports = new ArrayList<>(permitContext.getPermits().size());
        for (String permit : permitContext.getPermits()) {
            List<Verifier> verifiers = getVerifier(permit);
            verifiers.forEach(verifier -> verifier.finishVerify(permitContext, permit));
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
