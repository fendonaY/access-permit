package com.yyp.permit.aspect;

import com.yyp.permit.annotation.parser.AnnotationInfoProvider;
import com.yyp.permit.annotation.parser.AnnotationParser;
import com.yyp.permit.annotation.parser.PermissionAnnotationInfo;
import com.yyp.permit.annotation.parser.PermissionAnnotationParser;
import com.yyp.permit.support.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.List;

public class PermissionInterceptor implements MethodInterceptor {

    private SecurityDept securityDept;

    private AnnotationParser annotationParser = new PermissionAnnotationParser();

    public PermissionInterceptor(SecurityDept securityDept) {
        this.securityDept = securityDept;
    }

    public PermissionInterceptor() {
    }

    public void setSecurityDept(SecurityDept securityDept) {
        this.securityDept = securityDept;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<?> targetClass = (methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis()) : null);
        Method specificMethod = ClassUtils.getMostSpecificMethod(methodInvocation.getMethod(), targetClass);
        final Method method = BridgeMethodResolver.findBridgedMethod(specificMethod);
        if (!annotationParser.isCandidateClass(targetClass))
            return methodInvocation.proceed();

        PermissionInfo permissionInfo = new PermissionInfo();
        AnnotationInfoProvider<List<PermissionAnnotationInfo>> annotationInfo = annotationParser.getAnnotationInfo(method, targetClass);
        permissionInfo.setTargetClass(targetClass);
        permissionInfo.setArguments(methodInvocation.getArguments());
        permissionInfo.setTargetMethod(method);
        permissionInfo.setTargetObj(methodInvocation.getThis());
        permissionInfo.setAnnotationInfoList(annotationInfo.getAnnotationInfo());
        PermissionContext permissionContext = securityDept.register(permissionInfo);
        PermitToken permitToken = securityDept.securityVerify(permissionContext);
        PermissionManager.issuedPassCheck(permitToken);
        try {
            if (!permitToken.isVerify()) {
                Assert.isTrue(!RejectStrategy.VIOLENCE.equals(permitToken.getRejectStrategy()), permitToken.getExplain());
                return returnFail(method);
            }
            return methodInvocation.proceed();
        } finally {
            PermissionManager.cancelPassCheck(permitToken);
            if (PermissionManager.getPermitToken() == null)
                PermissionManager.clearRecycleBin();
        }
    }

    private Object returnFail(Method method) {
        Class clazz = method.getReturnType();
        if (clazz.isPrimitive()) {
            if (clazz == int.class) {
                return Integer.valueOf(0);
            } else if (clazz == long.class) {
                return Long.valueOf(0);
            } else if (clazz == short.class) {
                return Short.valueOf((short) 0);
            } else if (clazz == byte.class) {
                return Byte.valueOf((byte) 0);
            } else if (clazz == float.class) {
                return Float.valueOf(0);
            } else if (clazz == double.class) {
                return Double.valueOf(0);
            } else if (clazz == boolean.class) {
                return Boolean.FALSE;
            }
            return null;
        } else
            return null;
    }

}
