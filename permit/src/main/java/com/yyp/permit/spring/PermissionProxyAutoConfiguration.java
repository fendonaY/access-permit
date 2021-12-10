package com.yyp.permit.spring;

import com.yyp.permit.aspect.PermissionInterceptor;
import com.yyp.permit.support.SecurityDept;
import com.yyp.permit.support.VerifyRecordDept;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration(proxyBeanMethods=false)
@AutoConfigureAfter({PermissionConfiguration.class})
@ConditionalOnBean({SecurityDept.class})
public class PermissionProxyAutoConfiguration {

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
