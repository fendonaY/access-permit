package com.yyp.permit.dept.verify;

import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import com.yyp.permit.context.PermissionInfo;
import com.yyp.permit.dept.room.VerifyReport;
import com.yyp.permit.util.ParamUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VerifierHelper {

    public static void findValidData(VerifyReport verifyReport, PermissionInfo permissionInfo, PermissionAnnotationInfo annotationInfo) {
        List<Object> params = new ArrayList<>();
        if (verifyReport.getValidResult() == null) {
            int length = annotationInfo.getNames().length + annotationInfo.getIndexes().length;
            if (annotationInfo.getIndexes().length != 0) {
                List<Object> finalParams = params;
                Arrays.stream(annotationInfo.getIndexes()).forEach(index -> finalParams.add(ParamUtil.getAttr(index, "", permissionInfo.getArguments())));
            }
            params.addAll(Arrays.stream(annotationInfo.getNames()).map(name -> {
                String[] split = name.split("\\.");
                Object value;
                if (split.length > 1) {
                    value = ParamUtil.getInlayAttr(split, split[0], ParamUtil.getNextAttr(split, split[0]), permissionInfo.getArguments());
                } else {
                    value = ParamUtil.getAttrList(name, permissionInfo.getArguments());
                }
                return value;
            }).collect(Collectors.toList()));
            List<Object> collect = params.stream().filter(param -> param != null).collect(Collectors.toList());
            if (collect.size() != length) {
                verifyReport.setValidResult(annotationInfo.isCanEmpty() ? true : false);
                verifyReport.setValidResultObject(Collections.EMPTY_LIST);
            }
            verifyReport.setValidData(collect.toArray());
        }
    }
}
