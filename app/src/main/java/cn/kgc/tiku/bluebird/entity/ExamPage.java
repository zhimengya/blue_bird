package cn.kgc.tiku.bluebird.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kgc.tiku.bluebird.entity.result.AbstractResult;
import cn.kgc.tiku.bluebird.utils.Contant;

public class ExamPage extends AbstractResult {

    private Paper paper;
    private long examResultId;
    private String qcList;
    private List<Topic> topics;

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setExamResultId(long examResultId) {
        this.examResultId = examResultId;
    }

    public long getExamResultId() {
        return examResultId;
    }


    public void setQcList(String qcList) {
        this.qcList = qcList;
        setTopics(JSON.parseArray(qcList, Topic.class));
    }

    public String getQcList() {
        return qcList;
    }

    public Map<String, String> submitParams(List<Topic> answers, int zql) {

        Map<Integer, String> answerMap = new HashMap<Integer, String>();
        if (answers == null) return null;
        for (Topic topic : answers) {
            answerMap.put(topic.getId(), topic.getAnswers());
        }

        JSONObject root = new JSONObject();
        int count = 0;
        double zql2 = (topics.size() * (zql / 100.00));
        System.out.println(zql2);
        for (int i = 0; i < topics.size(); i++) {
            Topic topic = topics.get(i);
            JSONObject subJson = new JSONObject();
            subJson.put("position", count++);
            subJson.put("psqId", topic.getPsqId());
            subJson.put("questionId", topic.getId());
            subJson.put("time", Contant.shuaTiMiaoShu);
            if (i < zql2) {
                subJson.put("uAnswer", answerMap.get(topic.getId()));
            } else {
                subJson.put("uAnswer", "1,2,3,4,5");
            }
            root.put(String.valueOf(topic.getId()), subJson);
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("paperId", String.valueOf(paper.getId()));
        map.put("examResultId", String.valueOf(examResultId));
        map.put("json", root.toJSONString());
        return map;
    }
}