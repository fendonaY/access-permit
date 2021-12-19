package com.yyp.permit.demo.model;

import lombok.Data;

@Data
public class OrderFormBo {

    private Integer id;

    private Integer userId;

    private Integer stockId;

    private Integer count;

    private Double sumAmount;

}
