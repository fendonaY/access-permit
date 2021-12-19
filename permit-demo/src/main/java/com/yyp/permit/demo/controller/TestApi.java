package com.yyp.permit.demo.controller;

import com.yyp.permit.demo.model.dto.BuyGoodsDto;
import com.yyp.permit.demo.service.StockService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/permit/test")
public class TestApi {

    @Resource
    private StockService stockService;

    @PostMapping("/buy")
    public String buy(@RequestBody BuyGoodsDto buyGoodsDto) {
        stockService.buy(buyGoodsDto);
        return "购买成功";
    }
}
