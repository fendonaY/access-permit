package com.yyp.accesspermit.spring;

import com.yyp.accesspermit.annotation.Permission;
import com.yyp.accesspermit.util.AnnotationUtil;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.annotation.MergedAnnotation;

import java.lang.reflect.Method;

public class PermissionPointcut implements Pointcut, MethodMatcher {

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
        MergedAnnotation[] annotation = AnnotationUtil.getAnnotation(targetClass, method, Permission.class, Permission.List.class);
        return annotation.length != 0;
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
