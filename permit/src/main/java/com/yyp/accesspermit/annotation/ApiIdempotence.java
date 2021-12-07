package com.yyp.accesspermit.annotation;

import com.yyp.accesspermit.aspect.RejectStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author yyp
 * @description:接口幂等性限制 </p>
 * 底层实现为redisson 参考官方文档{@literal https://github.com/redisson/redisson?_ga=2.201175786.575262349.1622114659-2121655372.1622114659}
 * @date 2021/5/2714:00
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface ApiIdempotence {

    /**
     * 提示消息
     */
    String message() default "操作处理中，请勿重复操作";

    /**
     * 自定义键
     */
    String lock() default "";

    /**
     * 需要校验的参数的下标
     * 如果与 {@link ApiIdempotence#names()}同时指定，indexes优先。
     * 从0开始
     */
    int[] indexes() default {};

    /**
     * 需要校验的字段名称
     * 如果是JSON参数形式，则需要指定字段名称，如果表单参数形式则不用
     */
    String[] names() default {};

    /**
     * 接口的拥有锁的进度提示
     * true 提示，false不提示
     */
    boolean schedule() default true;

    /**
     * 拒绝策略
     */
    RejectStrategy reject() default RejectStrategy.VIOLENCE;

    /**
     * 业务执行时间
     * 如果为0则业务执行执行默为30秒，自动支持锁的维护
     * 如果大于0，业务执行时间依据设定的时间，且不会自动锁的维护
     * 建议开发者自己设定业务执行时间，因为自动支持锁的维护将会消耗一定的资源
     */
    long time() default 30 * 1000;

    /**
     * 时间单位
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;


}
