package com.yyp.accesspermit.aspect;

import com.yyp.accesspermit.annotation.Permission;
import com.yyp.accesspermit.support.*;
import com.yyp.accesspermit.util.AnnotationUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

public class PermissionInterceptor implements MethodInterceptor {

    private SecurityDept securityDept;

    public PermissionInterceptor(SecurityDept securityDept) {
        this.securityDept = securityDept;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<?> targetClass = (methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis()) : null);
        Method specificMethod = ClassUtils.getMostSpecificMethod(methodInvocation.getMethod(), targetClass);
        final Method method = BridgeMethodResolver.findBridgedMethod(specificMethod);
        MergedAnnotation[] annotation = AnnotationUtil.getAnnotation(targetClass, method, Permission.class, Permission.List.class);
        PermissionInfo permissionInfo = parseAnnotation(annotation);
        permissionInfo.setTargetClass(targetClass);
        permissionInfo.setArguments(methodInvocation.getArguments());
        permissionInfo.setTargetMethod(method);
        permissionInfo.setTargetObj(methodInvocation.getThis());
        if (permissionInfo.getAnnotationInfoList().isEmpty())
            return methodInvocation.proceed();

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

    private PermissionInfo parseAnnotation(MergedAnnotation[] annotations) {
        PermissionInfo permissionInfo = new PermissionInfo();
        for (MergedAnnotation annotation : annotations) {
            if (annotation == MergedAnnotation.missing())
                continue;
            permissionInfo.getAnnotationInfo(annotation);
        }
        return permissionInfo;
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
