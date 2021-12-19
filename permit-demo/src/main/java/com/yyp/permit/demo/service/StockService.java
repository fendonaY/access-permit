package com.yyp.permit.demo.service;

import com.yyp.permit.annotation.Permission;
import com.yyp.permit.demo.model.dto.BuyGoodsDto;

public interface StockService {

    //购买商品
    @Permission(permit = "existUser", names = "userId")
    @Permission(permit = "buyGoods", validCache = false)
    void buy(BuyGoodsDto buyGoodsDto);
}
