package com.yyp.permit.spring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "permission")
@Getter
@Setter
public class PermissionProperties {

    /**
     * 是否开启校验结果缓存 默认开启
     */
    private boolean cache;

    /**
     * 缓存最小时间
     */
    private int minCacheTime;

    /**
     * 缓存最大时间
     */
    private int maxCacheTime;

    /**
     * 缓存时间单位
     */
    private TimeUnit timeUnit;

    private VerifyRepositoryProperties verifyRepository;

    private ArchivesRoomProperties archivesRoom;


    @Getter
    @Setter
    public static class VerifyRepositoryProperties {

        private String repository;

        private boolean localCache;

        private String cacheKey;

        private String initSql;

        private String permitName;

        private String permissionName;


    }

    @Getter
    @Setter
    public static class ArchivesRoomProperties {

        /**
         * 文档室
         */
        private String room;

    }


}
