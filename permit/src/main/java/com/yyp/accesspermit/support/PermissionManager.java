package com.yyp.accesspermit.support;

import org.springframework.core.NamedThreadLocal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionManager {

    private static NamedThreadLocal<PermitToken> passCheck = new NamedThreadLocal("permit token");

    private static NamedThreadLocal<List<Rubbish>> dustbin = new NamedThreadLocal<>("dustbin");

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

    public static void putInRecycleBin(Rubbish rubbish) {
        Objects.requireNonNull(rubbish);
        if (dustbin.get() == null)
            dustbin.set(new ArrayList<>());
        dustbin.get().add(rubbish);
    }

    public static void clearRecycleBin() {
        if (dustbin.get() != null) {
            dustbin.get().forEach(r -> r.clear());
        }
        dustbin.remove();
    }

}
