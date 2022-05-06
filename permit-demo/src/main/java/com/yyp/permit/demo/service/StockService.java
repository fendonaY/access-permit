package com.yyp.permit.demo.service;

import com.yyp.permit.annotation.Permit;
import com.yyp.permit.demo.model.dto.BuyGoodsDto;

public interface StockService {

    //购买商品
    void buy(BuyGoodsDto buyGoodsDto);
}
