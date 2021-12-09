package com.yyp.accesspermit.spring;

import com.yyp.accesspermit.aspect.PermissionInterceptor;
import com.yyp.accesspermit.support.ArchivesRoom;
import com.yyp.accesspermit.support.CacheArchivesRoom;
import com.yyp.accesspermit.support.SecurityDept;
import com.yyp.accesspermit.support.VerifyRecordDept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
@Slf4j
public class PermissionConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = ArchivesRoom.class)
    public ArchivesRoom getArchivesRoom() {
        return new CacheArchivesRoom();
    }

    @Bean
    @ConditionalOnMissingBean(value = SecurityDept.class)
    public SecurityDept getSecurityDept(@Autowired ArchivesRoom archivesRoom) {
        return new VerifyRecordDept(archivesRoom);
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
