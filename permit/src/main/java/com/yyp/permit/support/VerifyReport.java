package com.yyp.permit.support;

import com.alibaba.fastjson.annotation.JSONField;
import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import com.yyp.permit.serializer.IgnoreSerializer;
import com.yyp.permit.serializer.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Getter
@Setter
public class VerifyReport implements Serializable, Cloneable {

    public VerifyReport(String permit) {
        this.permit = permit;
    }

    public VerifyReport() {
    }

    public VerifyReport(Object targetObj, Class targetClass, Method targetMethod) {
        this.targetObj = targetObj;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
    }

    @SneakyThrows
    @Override
    protected VerifyReport clone() {
        return (VerifyReport) super.clone();
    }

    private PermissionAnnotationInfo annotationInfo;

    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = IgnoreSerializer.class)
    private Object targetObj;

    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = IgnoreSerializer.class)
    private Class targetClass;

    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = IgnoreSerializer.class)
    private Method targetMethod;

    @JSONField(serializeUsing = ToStringSerializer.class, deserializeUsing = IgnoreSerializer.class)
    private Object[] arguments;

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

    /**
     * 当前
     */
    private boolean current;

}
