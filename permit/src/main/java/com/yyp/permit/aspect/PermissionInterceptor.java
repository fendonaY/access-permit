package com.yyp.permit.aspect;

import com.yyp.permit.annotation.parser.AnnotationInfoProvider;
import com.yyp.permit.annotation.parser.AnnotationParser;
import com.yyp.permit.annotation.parser.PermitAnnotationInfo;
import com.yyp.permit.annotation.parser.PermitAnnotationParser;
import com.yyp.permit.context.PermitContext;
import com.yyp.permit.context.PermitInfo;
import com.yyp.permit.context.PermitManager;
import com.yyp.permit.context.PermitToken;
import com.yyp.permit.dept.room.SecurityDept;
import com.yyp.permit.exception.PermitException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.List;

public class PermissionInterceptor implements MethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger(PermissionInterceptor.class);

    private SecurityDept securityDept;

    private AnnotationParser annotationParser = new PermitAnnotationParser();

    public PermissionInterceptor(SecurityDept securityDept) {
        this.securityDept = securityDept;
    }

    public PermissionInterceptor() {
    }

    public void setSecurityDept(SecurityDept securityDept) {
        this.securityDept = securityDept;
    }

    public SecurityDept getSecurityDept() {
        return securityDept;
    }

    public AnnotationParser getAnnotationParser() {
        return annotationParser;
    }

    public void setAnnotationParser(AnnotationParser annotationParser) {
        this.annotationParser = annotationParser;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Class<?> targetClass = (methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis()) : null);
        Method specificMethod = ClassUtils.getMostSpecificMethod(methodInvocation.getMethod(), targetClass);
        final Method method = BridgeMethodResolver.findBridgedMethod(specificMethod);
        if (!annotationParser.isCandidateClass(targetClass))
            return methodInvocation.proceed();
        PermitInfo permitInfo = new PermitInfo();
        AnnotationInfoProvider<List<PermitAnnotationInfo>> annotationInfo = annotationParser.getAnnotationInfo(method, targetClass);
        permitInfo.setTargetClass(targetClass);
        permitInfo.setArguments(methodInvocation.getArguments());
        permitInfo.setTargetMethod(method);
        permitInfo.setTargetObj(methodInvocation.getThis());
        permitInfo.setAnnotationInfoList(annotationInfo.getAnnotationInfo());
        return doInvoke(permitInfo, method, methodInvocation);
    }

    protected Object doInvoke(PermitInfo permitInfo, Method method, MethodInvocation methodInvocation) throws Throwable {
        PermitToken permitToken = null;
        try {
            try {
                PermitContext permitContext = securityDept.register(permitInfo);
                permitToken = securityDept.securityVerify(permitContext);
                if (!permitToken.isVerify()) {
                    Assert.isTrue(!RejectStrategy.VIOLENCE.equals(permitToken.getRejectStrategy()), permitToken.getExplain());
                    return returnFail(method);
                }
            } catch (Exception e) {
                log.error("{}.{} security verify exception", permitInfo.getTargetClass().getName(), permitInfo.getTargetMethod().getName());
                throw new PermitException(e.getMessage());
            }
            return methodInvocation.proceed();
        } finally {
            if (permitToken != null)
                PermitManager.cancelPassCheck(permitToken);
            try {
                if (PermitManager.getPermitToken() == null)
                    PermitManager.clearRecycleBin();
            } catch (Exception e) {
                log.error("归档失败:", e);
            }
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
