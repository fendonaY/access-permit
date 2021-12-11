package com.yyp.permit.annotation.parser;

@FunctionalInterface
public interface AnnotationInfoProvider<T> {

    T getAnnotationInfo();
}
