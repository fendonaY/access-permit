package com.yyp.permit.test.api;

import com.yyp.permit.annotation.Permission;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test2Api {
    @Permission(permit = "test", indexes = 0, canEmpty = true)
    public String list(String query) {
        return "test-" + query;
    }
}
