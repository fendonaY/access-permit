package com.yyp.permit.annotation.parser;

import com.yyp.permit.annotation.Permission;
import com.yyp.permit.aspect.RejectStrategy;
import com.yyp.permit.util.AnnotationUtil;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PermissionAnnotationParser implements AnnotationParser<PermissionAnnotationInfo> {

    @Override
    public boolean isCandidateClass(Class<?> targetClass) {
        return AnnotationUtil.existsAnnotation(targetClass, Permission.class) || AnnotationUtil.existsAnnotation(targetClass, Permission.List.class);
    }

    @Override
    public PermissionAnnotationInfo parseAnnotation(AnnotatedElement element) {
        MergedAnnotation annotation = AnnotationUtil.getAnnotation(element, Permission.class);
        return getPermissionAnnotationInfo(annotation);
    }

    @Override
    public <E> AnnotationInfoProvider<E> getAnnotationInfo(Method method, Class<?> targetClass) {
        MergedAnnotation[] annotation = AnnotationUtil.getAnnotation(targetClass, method, Permission.class, Permission.List.class);
        return () -> {
            List<PermissionAnnotationInfo> annInfoList = new ArrayList();
            for (int i = 0; i < annotation.length; i++) {
                PermissionAnnotationInfo permissionAnnotationInfo = getPermissionAnnotationInfo(annotation[i]);
                if (StringUtils.hasLength(permissionAnnotationInfo.getPermit())) {
                    annInfoList.add(permissionAnnotationInfo);
                }
            }
            return (E) annInfoList;
        };
    }


    private PermissionAnnotationInfo getPermissionAnnotationInfo(MergedAnnotation annotation) {
        PermissionAnnotationInfo permissionAnnotationInfo = new PermissionAnnotationInfo();

        if (annotation == MergedAnnotation.missing()) {
            return permissionAnnotationInfo;
        }
        permissionAnnotationInfo.setMessage(annotation.getString("message"));
        permissionAnnotationInfo.setIndexes(annotation.getIntArray("indexes"));
        permissionAnnotationInfo.setNames(annotation.getStringArray("names"));
        permissionAnnotationInfo.setCanEmpty(annotation.getBoolean("canEmpty"));
        permissionAnnotationInfo.setStrategy((RejectStrategy) annotation.getEnum("strategy", RejectStrategy.class));
        permissionAnnotationInfo.setPermit(annotation.getString("permit"));
        permissionAnnotationInfo.setValidCache(annotation.getBoolean("validCache"));
        permissionAnnotationInfo.setMinCacheTime(annotation.getInt("minCacheTime"));
        permissionAnnotationInfo.setMaxCacheTime(annotation.getInt("maxCacheTime"));
        permissionAnnotationInfo.setTimeUnit((TimeUnit) annotation.getEnum("timeUnit", TimeUnit.class));
        return permissionAnnotationInfo;

    }
}
