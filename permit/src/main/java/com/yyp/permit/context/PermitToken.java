package com.yyp.permit.context;

import com.yyp.permit.aspect.RejectStrategy;
import org.springframework.core.NamedThreadLocal;

public class PermitToken {

    private NamedThreadLocal<PermissionContext> permissionContextCache = new NamedThreadLocal<>("permissionContext");

    public enum PermissionPhase {
        REGISTER, VERIFIED
    }

    private PermitToken oldPermitToken;

    private boolean verify;

    private PermissionPhase phase;

    private RejectStrategy rejectStrategy;

    private String explain;

    public PermissionContext getPermissionContext() {
        return permissionContextCache.get();
    }

    public void putPePermissionContext(PermissionContext permissionContext) {
        permissionContextCache.set(permissionContext);
    }

    protected void clear() {
        permissionContextCache.remove();
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

    public PermitToken getOldPermitToken() {
        return oldPermitToken;
    }

    public void setOldPermitToken(PermitToken oldPermitToken) {
        this.oldPermitToken = oldPermitToken;
    }

    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public PermissionPhase getPhase() {
        return phase;
    }

    public void setPhase(PermissionPhase phase) {
        this.phase = phase;
    }

    public RejectStrategy getRejectStrategy() {
        return rejectStrategy;
    }

    public void setRejectStrategy(RejectStrategy rejectStrategy) {
        this.rejectStrategy = rejectStrategy;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}
