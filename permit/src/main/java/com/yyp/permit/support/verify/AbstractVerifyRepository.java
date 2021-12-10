package com.yyp.permit.support.verify;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

public abstract class AbstractVerifyRepository implements VerifyRepository {

    private HashMap<String, String> permitRepository;

    private DataSource dataSource;

    public AbstractVerifyRepository(HashMap<String, String> permitRepository, DataSource dataSource) {
        this.permitRepository = permitRepository;
        this.dataSource = dataSource;
    }

    public AbstractVerifyRepository() {
    }

    public HashMap<String, String> getPermitRepository() {
        return permitRepository;
    }

    public void addPermitRepository(String permit, String permission) {
        Objects.requireNonNull(permitRepository, "verify repository is null").put(permit, permission);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public VerifyRepository initRepository() {
        if (getPermitRepository() == null)
            this.permitRepository = new HashMap<>(16);
        return this;
    }

    @Override
    public String getPermission(String permit) {
        return Objects.requireNonNull(permitRepository, "verify repository is null").getOrDefault(permit, "");
    }
}
