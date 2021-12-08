package com.yyp.accesspermit.util;

import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtil {

    private String msg;

    public String getA() {
        return msg;
    }

    public void setA(String a) {
        this.msg = a;
    }

    public static boolean existsAnnotation(Class targetClass, Class<? extends Annotation> annotationType) {
        List<Method> mes = getMethods(targetClass);
        for (int i = 0; i < mes.size(); i++) {
            Method method = mes.get(i);
            AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
                    method, annotationType, false, false);
            if (attributes == null) {
                Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
                if (AnnotatedElementUtils.findMergedAnnotationAttributes(
                        specificMethod.getDeclaringClass(), annotationType, false, false) == null)
                    continue;
                return true;
            }
            return true;
        }
        return false;
    }

    public static <T extends Annotation> MergedAnnotation getAnnotation(AnnotatedElement element, Class<T> clazz) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none()).get(clazz);
    }


    private static List getMethods(Class<?> clazz) {
        List actualMethods = new ArrayList();
        Enhancer.getMethods(clazz, null, actualMethods);
        return actualMethods;
    }
}
