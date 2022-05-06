package com.yyp.permit.context;

import com.yyp.permit.dept.SecurityDept;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EasyGetPermitContext extends DefaultPermitContext {

    public EasyGetPermitContext(SecurityDept securityDept, PermitInfo permitInfo) {
        super(securityDept, permitInfo);
    }

    public <E> E getValidResultObjectIfOne(String permit) {
        List validResultObject = super.getValidResultObject(permit);
        Assert.isTrue(validResultObject.size() == 1, "validResultObject length gt 1");
        Map map = (Map) validResultObject.get(0);
        Assert.isTrue(map.size() == 1, "validResultObject length gt 1");
        Set<Map.Entry> set = map.entrySet();
        return (E) set.stream().findFirst().get().getValue();
    }

    public <E> E getValidResultObjectByKey(String permit, String key) {
        Assert.notNull(key, "key is null");
        List<Map> validResultObject = super.getValidResultObject(permit);
        Optional<Map> first = validResultObject.stream().filter(map -> map.containsKey(key)).findFirst();
        Map map = first.get();
        return (E) map.get(key);
    }


}
