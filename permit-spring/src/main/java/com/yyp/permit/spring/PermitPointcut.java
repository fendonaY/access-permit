package com.yyp.permit.spring;

import com.yyp.permit.annotation.Permit;
import com.yyp.permit.util.AnnotationUtil;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

import java.lang.reflect.Method;

public class PermitPointcut implements Pointcut, MethodMatcher {

    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return AnnotationUtil.existsAnnotation(targetClass, Permit.class) || AnnotationUtil.existsAnnotation(targetClass, Permit.List.class);
    }

    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        return false;
    }

}
