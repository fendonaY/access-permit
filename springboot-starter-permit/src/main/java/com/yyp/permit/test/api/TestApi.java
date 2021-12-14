package com.yyp.permit.test.api;

import com.yyp.permit.annotation.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permit")
public class TestApi {

    @Autowired
    Test2Api test2Api;

    @PostMapping("/test")
    @Permission(permit = "test", indexes = 0, canEmpty = true)
    public String list(String query) throws InterruptedException {
        test2Api.list("query2="+query);
        return "test-" + query;
    }
}
