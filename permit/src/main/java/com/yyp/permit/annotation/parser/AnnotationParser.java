package com.yyp.permit.annotation.parser;

import org.springframework.lang.NonNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public interface AnnotationParser<T> {

    boolean isCandidateClass(Class<?> targetClass);

    @NonNull
    T parseAnnotation(AnnotatedElement element);

    @NonNull
    <E> AnnotationInfoProvider<E> getAnnotationInfo(Method method, @NonNull Class<?> targetClass);
}
