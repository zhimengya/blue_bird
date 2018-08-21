package cn.kgc.tiku.bluebird.entity;

public class Topic {

    private String answers;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private boolean collection;
    private int id;
    private String picurl;
    private long psqId;
    private int questionIndex;
    private int questionScore;
    private String title;
    private int type;

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getAnswers() {
        return answers;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    public boolean getCollection() {
        return collection;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPsqId(long psqId) {
        this.psqId = psqId;
    }

    public long getPsqId() {
        return psqId;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionScore(int questionScore) {
        this.questionScore = questionScore;
    }

    public int getQuestionScore() {
        return questionScore;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}