package com.yyp.permit.support;

import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
public class PermissionInfo {

    @Deprecated
    private PermitToken.PermissionPhase phase;

    private Object targetObj;

    private Class targetClass;

    private Object[] arguments;

    private Method targetMethod;

    private List<PermissionAnnotationInfo> annotationInfoList = new ArrayList<>();

    public PermissionInfo() {
        phase = PermitToken.PermissionPhase.ACCESS;
    }

    public PermissionAnnotationInfo getAnnotationInfo(String permit) {
        return annotationInfoList.stream().filter(ann -> permit.equals(ann.getPermit())).findFirst().get();
    }

}
