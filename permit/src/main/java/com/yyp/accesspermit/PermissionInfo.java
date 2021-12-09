package com.yyp.accesspermit;

import com.yyp.accesspermit.aspect.RejectStrategy;
import lombok.Data;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
public class PermissionInfo {

    private PermitToken.PermissionPhase phase;

    private List<AnnotationInfo> annotationInfo;

    public PermissionInfo() {
        phase = PermitToken.PermissionPhase.ACCESS;
    }

    @Data
    public class AnnotationInfo {
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

    }
}
