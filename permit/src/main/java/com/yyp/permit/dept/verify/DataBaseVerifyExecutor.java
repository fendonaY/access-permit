package com.yyp.permit.dept.verify;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * @author yyp
 * @description:
 * @date 2021/4/713:48
 */
public class DataBaseVerifyExecutor implements ValidExecutor {
    protected final Log logger = LogFactory.getLog(getClass());

    private DataSource dataSource;

    private String sql;

    private Object[] params;

    private List<Map<String, Object>> result = new ArrayList();

    public DataBaseVerifyExecutor(DataSource dataSource, String sql, Object[] params) {
        this.dataSource = dataSource;
        this.sql = sql;
        this.params = params;
    }

    @Override
    public int execute(VerifyExecutorHandle verifyExecutorHandle) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            verifyExecutorHandle.handle(this);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ResultSetMetaData rsMeta = resultSet.getMetaData();
                int columnCount = rsMeta.getColumnCount();
                HashMap data = new HashMap(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    data.put(rsMeta.getColumnLabel(i), resultSet.getObject(i));
                }
                if (sql.toUpperCase(Locale.ROOT).startsWith("SELECT COUNT"))
                    return resultSet.getInt(1);
                result.add(data);
            }
        } catch (Exception e) {
            logger.error("error Permit" + sql + " " + e.getMessage());
            return 0;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return -1;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
