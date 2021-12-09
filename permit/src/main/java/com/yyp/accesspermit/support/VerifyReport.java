package com.yyp.accesspermit.support;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Data
public class VerifyReport implements Serializable {

    public VerifyReport(String permit) {
        this.permit = permit;
    }

    public VerifyReport(Object targetObj, Class targetClass, Method targetMethod) {
        this.targetObj = targetObj;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
    }

    private PermissionInfo.AnnotationInfo annotationInfo;

    private Object targetObj;

    private Class targetClass;

    private Method targetMethod;

    /**
     * 报告单号
     */
    private String id;

    /**
     * 校验的数据
     */
    private Object[] validData;

    /**
     * 校验结果
     */
    private Boolean validResult;

    /**
     * 校验返回值
     */
    private List validResultObject;

    /**
     * 校验返回值
     */
    private Class validClass;

    /**
     * 校验的通行证
     */
    private String permit;

    /**
     * 建议
     */
    private String suggest;

    /**
     * 归档
     */
    private boolean archive;

}
