package com.yyp.permit.dept.verifier.repository;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author yyp
 * @description:
 * @date 2021/4/713:48
 */
public class RedisVerifyRepository extends AbstractVerifyRepository {

    @Autowired
    private RedisTemplate redisTemplate;

    //是否关闭本地缓存
    private boolean localCache = false;

    private String cacheKey = "";

    public RedisVerifyRepository(ObjectProvider<DataSource[]> dataSources) {
        DataSource[] ifAvailable = dataSources.getIfAvailable();
        if (ifAvailable != null)
            setDataSource(ifAvailable[0]);
    }

    public RedisVerifyRepository() {
    }

    public boolean isLocalCache() {
        return localCache;
    }

    public void setLocalCache(boolean localCache) {
        this.localCache = localCache;
    }

    public String getCacheKey() {
        Assert.hasLength(this.cacheKey, "cacheKey is null");
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public RedisVerifyRepository initRepository() {
        super.initRepository();
        if (isLocalCache()) {
            Map entries = redisTemplate.opsForHash().entries(getCacheKey());
            if (entries != null)
                entries.forEach((permit, permission) -> addPermitRepository(String.valueOf(permit), String.valueOf(permission)));
        }
        return this;
    }

    @Override
    public Object getPermission(String permit) {
        if (!isLocalCache()) {
            Object o = redisTemplate.opsForHash().get(getCacheKey(), permit);
            if (o != null) {
                return String.valueOf(o);
            }
            return "";
        }
        return super.getPermission(permit);
    }
}
