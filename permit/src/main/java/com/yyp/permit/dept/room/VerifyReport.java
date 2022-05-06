package com.yyp.permit.dept.room;

import com.alibaba.fastjson.annotation.JSONField;
import com.yyp.permit.annotation.parser.PermitAnnotationInfo;
import com.yyp.permit.context.PermitToken;
import com.yyp.permit.serializer.IgnoreSerializer;
import com.yyp.permit.serializer.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Getter
@Setter
public class VerifyReport implements Serializable {

    public VerifyReport(String permit) {
        this.permit = permit;
        this.phase = PermitToken.PermissionPhase.REGISTER;
    }

    public VerifyReport() {
        this.phase = PermitToken.PermissionPhase.REGISTER;
    }

    public VerifyReport(Object targetObj, Class targetClass, Method targetMethod) {
        this.targetObj = targetObj;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.phase = PermitToken.PermissionPhase.REGISTER;
    }

    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = IgnoreSerializer.class)
    private Object targetObj;

    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = IgnoreSerializer.class)
    private Class targetClass;

    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = IgnoreSerializer.class)
    private Method targetMethod;

    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = IgnoreSerializer.class)
    private Object[] arguments;

    private PermitToken.PermissionPhase phase;

    private PermitAnnotationInfo annotationInfo;

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

    @Override
    public String toString() {
        return "VerifyReport{" +
                "id='" + id + '\'' +
                ", permit='" + permit + '\'' +
                ", archive=" + archive +
                '}';
    }
}
