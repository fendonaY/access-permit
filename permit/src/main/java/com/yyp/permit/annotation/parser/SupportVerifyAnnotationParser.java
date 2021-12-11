package com.yyp.permit.annotation.parser;

import com.yyp.permit.annotation.SupportVerify;
import com.yyp.permit.util.AnnotationUtil;
import org.springframework.core.annotation.MergedAnnotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class SupportVerifyAnnotationParser implements AnnotationParser<SupportVerifyAnnotationInfo> {

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtil.existsAnnotation(targetClass, SupportVerify.class);
    }

    @Override
    public SupportVerifyAnnotationInfo parseAnnotation(AnnotatedElement element) {
        MergedAnnotation annotation = AnnotationUtil.getAnnotation(element, SupportVerify.class);
        SupportVerifyAnnotationInfo annotationInfo = new SupportVerifyAnnotationInfo();
        if (annotation != MergedAnnotation.missing()) {
            annotationInfo.setPermit(annotation.getString("permit"));
        }
        return annotationInfo;
    }

    @Override
    public <E> AnnotationInfoProvider<E> getAnnotationInfo(Method method, Class<?> targetClass) {
        return () -> (E) parseAnnotation(targetClass);
    }
}
