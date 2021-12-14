package com.yyp.permit.context;

import com.yyp.permit.aspect.RejectStrategy;
import lombok.Data;

@Data
public class PermitToken {

    public enum PermissionPhase {
        REGISTER, VERIFIED
    }

    private PermitToken oldPermitToken;

    private boolean verify;

    private PermissionPhase phase;

    private RejectStrategy rejectStrategy;

    private String explain;

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
}
