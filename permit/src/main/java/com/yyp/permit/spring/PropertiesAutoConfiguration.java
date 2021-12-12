package com.yyp.permit.spring;

import com.yyp.permit.support.ArchivesRoom;
import com.yyp.permit.support.LocalCacheArchivesRoom;
import com.yyp.permit.support.LogArchivesRoom;
import com.yyp.permit.support.RedisCacheArchivesRoom;
import com.yyp.permit.support.verify.repository.DBVerifyRepository;
import com.yyp.permit.support.verify.repository.RedisVerifyRepository;
import com.yyp.permit.support.verify.repository.TestVerifyRepository;
import com.yyp.permit.support.verify.repository.VerifyRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({PermissionProperties.class})
public class PropertiesAutoConfiguration {

    private PermissionProperties permissionProperties;

    public PropertiesAutoConfiguration(PermissionProperties permissionProperties) {
        this.permissionProperties = permissionProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public VerifyRepository getVerifyRepository(ObjectProvider<DataSource[]> dataSources) {
        String repository = permissionProperties.getVerifyRepository() == null ? "" : permissionProperties.getVerifyRepository().getRepository();
        if ("db".equals(repository)) {
            DBVerifyRepository dbVerifyRepository = new DBVerifyRepository(dataSources);
            Optional.of(permissionProperties.getVerifyRepository().getInitSql()).ifPresent(dbVerifyRepository::setRepositoryQuery);
            Optional.of(permissionProperties.getVerifyRepository().getPermitName()).ifPresent(dbVerifyRepository::setPermitName);
            Optional.of(permissionProperties.getVerifyRepository().getPermissionName()).ifPresent(dbVerifyRepository::setPermissionName);
            return dbVerifyRepository;
        } else if ("redis".equals(repository)) {
            RedisVerifyRepository redisVerifyRepository = new RedisVerifyRepository(dataSources);
            redisVerifyRepository.setCacheKey(permissionProperties.getVerifyRepository().getCacheKey());
            redisVerifyRepository.setLocalCache(permissionProperties.getVerifyRepository().isLocalCache());
            return redisVerifyRepository;
        } else {
            return new TestVerifyRepository();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ArchivesRoom getArchivesRoom() {
        String room = permissionProperties.getArchivesRoom() == null ? "" : permissionProperties.getArchivesRoom().getRoom();
        if ("log".equals(room)) {
            return new LogArchivesRoom(new LocalCacheArchivesRoom());
        } else if ("local".equals(room)) {
            return new LocalCacheArchivesRoom();
        } else {
            return new RedisCacheArchivesRoom();
        }
    }
}
