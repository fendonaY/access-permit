package com.yyp.permit.spring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "permit")
@Getter
@Setter
public class PermitProperties {

    /**
     * true开始校验结果缓存，false则不缓存
     */
    private Boolean cache;

    /**
     * 缓存最小时间
     */
    private long minCacheTime;

    /**
     * 缓存最大时间
     */
    private long maxCacheTime;

    /**
     * 缓存时间单位
     */
    private TimeUnit timeUnit;

    private VerifyRepositoryProperties verifyRepository = new VerifyRepositoryProperties();

    private ArchivesRoomProperties archivesRoom = new ArchivesRoomProperties();


    @Getter
    @Setter
    public static class VerifyRepositoryProperties {

        /**
         * 校验仓库
         */
        private String repository;

        /**
         * true开启本地仓库缓存，false关闭，该属性只对redis仓库有效
         */
        private Boolean localCache;

        /**
         * redis仓库的钥匙， true开启本地仓库缓存，false关闭，该属性只对redis仓库有效
         */
        private String cacheKey;

        /**
         * 校验仓库初始化语句，该属性只对关系型仓库有效
         */
        private String initSql;

        /**
         * 校验仓库的校验规则键字段名，该属性只对关系型仓库有效
         */
        private String permitName;

        /**
         * 校验仓库的校验规则值字段名，该属性只对关系型仓库有效
         */
        private String permissionName;


    }

    @Getter
    @Setter
    public static class ArchivesRoomProperties {
        /**
         * 文档室
         */
        private String room;

        /**
         * 文档缓存策略
         */
        private String cacheStrategy;

    }


}
