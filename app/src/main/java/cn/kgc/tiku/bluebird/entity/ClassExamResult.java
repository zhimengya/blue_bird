package cn.kgc.tiku.bluebird.entity;

import cn.kgc.tiku.bluebird.entity.result.AbstractResult;

public class ClassExamResult extends AbstractResult {

    private UnifiedList unifiedPaper;
    private UnifiedResult unifiedResult;
    private String endTime;

    public void setUnifiedPaper(UnifiedList unifiedPaper) {
        this.unifiedPaper = unifiedPaper;
    }

    public UnifiedList getUnifiedPaper() {
        return unifiedPaper;
    }


    public void setUnifiedResult(UnifiedResult unifiedResult) {
        this.unifiedResult = unifiedResult;
    }

    public UnifiedResult getUnifiedResult() {
        return unifiedResult;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }


}