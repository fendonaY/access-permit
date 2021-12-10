package com.yyp.permit.support.verify;

import com.yyp.permit.support.PermissionVerifyExecutor;
import com.yyp.permit.support.VerifyReport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * @author yyp
 * @description:
 * @date 2021/4/713:48
 */
public class DBVerifyRepository extends AbstractVerifyRepository {

    private final Log logger = LogFactory.getLog(getClass());

    private final String repository_query = "SELECT PERMIT,EXEC_PERMIT FROM permit_dict";

    public DBVerifyRepository(ObjectProvider<DataSource[]> dataSources) {
        DataSource[] ifAvailable = dataSources.getIfAvailable();
        if (ifAvailable != null)
            setDataSource(ifAvailable[0]);
    }

    public DBVerifyRepository initRepository() {
        super.initRepository();
        ValidExecutor validExecutor = getExecutor(repository_query, new Object[0]);
        int verify_warehouse_start_init = validExecutor.execute(executor -> logger.info("verify warehouse start init"));
        Assert.state(verify_warehouse_start_init != -1, "verify warehouse initialization failed");
        validExecutor.getResult().forEach(dataMap -> addPermitRepository((String) dataMap.get("PERMIT"), (String) dataMap.get("EXEC_PERMIT")));
        return this;
    }

    @Override
    public ValidExecutor getExecutor(VerifyReport verifyReport) {
        return getExecutor(verifyReport.getValidData(), verifyReport.getPermit());
    }

    public ValidExecutor getExecutor(String sql, Object[] params) {
        return getExecutor(getDataSource(), sql, params);
    }

    public ValidExecutor getExecutor(Object[] params, String validCode) {
        return getExecutor(getDataSource(), getPermission(validCode), params);
    }

    private ValidExecutor getExecutor(DataSource dataSource, String sql, Object[] params) {
        return new PermissionVerifyExecutor(dataSource, sql, params);
    }
}
