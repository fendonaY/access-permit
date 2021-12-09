//package com.yyp.accesspermit.support;
//
//
//import com.yyp.accesspermit.annotation.Permission;
//import com.yyp.accesspermit.aspect.PermissionInterceptor;
//import com.yyp.accesspermit.util.AnnotationUtil;
//import lombok.SneakyThrows;
//import org.aopalliance.intercept.MethodInterceptor;
//import org.springframework.aop.TargetSource;
//import org.springframework.aop.framework.AopProxyUtils;
//import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
//import org.springframework.beans.BeansException;
//
///**
// * @author yyp
// * @description:
// * @date 2021/4/618:06
// */
//public class PermissionScanner extends AbstractAutoProxyCreator {
//
//    private MethodInterceptor interceptor;
//
//    @Override
//    protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass, String beanName, TargetSource customTargetSource)
//            throws BeansException {
//        if (interceptor == null)
//            return null;
//        return new Object[]{interceptor};
//    }
//
//    @SneakyThrows
//    @Override
//    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
//        interceptor = null;
//        Class<?> target = AopProxyUtils.ultimateTargetClass(bean);
//        if (!AnnotationUtil.existsAnnotation(target, Permission.class)) {
//            return bean;
//        }
//        interceptor = new PermissionInterceptor();
//        setProxyTargetClass(true);
//        return super.wrapIfNecessary(bean, beanName, cacheKey);
//    }
//
//}
//
