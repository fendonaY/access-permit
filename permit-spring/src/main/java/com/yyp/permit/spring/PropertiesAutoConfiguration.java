package com.yyp.permit.spring;

import com.yyp.permit.dept.room.ArchivesRoom;
import com.yyp.permit.dept.room.LocalCacheArchivesRoom;
import com.yyp.permit.dept.room.RedisCacheArchivesRoom;
import com.yyp.permit.dept.verifier.repository.DBVerifyRepository;
import com.yyp.permit.dept.verifier.repository.EmptyVerifyRepository;
import com.yyp.permit.dept.verifier.repository.RedisVerifyRepository;
import com.yyp.permit.dept.verifier.repository.VerifyRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration(proxyBeanMethods = false)
//@ConditionalOnSingleCandidate(DataSource.class)
//@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({PermitProperties.class})
public class PropertiesAutoConfiguration {

    private PermitProperties properties;

    public PropertiesAutoConfiguration(PermitProperties properties) {
        ofNull(properties::getCache, Boolean.TRUE, properties::setCache);
        ofNull(properties::getTimeUnit, TimeUnit.MILLISECONDS, properties::setTimeUnit);

        PermitProperties.VerifyRepositoryProperties verifyRepository = properties.getVerifyRepository();
        ofNull(verifyRepository::getRepository, "db", verifyRepository::setRepository);
        ofNull(verifyRepository::getLocalCache, Boolean.TRUE, verifyRepository::setLocalCache);
        ofNull(verifyRepository::getPermitName, "PERMIT", verifyRepository::setPermitName);
        ofNull(verifyRepository::getPermissionName, "EXEC_PERMIT", verifyRepository::setPermissionName);
        ofNull(verifyRepository::getInitSql, "SELECT PERMIT,EXEC_PERMIT FROM permit_dict", verifyRepository::setInitSql);

        PermitProperties.ArchivesRoomProperties archivesRoom = properties.getArchivesRoom();
        ofNull(archivesRoom::getRoom, "redis", archivesRoom::setRoom);
        ofNull(archivesRoom::getCacheStrategy, "default", archivesRoom::setCacheStrategy);

        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public VerifyRepository getVerifyRepository(ObjectProvider<DataSource[]> dataSources) {
        PermitProperties.VerifyRepositoryProperties verifyRepository = properties.getVerifyRepository();
        if ("db".equals(verifyRepository.getRepository())) {
            DBVerifyRepository dbVerifyRepository = new DBVerifyRepository(dataSources);
            dbVerifyRepository.setRepositoryQuery(verifyRepository.getInitSql());
            dbVerifyRepository.setPermissionName(verifyRepository.getPermissionName());
            dbVerifyRepository.setPermitName(verifyRepository.getPermitName());
            return dbVerifyRepository;
        } else if ("redis".equals(verifyRepository.getRepository())) {
            RedisVerifyRepository redisVerifyRepository = new RedisVerifyRepository(dataSources);
            redisVerifyRepository.setCacheKey(verifyRepository.getCacheKey());
            redisVerifyRepository.setLocalCache(verifyRepository.getLocalCache());
            return redisVerifyRepository;
        } else if ("empty".equals(verifyRepository.getRepository())) {
            return new EmptyVerifyRepository().initRepository();
        } else throw new IllegalArgumentException(verifyRepository.getRepository() + " illegal verify repository");
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
