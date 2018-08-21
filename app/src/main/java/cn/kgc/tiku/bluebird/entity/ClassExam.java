package cn.kgc.tiku.bluebird.entity;

import java.util.List;

import cn.kgc.tiku.bluebird.entity.result.AbstractResult;

public class ClassExam extends AbstractResult{

    private List<UnifiedList> unifiedList;
    private Pagination pagination;

    public void setUnifiedList(List<UnifiedList> unifiedList) {
        this.unifiedList = unifiedList;
    }

    public List<UnifiedList> getUnifiedList() {
        return unifiedList;
    }


    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Pagination getPagination() {
        return pagination;
    }


}