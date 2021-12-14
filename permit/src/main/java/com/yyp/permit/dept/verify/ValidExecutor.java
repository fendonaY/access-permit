package com.yyp.permit.dept.verify;

import java.util.List;
import java.util.Map;

public interface ValidExecutor {

    /**
     * 执行校验
     *
     * @param verifyExecutorHandle 校验前置处理
     * @return 存在-1,0,?>0三种情况， 如果为-1则根据{@link ValidExecutor#getResult()}是否为空作为依据，0或者大于0，则当前返回值作为判断依据。
     * 0校验不通过，大于0校验通过
     */
    int execute(VerifyExecutorHandle verifyExecutorHandle);

    /**
     * 获取返回值
     * 可以为null或者空，如果不需要结果
     * <p>
     * 比如是count校验则不需要，因为count校验往往判断是否存在.
     * 比如是select校验则需要，因为可能判断某条数据状态合法并且需要获取结果用于后置的业务处理
     * </p>
     *
     * @return 校验结果
     * @see com.yyp.permit.dept.verify.repository.VerifyRepository#getPermission(String)
     */
    List<Map<String, Object>> getResult();
}
