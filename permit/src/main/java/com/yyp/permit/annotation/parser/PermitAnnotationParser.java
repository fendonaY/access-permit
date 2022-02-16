package com.yyp.permit.annotation.parser;

import com.yyp.permit.annotation.Permit;
import com.yyp.permit.aspect.RejectStrategy;
import com.yyp.permit.util.AnnotationUtil;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PermitAnnotationParser implements AnnotationParser<PermitAnnotationInfo> {

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtil.existsAnnotation(targetClass, Permit.class) || AnnotationUtil.existsAnnotation(targetClass, Permit.List.class);
    }

    @Override
    public PermitAnnotationInfo parseAnnotation(AnnotatedElement element) {
        MergedAnnotation annotation = AnnotationUtil.getAnnotation(element, Permit.class);
        return getPermissionAnnotationInfo(annotation);
    }

    @Override
    public <E> AnnotationInfoProvider<E> getAnnotationInfo(Method method, Class<?> targetClass) {
        MergedAnnotation[] annotation = AnnotationUtil.getAnnotation(targetClass, method, Permit.class, Permit.List.class);
        return () -> {
            List<PermitAnnotationInfo> annInfoList = new ArrayList();
            for (int i = 0; i < annotation.length; i++) {
                PermitAnnotationInfo permitAnnotationInfo = getPermissionAnnotationInfo(annotation[i]);
                if (StringUtils.hasLength(permitAnnotationInfo.getPermit())) {
                    annInfoList.add(permitAnnotationInfo);
                }
            }
            return (E) annInfoList;
        };
    }


    private PermitAnnotationInfo getPermissionAnnotationInfo(MergedAnnotation annotation) {
        PermitAnnotationInfo permitAnnotationInfo = new PermitAnnotationInfo();

        if (annotation == MergedAnnotation.missing()) {
            return permitAnnotationInfo;
        }
        permitAnnotationInfo.setMessage(annotation.getString("message"));
        permitAnnotationInfo.setIndexes(annotation.getIntArray("indexes"));
        permitAnnotationInfo.setNames(annotation.getStringArray("names"));
        permitAnnotationInfo.setCanEmpty(annotation.getBoolean("canEmpty"));
        permitAnnotationInfo.setStrategy((RejectStrategy) annotation.getEnum("strategy", RejectStrategy.class));
        permitAnnotationInfo.setPermit(annotation.getString("permit"));
        permitAnnotationInfo.setValidCache(annotation.getBoolean("validCache"));
        return permitAnnotationInfo;

    }
}
