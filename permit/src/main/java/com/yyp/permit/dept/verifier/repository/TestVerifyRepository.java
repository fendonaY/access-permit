package com.yyp.permit.dept.verifier.repository;

import org.springframework.stereotype.Component;

public class TestVerifyRepository extends AbstractVerifyRepository {

    public TestVerifyRepository initRepository() {
        super.initRepository();
//        addPermitRepository("test", "SELECT COUNT(1) FROM TEST WHERE ID= ?");
        return this;
    }
}
