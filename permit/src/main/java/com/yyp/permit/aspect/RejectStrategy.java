package com.yyp.permit.aspect;

import lombok.Getter;

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
