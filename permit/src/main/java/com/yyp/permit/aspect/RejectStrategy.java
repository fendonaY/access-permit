package com.yyp.permit.aspect;

import lombok.Getter;

/**
 * @author yyp
 * @description:
 * @date 2021/5/3110:30
 */
@Getter
public enum RejectStrategy {

    /**
     * 暴力拒绝
     * 以异常的形式对外抛出
     */
    VIOLENCE(1),

    /**
     * 温柔拒绝
     * 以默认值的形式直接返回
     */
    GENTLE(2),

    ;


    private Integer strategy;

    RejectStrategy(Integer strategy) {
        this.strategy = strategy;
    }
}
