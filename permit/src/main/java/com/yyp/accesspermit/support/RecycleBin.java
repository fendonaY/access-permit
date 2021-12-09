package com.yyp.accesspermit.support;

public interface RecycleBin {

    Rubbish produceRubbish();

    default void putInRecycleBin() {
        PermissionManager.putInRecycleBin(produceRubbish());
    }

}
