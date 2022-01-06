package com.yyp.permit.util;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yyp
 * @description:
 * @date 2021/5/2713:52
 */
public class ParamUtil {
    private static final Logger log = LoggerFactory.getLogger(ParamUtil.class);

    private ParamUtil() {

    }


    /**
     * @param attrs     连级属性数组
     * @param attr      当前属性
     * @param nextAttr  下一个属性
     * @param arguments 数据
     * @return java.lang.Object
     * @throws
     * @Description: 递归获取连级属性
     * @author yyp
     * @date 2021/4/22 11:37
     */
    public static Object getInlayAttr(String[] attrs, String attr, String nextAttr, Object[] arguments) {
        if (!StringUtils.hasText(attr))
            return arguments[0];
        Object attrValue = getAttr(-1, attr, arguments);

        if (attrValue instanceof List) {
            return ((List<?>) attrValue).stream().map(o -> getInlayAttr(attrs, nextAttr, getNextAttr(attrs, nextAttr), Arrays.asList(o).toArray())
            ).collect(Collectors.toList());
        }
        return getInlayAttr(attrs, nextAttr, getNextAttr(attrs, nextAttr), Arrays.asList(attrValue).toArray());
    }

    public static String getNextAttr(String[] attrs, String attr) {
        for (int i = 0; i < attrs.length; i++) {
            if (attr.equals(attrs[i]) && i < attrs.length - 1) {
                return attrs[i + 1];
            }
        }
        return "";
    }

    public static Object getAttrList(String attr, Object[] arguments) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < arguments.length; i++) {
            Object arg = arguments[i];
            try {
                if (arg instanceof List) {
                    list.addAll(((List<?>) arg).stream().map(o -> findKey(attr, o)).collect(Collectors.toList()));
                    continue;
                }
                Object findKey = findKey(attr, arg);
                if (findKey != null)
                    return findKey;
            } catch (Exception e) {
            }
        }
        list = list.stream().filter(o -> o != null).collect(Collectors.toList());
        return list.isEmpty() ? null : list;
    }

    public static Object getAttr(int index, String attr, Object[] arguments) {
        if (index >= 0) {
            return arguments[index];
        }
        for (int i = 0; i < arguments.length; i++) {
            Object arg = arguments[i];
            try {
                Object findKey = findKey(attr, arg);
                if (findKey != null)
                    return findKey;
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static String getKeyMD5(String dataKey, Object[] args) {
        return dataKey + "$$" + SecureUtil.md5(args.toString());
    }

    private static Object findKey(String key, Object obj) {
        if (ObjectUtils.isArray(obj) || obj instanceof Collection) {
            JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(obj));
            for (int i = 0; i < jsonArray.size(); i++) {
                Object value = findKey(key, jsonArray.get(i));
                if (value != null)
                    return value;
            }
        } else if (obj instanceof Map) {
            Map<String, Object> obj1 = (Map<String, Object>) obj;
            if (obj1.containsKey(key)) {
                return obj1.get(key);
            } else {
                Set<Map.Entry<String, Object>> set = obj1.entrySet();
                for (Map.Entry next : set) {
                    try {
                        Object value = findKey(key, next.getValue());
                        if (value != null)
                            return value;
                    } catch (Exception e) {
                        log.warn("get {} filed:{} error", obj.getClass().getSimpleName(), next.getKey());
                    }
                }
            }
        } else {
            Field[] fields = ReflectUtil.getFields(obj.getClass());
            Optional<Field> first = Arrays.stream(fields).filter(field -> field.getName().equals(key)).findFirst();
            if (first.isPresent()) {
                Field field = first.get();
                boolean accessible = field.isAccessible();
                Object o = null;
                try {
                    field.setAccessible(true);
                    o = field.get(obj);
                } catch (IllegalAccessException e) {
                    log.warn("get {} filed:{} error", obj.getClass().getSimpleName(), field.getName());
                }
                field.setAccessible(accessible);
                return o;
            } else {
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    try {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        Object o = field.get(obj);
                        field.setAccessible(accessible);
                        Object value = findKey(key, o);
                        if (value != null)
                            return value;
                    } catch (Exception e) {
                        log.warn("get {} filed:{} error", obj.getClass().getSimpleName(), field.getName());
                    }
                }
            }
        }
        return null;
    }
}
