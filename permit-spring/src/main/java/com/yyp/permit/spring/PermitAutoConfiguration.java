package com.yyp.permit.spring;

import com.yyp.permit.dept.room.ArchivesRoom;
import com.yyp.permit.dept.room.SecurityDept;
import com.yyp.permit.dept.room.VerifyRecordDept;
import com.yyp.permit.dept.verifier.VerifyTemplate;
import com.yyp.permit.dept.verifier.repository.VerifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
public class PermitAutoConfiguration {

    @Bean("mainSecurityDept")
    @ConditionalOnMissingBean
    public SecurityDept getSecurityDept(@Autowired ArchivesRoom archivesRoom) {
        VerifyRecordDept verifyRecordDept = new VerifyRecordDept(archivesRoom);
        return verifyRecordDept;
    }

    @Bean
    public VerifyTemplate getVerifyTemplate(@Autowired VerifyRepository verifyRepository) {
        return new VerifyTemplate(verifyRepository.initRepository());
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public VerifierScanner registerVerifierScanner() {
        return new VerifierScanner();
    }
}
