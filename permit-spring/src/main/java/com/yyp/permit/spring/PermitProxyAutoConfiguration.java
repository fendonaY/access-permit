package com.yyp.permit.spring;

import com.yyp.permit.aspect.PermissionInterceptor;
import com.yyp.permit.dept.room.SecurityDept;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration(proxyBeanMethods=false)
@AutoConfigureAfter({PermitAutoConfiguration.class})
@ConditionalOnBean({SecurityDept.class})
public class PermitProxyAutoConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public PermissionInterceptor getPermissionInterceptor(@Autowired SecurityDept securityDept) {
        return new PermissionInterceptor(securityDept);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DefaultPointcutAdvisor permissionPointcutAdvisor(@Autowired PermissionInterceptor permissionInterceptor) {
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        defaultPointcutAdvisor.setPointcut(new PermitPointcut());
        defaultPointcutAdvisor.setAdvice(permissionInterceptor);
        return defaultPointcutAdvisor;
    }
}
