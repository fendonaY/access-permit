package com.yyp.test.api;

import com.yyp.permit.annotation.ApiIdempotence;
import com.yyp.permit.annotation.Permission;
import com.yyp.permit.aspect.RejectStrategy;
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
    @ApiIdempotence(reject = RejectStrategy.GENTLE)
    public String list(String query) throws InterruptedException {
        Thread.sleep(500);
        test2Api.list(query + "test");
        return "test-" + query;
    }
}
