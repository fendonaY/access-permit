package com.yyp.permit.support.verify;

import com.yyp.permit.support.VerifyReport;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionalReport extends VerifyReport {
    private List<Map<String, Object>> result = new ArrayList();

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    public static FunctionalReport getFunctionalReport(VerifyReport verifyReport) {
        FunctionalReport functionalReport = new FunctionalReport();
        BeanUtils.copyProperties(verifyReport, functionalReport);
        return functionalReport;
    }
}
