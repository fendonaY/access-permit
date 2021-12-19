package com.yyp.permit.dept.verifier.repository;

import com.yyp.permit.dept.room.VerifyReport;
import com.yyp.permit.dept.verifier.DataBaseVerifyExecutor;
import com.yyp.permit.dept.verifier.FunctionalVerify;
import com.yyp.permit.dept.verifier.FunctionalVerifyExecutor;
import com.yyp.permit.dept.verifier.ValidExecutor;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractVerifyRepository implements VerifyRepository {

    private Map<String, Object> permitRepository;

    private DataSource dataSource;

    public AbstractVerifyRepository(Map<String, Object> permitRepository, DataSource dataSource) {
        this.permitRepository = permitRepository;
        this.dataSource = dataSource;
    }

    public AbstractVerifyRepository() {
    }

    public void addPermitRepository(String permit, Object permission) {
        checkSupport(permission);
        Assert.notNull(permission, "permission is null");
        Assert.notNull(permitRepository, "verify repository is null");
        Assert.isTrue(!permitRepository.containsKey(permit), "permission of permit already exists：" + permit);
        permitRepository.put(permit, permission);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public VerifyRepository initRepository() {
        if (this.permitRepository == null)
            this.permitRepository = new HashMap<>(16);
        return this;
    }

    @Override
    public ValidExecutor getExecutor(VerifyReport verifyReport) {
        return getExecutor(verifyReport, verifyReport.getValidData(), verifyReport.getPermit());
    }

    protected ValidExecutor getExecutor(String sql, Object[] params) {
        return getDefaultExecutor(getDataSource(), sql, params);
    }

    protected ValidExecutor getExecutor(VerifyReport verifyReport, Object[] params, String validCode) {
        Object permission = getPermission(validCode);
        if (permission instanceof FunctionalVerify) {
            return new FunctionalVerifyExecutor(verifyReport, (FunctionalVerify) permission);
        }
        return getDefaultExecutor(getDataSource(), permission, params);
    }

    private ValidExecutor getDefaultExecutor(DataSource dataSource, Object sql, Object[] params) {
        Objects.requireNonNull(dataSource, "dataSource is null");
        return new DataBaseVerifyExecutor(dataSource, String.valueOf(sql), params);
    }

    @Override
    public Object getPermission(String permit) {
        Objects.requireNonNull(permitRepository, "verify repository is null");
        Assert.notEmpty(permitRepository, "verify repository not exist permission");
        return permitRepository.getOrDefault(permit, "");
    }

    void checkSupport(Object permission) {
        boolean type1 = permission instanceof String;
        boolean type2 = permission instanceof FunctionalVerify;
        Assert.isTrue(type1 || type2, "permission no support [" + permission.getClass().getName() + "]");
    }

}
