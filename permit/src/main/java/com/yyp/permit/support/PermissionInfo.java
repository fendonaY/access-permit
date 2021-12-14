package com.yyp.permit.support;

import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
public class PermissionInfo {

    private Object targetObj;

    private Class targetClass;

    private Method targetMethod;

    private Object[] arguments;

    private List<PermissionAnnotationInfo> annotationInfoList = new ArrayList<>();

    public PermissionAnnotationInfo getAnnotationInfo(String permit) {
        return annotationInfoList.stream().filter(ann -> permit.equals(ann.getPermit())).findFirst().get();
    }
}
