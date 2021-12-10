package com.yyp.accesspermit.spring;

import com.yyp.accesspermit.annotation.Permission;
import com.yyp.accesspermit.util.AnnotationUtil;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

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
        return AnnotationUtil.existsAnnotation(targetClass, Permission.class) || AnnotationUtil.existsAnnotation(targetClass, Permission.List.class);
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
