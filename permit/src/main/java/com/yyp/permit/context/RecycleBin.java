package com.yyp.permit.context;

public interface RecycleBin {

    Rubbish produceRubbish();

    default void putInRecycleBin() {
        PermissionManager.putInRecycleBin(produceRubbish());
    }

}
