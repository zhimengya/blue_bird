package cn.kgc.tiku.bluebird.entity;

import com.alibaba.fastjson.JSON;

public class ClassExamPage extends ExamPage {

    private long countDown;
    private String cqList;
    private String sqList;


    public void setCountDown(long countDown) {
        this.countDown = countDown;
    }

    public long getCountDown() {
        return countDown;
    }

    public void setCqList(String cqList) {
        this.cqList = cqList;
        if (cqList != null & cqList.trim().length() != 0) {
            this.setTopics(JSON.parseArray(cqList, Topic.class));
        }
    }

    public String getCqList() {
        return cqList;
    }


    public void setSqList(String sqList) {
        this.sqList = sqList;
    }

    public String getSqList() {
        return sqList;
    }


}