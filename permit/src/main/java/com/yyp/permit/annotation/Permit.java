package com.yyp.permit.annotation;


import com.yyp.permit.aspect.RejectStrategy;
import com.yyp.permit.context.PermitContext;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Repeatable(Permit.List.class)
public @interface Permit {

    /**
     * 提示消息
     */
    String message() default "access reject";

    /**
     * 需要校验的id的下标
     * 如果是表单的参数形式，则需要指定账套id的下标，如果JAVABEAN形式则不用。
     * 如果与 {@link Permit#names()}同时指定，indexes优先。
     * 从0开始
     */
    int[] indexes() default {};

    /**
     * 需要校验的字段名称
     * 如果是JAVABEAN参数形式，则需要指定字段名称，如果表单参数形式则不用
     */
    String[] names() default {};

    /**
     * 校验结果是否可以为空
     * true:如果依赖{@link PermitContext#getValidResultObject(String)}校验直接通过
     * false:通过校验器返回的数据如果为空，则不通过，如果不为空，则通过
     */
    boolean canEmpty() default false;

    /**
     * 拒绝策略
     */
    RejectStrategy strategy() default RejectStrategy.VIOLENCE;

    /**
     * 校验通行证
     * 校验方式会以该通行证校验
     */
    String permit();

    /**
     * 是否开启校验结果缓存 默认关闭
     */
    boolean validCache() default false;

    /**
     * Defines several {@link Permit} annotations on the same element.
     *
     * @see Permit
     */
    @Target({TYPE, METHOD})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        Permit[] value();
    }

}
