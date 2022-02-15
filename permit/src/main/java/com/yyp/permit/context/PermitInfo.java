package com.yyp.permit.context;

import com.yyp.permit.annotation.parser.PermitAnnotationInfo;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
public class PermitInfo {

    private Object targetObj;

    private Class targetClass;

    private Method targetMethod;

    private Object[] arguments;

    private List<PermitAnnotationInfo> annotationInfoList = new ArrayList<>();

    public PermitAnnotationInfo getAnnotationInfo(String permit) {
        return annotationInfoList.stream().filter(ann -> permit.equals(ann.getPermit())).findFirst().get();
    }
}
