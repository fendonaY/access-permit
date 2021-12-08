package com.yyp.accesspermit;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VerifyReport implements Serializable {

    public VerifyReport(String permit) {
        this.permit = permit;
    }

    private String id;

    /**
     * 校验的数据
     */
    private Object validData;

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

    private String suggest;

}
