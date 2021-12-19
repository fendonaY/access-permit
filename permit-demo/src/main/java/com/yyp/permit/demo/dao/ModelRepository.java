package com.yyp.permit.demo.dao;

import com.yyp.permit.demo.model.StockBo;
import com.yyp.permit.demo.model.UserBo;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 模拟数据库
 */
@Component
public class ModelRepository {

    private List<UserBo> userRepository = new ArrayList<>(16);

    private List<StockBo> stockRepository = new ArrayList<>(16);

    private List<UserBo> orderRepository = new ArrayList<>(16);


    @PostConstruct
    public void init() {
        UserBo userBo = new UserBo();
        userBo.setAsset(100d);
        userBo.setId(1);
        userBo.setName("张三");
        userRepository.add(userBo);

        StockBo stockBo = new StockBo();
        stockBo.setAmount(12d);
        stockBo.setId(1);
        stockBo.setCount(10);
        stockBo.setGoodsName("商品1");
        stockRepository.add(stockBo);
    }

    public UserBo getUser(Integer id) {
        Optional<UserBo> first = userRepository.stream().filter(userBo -> id.equals(userBo.getId())).findFirst();
        return first.get();
    }

    public StockBo getStock(Integer id) {
        Optional<StockBo> first = stockRepository.stream().filter(userBo -> id.equals(userBo.getId())).findFirst();
        return first.get();
    }

}
