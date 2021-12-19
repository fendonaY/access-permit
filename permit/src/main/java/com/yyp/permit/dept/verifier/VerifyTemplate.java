package com.yyp.permit.dept.verifier;

import com.alibaba.fastjson.JSONObject;
import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import com.yyp.permit.dept.room.VerifyReport;
import com.yyp.permit.dept.verifier.repository.DBVerifyRepository;
import com.yyp.permit.dept.verifier.repository.VerifyRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yyp
 * @description:
 * @date 2021/4/713:48
 */
public final class VerifyTemplate {

    private VerifyExecutorHandle defaultHandle;

    private VerifyRepository verifyRepository;

    public VerifyTemplate(VerifyRepository verifyRepository) {
        this.verifyRepository = verifyRepository;
        this.defaultHandle = defaultHandle();
    }

    public void setDefaultHandle(VerifyExecutorHandle defaultHandle) {
        this.defaultHandle = defaultHandle;
    }

    public VerifyRepository getVerifyRepository() {
        return verifyRepository;
    }

    public void setVerifyRepository(DBVerifyRepository verifyRepository) {
        this.verifyRepository = verifyRepository;
    }


    public boolean validParams(VerifyReport verifyReport) {
        PermissionAnnotationInfo annotationInfo = verifyReport.getAnnotationInfo();
        ValidExecutor executor = getVerifyRepository().getExecutor(verifyReport);
        int execute = executor.execute(this.defaultHandle);
        List<Map<String, Object>> result = executor.getResult();
        verifyReport.setValidResultObject(result);
        verifyReport.setValidResult(execute == -1 ? !annotationInfo.isCanEmpty() ? !result.isEmpty() : true : execute > 0);
        return verifyReport.getValidResult();
    }

    public VerifyExecutorHandle defaultHandle() {
        return new VerifyExecutorHandle() {
            @Override
            public Object handle(ValidExecutor validExecutor) {
                DataBaseVerifyExecutor permissionValidExecutor = (DataBaseVerifyExecutor) validExecutor;
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
                return null;
            }
        };
    }
}
