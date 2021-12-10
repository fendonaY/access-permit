package com.yyp.accesspermit.spring;

import com.yyp.accesspermit.aspect.PermissionInterceptor;
import com.yyp.accesspermit.support.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.Arrays;

@Configuration
@Slf4j
public class PermissionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ArchivesRoom getArchivesRoom(@Autowired RedissonClient redissonClient) {
        return new CacheArchivesRoom(redissonClient);
    }

    @Bean
    public Verifier getVerifier(@Autowired VerifyTemplate verifyTemplate) {
        return new CacheVerifier(verifyTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityDept getSecurityDept(ObjectProvider<Verifier[]> verifiers, @Autowired ArchivesRoom archivesRoom) {
        VerifyRecordDept verifyRecordDept = new VerifyRecordDept(archivesRoom);
        Verifier[] verifierList = verifiers.getIfAvailable();
        if (verifierList != null) {
            Arrays.stream(verifierList).forEach(v -> verifyRecordDept.addVerifier(v));
        }
        return verifyRecordDept;
    }

    @Bean
    public VerifyTemplate getVerifyTemplate() {
        return new VerifyTemplate();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public PermissionInterceptor getPermissionInterceptor(@Autowired SecurityDept securityDept) {
        return new PermissionInterceptor(securityDept);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DefaultPointcutAdvisor permissionPointcutAdvisor(@Autowired PermissionInterceptor permissionInterceptor) {
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        defaultPointcutAdvisor.setPointcut(new PermissionPointcut());
        defaultPointcutAdvisor.setAdvice(permissionInterceptor);
        return defaultPointcutAdvisor;
    }
}
