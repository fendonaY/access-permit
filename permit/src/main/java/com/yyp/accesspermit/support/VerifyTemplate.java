package com.yyp.accesspermit.support;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yyp
 * @description:
 * @date 2021/4/713:48
 */
public class VerifyTemplate implements ApplicationContextAware, InitializingBean {

    private VerifyExecutorHandle defaultHandle;

    private PermissionVerifyRepository verifyRepository;

    private ApplicationContext applicationContext;

    public boolean validParams(VerifyReport verifyReport) {
        PermissionInfo.AnnotationInfo annotationInfo = verifyReport.getAnnotationInfo();
        ValidExecutor executor = getVerifyRepository().getExecutor(verifyReport.getValidData(), verifyReport.getPermit());
        int execute = executor.execute(defaultHandle);
        List<Map<String, Object>> result = executor.getResult();
        verifyReport.setValidResultObject(getValidResultObject(executor));
        verifyReport.setValidResult(execute == -1 ? !annotationInfo.isCanEmpty() ? !result.isEmpty() : true : execute > 0);
        return verifyReport.getValidResult();
    }

    private List getValidResultObject(ValidExecutor executor) {
        return executor.getResult().stream().map(r -> JSONObject.toJSON(r)).collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() {
        if (this.defaultHandle == null)
            this.defaultHandle = new VerifyExecutorHandle() {
                @Override
                public void handle(ValidExecutor validExecutor) {
                    PermissionVerifyExecutor permissionValidExecutor = (PermissionVerifyExecutor) validExecutor;
                    Object[] params = permissionValidExecutor.getParams();
                    String sql = permissionValidExecutor.getSql();
                    List<Object> newParams = new ArrayList(params.length * 2);
                    List array = null;
                    for (int i = 0; i < params.length; i++) {
                        Object param = params[i];
                        if (param instanceof List) {
                            if (param instanceof Collection)
                                newParams.addAll(array = (List) param);
                            else
                                newParams.addAll(array = Arrays.asList((Object[]) param));
                            continue;
                        }
                        newParams.add(param);
                    }
                    if (array != null) {
                        StringBuffer stringBuffer = new StringBuffer("?");
                        for (int i = 0; i <= array.size() - 2; i++) {
                            stringBuffer.append(",?");
                        }
                        permissionValidExecutor.setSql(sql.replace("?IN?", stringBuffer.toString()));
                        permissionValidExecutor.setParams(newParams.toArray());
                        handle(validExecutor);
                    }
                }
            };
    }

    public PermissionVerifyRepository getVerifyRepository() {
        return PermissionVerifyRepository.getRepository(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
