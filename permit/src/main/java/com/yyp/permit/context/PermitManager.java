package com.yyp.permit.context;

import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.Objects;

public class PermitManager {

    private static NamedThreadLocal<PermitToken> passCheck = new NamedThreadLocal("permit token");

    private static NamedThreadLocal<Rubbish> dustbin = new NamedThreadLocal<>("dustbin");

    public static void issuedPassCheck(PermitToken permitToken) {
        permitToken.setOldPermitToken(passCheck.get());
        passCheck.set(permitToken);
    }

    public static void cancelPassCheck(PermitToken permitToken) {
        passCheck.set(permitToken.getOldPermitToken());
    }

    public static PermitToken getPermitToken() {
        return passCheck.get();
    }

    public static PermitToken getOfNonNullPermitToken() {
        PermitToken permitToken = getPermitToken();
        Assert.notNull(permitToken, "no permit token was issued");
        return permitToken;
    }

    public static void putInRecycleBin(Rubbish rubbish) {
        Objects.requireNonNull(rubbish);
        dustbin.set(rubbish);
    }

    public static void clearRecycleBin() {
        if (dustbin.get() != null) {
            dustbin.get().clear();
        }
        dustbin.remove();
    }
}
