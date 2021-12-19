package com.yyp.permit.demo.service.impl;

import com.yyp.permit.context.PermissionContext;
import com.yyp.permit.context.PermissionManager;
import com.yyp.permit.demo.model.StockBo;
import com.yyp.permit.demo.model.UserBo;
import com.yyp.permit.demo.model.dto.BuyGoodsDto;
import com.yyp.permit.demo.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Service
public class StockServiceImpl implements StockService {

    @Override
    public void buy(BuyGoodsDto buyGoodsDto) {
        PermissionContext permissionContext = PermissionManager.getPermitToken().getPermissionContext();
        List<Map<String, Object>> buyGoods = permissionContext.getValidResultObject("buyGoods");
        Assert.notEmpty(buyGoods, "拒绝处理");
        Map<String, Object> validResult = buyGoods.get(0);
        UserBo user = (UserBo) validResult.get("user");
        StockBo stock = (StockBo) validResult.get("stock");

        //生成订单

        //生成物流信息

        //更新积分信息

        //......

    }
}
