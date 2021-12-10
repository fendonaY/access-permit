package com.yyp.permit.spring;

import com.yyp.permit.support.ArchivesRoom;
import com.yyp.permit.support.CacheArchivesRoom;
import com.yyp.permit.support.SecurityDept;
import com.yyp.permit.support.VerifyRecordDept;
import com.yyp.permit.support.verify.*;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class PermissionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ArchivesRoom getArchivesRoom(@Autowired RedissonClient redissonClient) {
        return new CacheArchivesRoom(redissonClient);
    }

    @Bean
    public SecurityDept getSecurityDept(@Autowired ArchivesRoom archivesRoom) {
        VerifyRecordDept verifyRecordDept = new VerifyRecordDept(archivesRoom);
        return verifyRecordDept;
    }

    @Bean
    public VerifyRepository getDBVerifyRepository(ObjectProvider<DataSource[]> dataSources) {
        return new DBVerifyRepository(dataSources);
    }

    @Bean
    public VerifyTemplate getVerifyTemplate(@Autowired VerifyRepository verifyRepository) {
        return new VerifyTemplate(verifyRepository.initRepository());
    }

    @Bean
    public Verifier getVerifier(@Autowired VerifyTemplate verifyTemplate) {
        return new CacheVerifier(verifyTemplate);
    }
}
