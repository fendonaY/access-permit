//package com.yyp.accesspermit.aspect;
//
//import com.aixiao.msj.account.apis.valid.annotation.IdPermissionValid;
//import com.aixiao.msj.account.config.exception.SystemErrorType;
//import com.aixiao.msj.account.tools.ExceptionUtil;
//import org.aopalliance.intercept.MethodInterceptor;
//import org.aopalliance.intercept.MethodInvocation;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.aop.support.AopUtils;
//import org.springframework.core.BridgeMethodResolver;
//import org.springframework.core.annotation.MergedAnnotation;
//import org.springframework.util.ClassUtils;
//
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.stream.Collectors;
//
///**
// * @author yyp
// * @description:
// * @date 2021/4/618:06
// */
//public class ParamPermissionInterceptor implements MethodInterceptor {
//
//    private static Logger logger = LoggerFactory.getLogger(ParamPermissionInterceptor.class);
//
//    private Permission permission;
//
//    public ParamPermissionInterceptor(Permission permission) {
//        this.permission = permission;
//    }
//
//    @Override
//    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
//        Class<?> targetClass = (methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis()) : null);
//        Method specificMethod = ClassUtils.getMostSpecificMethod(methodInvocation.getMethod(), targetClass);
//        final Method method = BridgeMethodResolver.findBridgedMethod(specificMethod);
//        MergedAnnotation valid = AnnotationUtil.getAnnotation(method, IdPermissionValid.class);
//        valid = valid == MergedAnnotation.missing() ? AnnotationUtil.getAnnotation(targetClass, IdPermissionValid.class) : valid;
//        MergedAnnotation validList = AnnotationUtil.getAnnotation(method, IdPermissionValid.List.class);
//        MergedAnnotation[] value = new MergedAnnotation[0];
//        if (validList != MergedAnnotation.missing()) {
//            value = validList.getAnnotationArray("value", IdPermissionValid.class);
//        } else if (valid != MergedAnnotation.missing()) {
//            value = new MergedAnnotation[1];
//            value[0] = valid;
//        }
//        for (MergedAnnotation mergedAnnotation : value) {
//            if (!permission.valid(mergedAnnotation, methodInvocation)) {
//                PermissionPermit permit = (PermissionPermit) mergedAnnotation.getEnum("permit", PermissionPermit.class);
//                logger.warn("ID permission validation failed:permit:{} method:{}.{} arguments:{}", permit.getCode(),
//                        targetClass.getName(), specificMethod.getName(), Arrays.stream(specificMethod.getParameterTypes()).map(clazz -> clazz.getName()).collect(Collectors.joining(",")));
//                RejectStrategy rejectStrategy = (RejectStrategy) valid.getEnum("strategy", RejectStrategy.class);
//                ExceptionUtil.isTrue(RejectStrategy.VIOLENCE.equals(rejectStrategy), SystemErrorType.PERMIT_ERROR.setMsg(permit.toString()));
//                return returnFail(method);
//            }
//        }
//        return methodInvocation.proceed();
//    }
//
//    private Object returnFail(Method method) {
//        Class clazz = method.getReturnType();
//        if (clazz.isPrimitive()) {
//            if (clazz == int.class) {
//                return Integer.valueOf(0);
//            } else if (clazz == long.class) {
//                return Long.valueOf(0);
//            } else if (clazz == short.class) {
//                return Short.valueOf((short) 0);
//            } else if (clazz == byte.class) {
//                return Byte.valueOf((byte) 0);
//            } else if (clazz == float.class) {
//                return Float.valueOf(0);
//            } else if (clazz == double.class) {
//                return Double.valueOf(0);
//            } else if (clazz == boolean.class) {
//                return Boolean.FALSE;
//            }
//            return null;
//        } else
//            return null;
//    }
//
//}
