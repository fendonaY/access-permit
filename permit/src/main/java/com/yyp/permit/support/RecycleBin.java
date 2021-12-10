package com.yyp.permit.support;

public interface RecycleBin {

    Rubbish produceRubbish();

    default void putInRecycleBin() {
        PermissionManager.putInRecycleBin(produceRubbish());
    }

}
