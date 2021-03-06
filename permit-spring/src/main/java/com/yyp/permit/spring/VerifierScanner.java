package com.yyp.permit.spring;

import com.yyp.permit.annotation.parser.AnnotationParser;
import com.yyp.permit.annotation.parser.SupportVerifyAnnotationInfo;
import com.yyp.permit.annotation.parser.SupportVerifyAnnotationParser;
import com.yyp.permit.dept.room.SecurityDept;
import com.yyp.permit.dept.room.VerifyRecordDept;
import com.yyp.permit.dept.verifier.DefaultVerifier;
import com.yyp.permit.dept.verifier.Verifier;
import com.yyp.permit.dept.verifier.VerifyTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.Collection;

@Slf4j
public class VerifierScanner implements BeanFactoryPostProcessor, ApplicationContextAware, ApplicationRunner {

    private ApplicationContext applicationContext;

    private AnnotationParser<SupportVerifyAnnotationInfo> annotationParser = new SupportVerifyAnnotationParser();

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void run(ApplicationArguments args) {
        SecurityDept mainSecurityDept = applicationContext.getBean("mainSecurityDept", SecurityDept.class);
        if (!beanFactory.containsBean("defaultVerifier"))
            beanFactory.registerSingleton("defaultVerifier", new DefaultVerifier(applicationContext.getBean(VerifyTemplate.class)));
        Collection<Verifier> values = applicationContext.getBeansOfType(Verifier.class).values();
        if (mainSecurityDept instanceof VerifyRecordDept) {
            VerifyRecordDept verifyRecordDept = (VerifyRecordDept) mainSecurityDept;
            values.forEach(verifier -> {
                Class<?> targetClass = AopProxyUtils.ultimateTargetClass(verifier);
                if (annotationParser.isCandidateClass(targetClass)) {
                    SupportVerifyAnnotationInfo annotationInfo = annotationParser.parseAnnotation(targetClass);
                    if (StringUtils.hasText(annotationInfo.getPermit()))
                        verifyRecordDept.addVerifier(verifier, annotationInfo.getPermit());
                    else verifyRecordDept.addVerifier(verifier);
                    log.info("scan verifier and add => {}", targetClass.getName());
                }
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
