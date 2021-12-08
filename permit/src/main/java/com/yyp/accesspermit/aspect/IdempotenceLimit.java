package com.yyp.accesspermit.aspect;

import com.yyp.accesspermit.annotation.ApiIdempotence;
import com.yyp.accesspermit.util.ParamUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yyp
 * @description:
 * @date 2021/5/2710:17
 */
@EnableAspectJAutoProxy
@Aspect
@Component
@Slf4j
public class IdempotenceLimit {

    @Resource
    @Lazy
    private RedissonClient redissonClient;

    private final String IDEMPOTENCE_KEY = "idempotence@";

    @Pointcut("@annotation(com.yyp.accesspermit.annotation.ApiIdempotence)")
    public void idempotence() {
    }

    @Around("idempotence()")
    public Object aroundLog(JoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        ApiIdempotence annotation = method.getAnnotation(ApiIdempotence.class);
        long time = annotation.time();
        RLock lock = getLock(annotation, point);
        if (!lock.isLocked()) {
            if (time > 0 ? lock.tryLock(0, time, annotation.unit()) : lock.tryLock()) {
                try {
                    return ((ProceedingJoinPoint) point).proceed();
                } finally {
                    lock.unlock();
                }
            } else {
                return returnObject(annotation, method, lock);
            }
        } else
            return returnObject(annotation, method, lock);
    }

    private RLock getLock(ApiIdempotence annotation, JoinPoint point) {
        RLock lock;
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        int[] indexes = annotation.indexes();
        String[] names = annotation.names();
        if (StringUtils.hasText(annotation.lock()))
            lock = redissonClient.getLock(IDEMPOTENCE_KEY + annotation.lock());
        else if (indexes.length != 0 || names.length != 0) {
            List<Object> params = new ArrayList<>();
            Arrays.stream(indexes).forEach(index -> params.add(ParamUtil.getAttr(index, "", point.getArgs())));
            params.addAll(Arrays.stream(names).map(name -> {
                String[] split = name.split("\\.");
                Object value;
                if (split.length > 1) {
                    value = ParamUtil.getInlayAttr(split, split[0], ParamUtil.getNextAttr(split, split[0]), point.getArgs());
                } else {
                    value = ParamUtil.getAttrList(name, point.getArgs());
                }
                return value;
            }).collect(Collectors.toList()));
            List<Object> collect = params.stream().filter(param -> param != null).collect(Collectors.toList());
            lock = redissonClient.getLock(IDEMPOTENCE_KEY + ParamUtil.getKeyMD5(method.getName(), collect.isEmpty() ? point.getArgs() : collect.toArray()));
        } else
            lock = redissonClient.getLock(IDEMPOTENCE_KEY + ParamUtil.getKeyMD5(method.getName(), point.getArgs()));
        return lock;
    }

    @SneakyThrows
    private Object returnObject(ApiIdempotence annotation, Method method, RLock lock) {
        String msg = annotation.message();
        if (annotation.schedule() && annotation.time() > 0) {
            DecimalFormat df = new DecimalFormat("0%");
            String format = df.format(new BigDecimal(annotation.time() - lock.remainTimeToLive()).divide(new BigDecimal(annotation.time()), 2, RoundingMode.HALF_UP));
            msg = msg.concat(" 进度为：" + format);
        }
        Assert.isTrue(!annotation.reject().equals(RejectStrategy.VIOLENCE), msg);
        Object o = method.getReturnType().newInstance();
        Field msgF = ReflectionUtils.findField(o.getClass(), "msg", String.class);
        if (msgF != null) {
            boolean accessible = msgF.isAccessible();
            msgF.setAccessible(true);
            ReflectionUtils.setField(msgF, o, msg);
            msgF.setAccessible(accessible);
            return o;
        }
        return msg;
    }
}
