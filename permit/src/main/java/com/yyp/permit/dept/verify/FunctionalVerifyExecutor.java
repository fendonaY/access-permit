package com.yyp.permit.dept.verify;

import com.yyp.permit.dept.room.VerifyReport;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yyp
 * @description:
 * @date 2021/4/713:48
 */
public class FunctionalVerifyExecutor implements ValidExecutor {

    private List<Map<String, Object>> result = new ArrayList();

    private VerifyReport verifyReport;

    private FunctionalVerify functionalVerify;

    public FunctionalVerifyExecutor(VerifyReport verifyReport, FunctionalVerify functionalVerify) {
        this.verifyReport = verifyReport;
        this.functionalVerify = functionalVerify;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    public VerifyReport getVerifyReport() {
        return verifyReport;
    }

    public void setVerifyReport(VerifyReport verifyReport) {
        this.verifyReport = verifyReport;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    @Override
    public int execute(VerifyExecutorHandle verifyExecutorHandle) {
        Assert.notNull(verifyReport, "an empty report cannot be processed");
        return ((FunctionalVerify) verifyReport -> {
            int verify = functionalVerify.verify(verifyReport);
            setResult(verifyReport.getResult());
            return verify;
        }).verify(FunctionalReport.getFunctionalReport(verifyReport));
    }
}
