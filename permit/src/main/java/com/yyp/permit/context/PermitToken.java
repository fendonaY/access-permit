package com.yyp.permit.context;

import com.yyp.permit.aspect.RejectStrategy;
import com.yyp.permit.dept.room.VerifyReport;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;

@Data
public class PermitToken {
    public enum PermissionPhase {
        REGISTER, PREPARE,VERIFIED
    }

    private PermitContext permitContext;

    private PermitToken oldPermitToken;

    private boolean verify;

    private PermissionPhase phase;

    private RejectStrategy rejectStrategy;

    private String explain;

    private Map<String, VerifyReport> recordStore;

    public void putPePermissionContext(PermitContext permitContext) {
        this.permitContext = permitContext;
    }

    public static PermitToken pass(String explain) {
        return new PermitToken(true, PermissionPhase.VERIFIED, explain);
    }

    public static PermitToken pass() {
        return new PermitToken(true, PermissionPhase.VERIFIED, null);
    }

    public static PermitToken reject(String explain) {
        return new PermitToken(false, PermissionPhase.VERIFIED, explain);
    }

    public static PermitToken reject() {
        return new PermitToken(false, PermissionPhase.VERIFIED, null);
    }

    public PermitToken() {
    }

    public PermitToken(boolean verify, PermissionPhase phase, String explain) {
        this.verify = verify;
        this.phase = phase;
        this.explain = explain;
    }

    public VerifyReport getVerifyReport(String permit) {
        VerifyReport verifyReport = Optional.ofNullable(this.getRecordStore()).map(recordStore -> recordStore.get(permit)).orElse(null);
        Assert.notNull(verifyReport, permit + " archives doesn't exist");
        return verifyReport;
    }
}
