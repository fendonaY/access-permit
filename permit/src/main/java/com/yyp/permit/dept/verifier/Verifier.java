package com.yyp.permit.dept.verifier;

import com.yyp.permit.context.PermitContext;
import com.yyp.permit.context.PermitInfo;
import com.yyp.permit.dept.room.VerifyReport;

public interface Verifier {

    /**
     * 准备验证
     */
    void prepareVerify(PermitContext permitContext, String permit);

    /**
     * 验证
     */
    boolean verify(VerifyReport verifyReport, PermitInfo permitInfo, String permit);

    /**
     * 验证完成
     */
    void finishVerify(PermitContext permitContext, String permit);

}
