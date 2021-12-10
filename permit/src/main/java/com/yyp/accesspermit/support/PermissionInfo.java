package com.yyp.accesspermit.support;

import com.yyp.accesspermit.aspect.RejectStrategy;
import lombok.Data;
import org.springframework.core.annotation.MergedAnnotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
public class PermissionInfo {

    private PermitToken.PermissionPhase phase;

    private Object targetObj;

    private Class targetClass;

    private Object[] arguments;

    private Method targetMethod;

    private List<AnnotationInfo> annotationInfoList = new ArrayList<>();

    public PermissionInfo() {
        phase = PermitToken.PermissionPhase.ACCESS;
    }

    public AnnotationInfo getAnnotationInfo(MergedAnnotation parsePermission) {
        AnnotationInfo annotationInfo = new AnnotationInfo(parsePermission);
        annotationInfoList.add(annotationInfo);
        return annotationInfo;
    }

    @Data
    public static class AnnotationInfo {
        private String message;

        private int[] indexes;

        private String[] names;

        private boolean canEmpty;

        private RejectStrategy strategy;

        private String permit;

        private boolean validCache;

        private int minCacheTime;

        private int maxCacheTime;

        private TimeUnit timeUnit;

        public AnnotationInfo() {
        }

        public AnnotationInfo(MergedAnnotation parsePermission) {
            this.message = parsePermission.getString("message");
            this.indexes = parsePermission.getIntArray("indexes");
            this.names = parsePermission.getStringArray("names");
            this.canEmpty = parsePermission.getBoolean("canEmpty");
            this.strategy = (RejectStrategy) parsePermission.getEnum("strategy", RejectStrategy.class);
            this.permit = parsePermission.getString("permit");
            this.validCache = parsePermission.getBoolean("validCache");
            this.minCacheTime = parsePermission.getInt("minCacheTime");
            this.maxCacheTime = parsePermission.getInt("maxCacheTime");
            this.timeUnit = (TimeUnit) parsePermission.getEnum("timeUnit", TimeUnit.class);
        }
    }
}
