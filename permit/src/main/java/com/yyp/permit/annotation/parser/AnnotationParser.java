package com.yyp.permit.annotation.parser;

import org.springframework.lang.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public interface AnnotationParser<T> {

    boolean isCandidateClass(Class<?> targetClass);

    @Nullable
    T parseAnnotation(AnnotatedElement element);

    @Nullable
    <E> AnnotationInfoProvider<E> getAnnotationInfo(Method method, @Nullable Class<?> targetClass);
}
