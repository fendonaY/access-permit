package com.yyp.permit.spring;

import com.yyp.permit.support.ArchivesRoom;
import com.yyp.permit.support.LocalCacheArchivesRoom;
import com.yyp.permit.support.RedisCacheArchivesRoom;
import com.yyp.permit.support.verify.repository.DBVerifyRepository;
import com.yyp.permit.support.verify.repository.RedisVerifyRepository;
import com.yyp.permit.support.verify.repository.TestVerifyRepository;
import com.yyp.permit.support.verify.repository.VerifyRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration(proxyBeanMethods = false)
//@ConditionalOnSingleCandidate(DataSource.class)
//@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({PermissionProperties.class})
public class PropertiesAutoConfiguration {

    private PermissionProperties properties;

    public PropertiesAutoConfiguration(PermissionProperties properties) {
        ofNull(properties::getCache, Boolean.TRUE, properties::setCache);
        ofNull(properties::getTimeUnit, TimeUnit.MILLISECONDS, properties::setTimeUnit);

        PermissionProperties.VerifyRepositoryProperties verifyRepository = properties.getVerifyRepository();
        ofNull(verifyRepository::getRepository, "db", verifyRepository::setRepository);
        ofNull(verifyRepository::getLocalCache, Boolean.TRUE, verifyRepository::setLocalCache);
        ofNull(verifyRepository::getPermitName, "PERMIT", verifyRepository::setPermitName);
        ofNull(verifyRepository::getPermissionName, "EXEC_PERMIT", verifyRepository::setPermissionName);
        ofNull(verifyRepository::getInitSql, "SELECT PERMIT,EXEC_PERMIT FROM permit_dict", verifyRepository::setInitSql);

        PermissionProperties.ArchivesRoomProperties archivesRoom = properties.getArchivesRoom();
        ofNull(archivesRoom::getRoom, "redis", archivesRoom::setRoom);
        ofNull(archivesRoom::getCacheStrategy, "default", archivesRoom::setCacheStrategy);

        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public VerifyRepository getVerifyRepository(ObjectProvider<DataSource[]> dataSources) {
        String repository = properties.getVerifyRepository() == null ? "" : properties.getVerifyRepository().getRepository();
        if ("db".equals(repository)) {
            DBVerifyRepository dbVerifyRepository = new DBVerifyRepository(dataSources);
            Optional.of(properties.getVerifyRepository().getInitSql()).ifPresent(dbVerifyRepository::setRepositoryQuery);
            Optional.of(properties.getVerifyRepository().getPermitName()).ifPresent(dbVerifyRepository::setPermitName);
            Optional.of(properties.getVerifyRepository().getPermissionName()).ifPresent(dbVerifyRepository::setPermissionName);
            return dbVerifyRepository;
        } else if ("redis".equals(repository)) {
            RedisVerifyRepository redisVerifyRepository = new RedisVerifyRepository(dataSources);
            redisVerifyRepository.setCacheKey(properties.getVerifyRepository().getCacheKey());
            redisVerifyRepository.setLocalCache(properties.getVerifyRepository().getLocalCache());
            return redisVerifyRepository;
        } else {
            return new TestVerifyRepository();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ArchivesRoom getArchivesRoom() {
        String room = properties.getArchivesRoom().getRoom();
        String cacheStrategy = properties.getArchivesRoom().getCacheStrategy();
        if ("local".equals(room)) {
            return new LocalCacheArchivesRoom();
        } else if ("redis".equals(room)) {
            RedisCacheArchivesRoom redisCacheArchivesRoom = new RedisCacheArchivesRoom();
            redisCacheArchivesRoom.setCacheStrategy(cacheStrategy);
            return redisCacheArchivesRoom;
        } else throw new IllegalArgumentException(room + " illegal archivers room");
    }

    private <T> void ofNull(Supplier value, T defaultValue, Consumer<T> consumer) {
        if (ObjectUtils.isEmpty(value.get())) {
            consumer.accept(defaultValue);
        }
    }
}
