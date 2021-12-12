package com.yyp.test.api;

import com.yyp.permit.annotation.Permission;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test2Api {
    @Permission(permit = "test", indexes = 0, canEmpty = true)
    public String list(String query) {
        return "test-" + query;
    }
}
