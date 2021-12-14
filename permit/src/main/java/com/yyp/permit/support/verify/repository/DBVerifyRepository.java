package com.yyp.permit.support.verify.repository;

import com.yyp.permit.support.verify.ValidExecutor;
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

    private String permitName;

    private String permissionName;

    private String repositoryQuery;

    public DBVerifyRepository(ObjectProvider<DataSource[]> dataSources) {
        DataSource[] ifAvailable = dataSources.getIfAvailable();
        if (ifAvailable != null)
            setDataSource(ifAvailable[0]);
    }

    public DBVerifyRepository() {
    }

    public String getPermitName() {
        return permitName;
    }

    public void setPermitName(String permitName) {
        this.permitName = permitName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getRepositoryQuery() {
        return repositoryQuery;
    }

    public void setRepositoryQuery(String repositoryQuery) {
        this.repositoryQuery = repositoryQuery;
    }

    public DBVerifyRepository initRepository() {
        super.initRepository();
        ValidExecutor validExecutor = getExecutor(repositoryQuery, new Object[0]);
        int verify_warehouse_start_init = validExecutor.execute(executor -> {
            logger.info("verify warehouse start init");
            return null;
        });
        Assert.state(verify_warehouse_start_init != 0, "verify db repository initialization failed");
        validExecutor.getResult().forEach(dataMap -> addPermitRepository((String) dataMap.get(permitName), dataMap.get(permissionName)));
        return this;
    }
}
