package com.yyp.permit.dept.verifier;

import com.yyp.permit.annotation.parser.PermitAnnotationInfo;
import com.yyp.permit.dept.room.VerifyReport;
import com.yyp.permit.dept.verifier.repository.DBVerifyRepository;
import com.yyp.permit.dept.verifier.repository.VerifyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class VerifyTemplate {
    private static final Logger log = LoggerFactory.getLogger(VerifyTemplate.class);

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
        PermitAnnotationInfo annotationInfo = verifyReport.getAnnotationInfo();
        ValidExecutor executor = getVerifyRepository().getExecutor(verifyReport);
        int execute = 0;
        try {
            execute = executor.execute(this.defaultHandle);
        } catch (Exception e) {
            log.error("executor execute error：{}", e);
        }
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
