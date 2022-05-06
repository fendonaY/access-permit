package com.yyp.permit.dept;

import com.yyp.permit.aspect.RejectStrategy;
import com.yyp.permit.context.*;
import com.yyp.permit.dept.room.ArchivesRoom;
import com.yyp.permit.dept.room.VerifyReport;
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

        List<VerifyReport> verifyReportList = archivesRoom.getVerifyReportList();

        List<VerifyReport> checkList = verifyReportList.stream().filter(verifyReport -> verifyReport.getValidResult() == null).collect(Collectors.toList());
        prepareVerify(permitContext, checkList);
        doVerify(permitContext, checkList);
        finishVerify(permitContext, checkList);

        String errorMsg = verifyReportList.stream().filter(report -> !report.getValidResult()).map(report -> report.getSuggest()).collect(Collectors.joining(","));
        long count = verifyReportList.stream().filter(report -> !report.getValidResult()).filter(report -> RejectStrategy.VIOLENCE.equals(report.getAnnotationInfo().getStrategy())).count();
        if (StringUtils.hasText(errorMsg)) {
            exposeToken.setExplain(errorMsg);
            exposeToken.setVerify(false);
        } else {
            exposeToken.setVerify(true);
        }
        exposeToken.setRejectStrategy(count > 0 ? RejectStrategy.VIOLENCE : RejectStrategy.GENTLE);
        exposeToken.putPePermissionContext(permitContext);
        return exposeToken;
    }

    protected void prepareVerify(PermitContext permitContext, List<VerifyReport> reports) {
        for (VerifyReport report : reports) {
            List<Verifier> verifiers = getVerifier(report.getPermit());
            verifiers.forEach(verifier -> verifier.prepareVerify(permitContext, report));
        }
    }

    protected void doVerify(PermitContext permitContext, List<VerifyReport> reports) {
        for (VerifyReport report : reports) {
            List<Verifier> verifiers = getVerifier(report.getPermit());
            verifiers.forEach(verifier -> verifier.verify(permitContext, report));
        }
    }

    protected void finishVerify(PermitContext permitContext, List<VerifyReport> reports) {
        for (VerifyReport report : reports) {
            List<Verifier> verifiers = getVerifier(report.getPermit());
            verifiers.forEach(verifier -> verifier.finishVerify(permitContext, report));
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
