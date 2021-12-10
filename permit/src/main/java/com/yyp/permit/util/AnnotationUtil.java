package com.yyp.permit.util;

import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtil {

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

    public static MergedAnnotation[] getAnnotation(Class<?> targetClass, Method method, Class annotationClass, Class annotationsClass) {
        MergedAnnotation valid = AnnotationUtil.getAnnotation(method, annotationClass);
        valid = valid == MergedAnnotation.missing() ? AnnotationUtil.getAnnotation(targetClass, annotationClass) : valid;
        MergedAnnotation[] value = new MergedAnnotation[0];

        if (annotationsClass != null) {
            MergedAnnotation validList = AnnotationUtil.getAnnotation(method, annotationsClass);
            if (validList != MergedAnnotation.missing()) {
                value = validList.getAnnotationArray("value", annotationClass);
                return value;
            }
        }
        if (valid != MergedAnnotation.missing()) {
            value = new MergedAnnotation[1];
            value[0] = valid;
        }
        return value;
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
