package com.yyp.accesspermit.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * @author yyp
 * @description:
 * @date 2021/4/713:48
 */
public class PermissionVerifyRepository {

    private final Log logger = LogFactory.getLog(getClass());

    private DataSource dataSource = (DataSource) getBean(DataSource.class);

    private ApplicationContext beanFactory;

    private HashMap<String, String> sqlRepository = new HashMap(16);

    private String repository_query = "SELECT PERMIT,EXEC_PERMIT FROM permit_dict";

    private static PermissionVerifyRepository repository;

    public PermissionVerifyRepository(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }

    public PermissionVerifyRepository initRepository() {
        ValidExecutor validExecutor = getExecutor(repository_query, new Object[0]);
        int verify_warehouse_start_init = validExecutor.execute(executor -> logger.info("verify warehouse start init"));
        Assert.state(verify_warehouse_start_init == -1, "verify warehouse initialization failed");
        validExecutor.getResult().forEach(dataMap -> sqlRepository.put((String) dataMap.get("PERMIT"), (String) dataMap.get("EXEC_PERMIT")));
        return this;
    }

    public ValidExecutor getExecutor(String sql, Object[] params) {
        return getExecutor(dataSource, sql, params);
    }

    public ValidExecutor getExecutor(Object[] params, String validCode) {
        return getExecutor(dataSource, getValidPermit(validCode), params);
    }

    private ValidExecutor getExecutor(DataSource dataSource, String sql, Object[] params) {
        return new PermissionVerifyExecutor(dataSource, sql, params);
    }

    public String getValidPermit(String code) {
        return sqlRepository.getOrDefault(code, "");
    }

    public Object getBean(Class clazz) {
        return beanFactory.getBean(clazz);
    }

    public static PermissionVerifyRepository getRepository(ApplicationContext beanFactory) {
        if (repository == null) {
            Assert.notNull(beanFactory, "beanFactory can't null");
            repository = new PermissionVerifyRepository(beanFactory).initRepository();
        }
        return repository;
    }
}
