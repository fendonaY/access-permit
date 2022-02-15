package com.yyp.permit.annotation.parser;

import com.yyp.permit.aspect.RejectStrategy;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class PermitAnnotationInfo {

    private String message;

    private int[] indexes;

    private String[] names;

    private boolean canEmpty;

    private RejectStrategy strategy;

    private String permit;

    private boolean validCache;

    private int minCacheTime;

    private int maxCacheTime;

    private TimeUnit timeUnit;

}
