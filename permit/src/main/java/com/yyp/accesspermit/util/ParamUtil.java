package com.yyp.accesspermit.util;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yyp
 * @description:
 * @date 2021/5/2713:52
 */
public class ParamUtil {

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
        return dataKey + "$$" + SecureUtil.md5(JSONArray.toJSONString(args));
    }

    private static Object findKey(String key, Object obj) {
        if (ObjectUtils.isArray(obj)) {
            JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(obj));
            for (int i = 0; i < jsonArray.size(); i++) {
                Object value = findKey(key, jsonArray.get(i));
                if (value != null)
                    return value;
            }
        } else {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(obj));
            if (jsonObject.containsKey(key)) {
                return jsonObject.get(key);
            } else {
                Set<Map.Entry<String, Object>> set = jsonObject.entrySet();
                for (Map.Entry next : set) {
                    try {
                        Object value = findKey(key, next.getValue());
                        if (value != null)
                            return value;
                    } catch (Exception e) {
                    }
                }
            }
        }
        return null;
    }
}
