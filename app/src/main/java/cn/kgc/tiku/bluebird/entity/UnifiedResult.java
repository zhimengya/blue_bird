package cn.kgc.tiku.bluebird.entity;

public class UnifiedResult {

    private int choiceScore;
    private double examScore;
    private long examBeginTime;
    private long examResultId;
    private String subjectiveScore;

    public void setChoiceScore(int choiceScore) {
        this.choiceScore = choiceScore;
    }

    public int getChoiceScore() {
        return choiceScore;
    }

    public void setExamScore(double examScore) {
        this.examScore = examScore;
    }

    public double getExamScore() {
        return examScore;
    }

    public void setExamBeginTime(long examBeginTime) {
        this.examBeginTime = examBeginTime;
    }

    public long getExamBeginTime() {
        return examBeginTime;
    }

    public void setExamResultId(long examResultId) {
        this.examResultId = examResultId;
    }

    public long getExamResultId() {
        return examResultId;
    }

    public void setSubjectiveScore(String subjectiveScore) {
        this.subjectiveScore = subjectiveScore;
    }

    public String getSubjectiveScore() {
        return subjectiveScore;
    }

}