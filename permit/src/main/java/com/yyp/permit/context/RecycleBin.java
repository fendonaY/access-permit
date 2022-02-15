package com.yyp.permit.context;

public interface RecycleBin {

    Rubbish produceRubbish();

    default void putInRecycleBin() {
        PermitManager.putInRecycleBin(produceRubbish());
    }

}
