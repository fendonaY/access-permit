package com.yyp.permit.spring;

import com.yyp.permit.aspect.IdempotenceLimit;
import com.yyp.permit.dept.room.ArchivesRoom;
import com.yyp.permit.dept.room.SecurityDept;
import com.yyp.permit.dept.room.VerifyRecordDept;
import com.yyp.permit.dept.verifier.VerifyTemplate;
import com.yyp.permit.dept.verifier.repository.DBVerifyRepository;
import com.yyp.permit.dept.verifier.repository.VerifyRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({PropertiesAutoConfiguration.class})
public class PermissionAutoConfiguration {

    @Bean("mainSecurityDept")
    @ConditionalOnMissingBean
    public SecurityDept getSecurityDept(@Autowired ArchivesRoom archivesRoom) {
        VerifyRecordDept verifyRecordDept = new VerifyRecordDept(archivesRoom);
        return verifyRecordDept;
    }

    @Bean
    @ConditionalOnMissingBean
    public VerifyRepository getDBVerifyRepository(ObjectProvider<DataSource[]> dataSources) {
        return new DBVerifyRepository(dataSources);
    }

    @Bean
    public IdempotenceLimit getIdempotenceLimit() {
        return new IdempotenceLimit();
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