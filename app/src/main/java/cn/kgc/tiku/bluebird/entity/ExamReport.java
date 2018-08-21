package cn.kgc.tiku.bluebird.entity;

public class ExamReport {

	private int totalScore;
	private String choiceScore;
	private double totalCount;
	private String subjectiveScore;
	private long examSubmitTime;
	private int questionCount;
	private int correctCount;
	private int finishedQuestionCount;

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setChoiceScore(String choiceScore) {
		this.choiceScore = choiceScore;
	}

	public String getChoiceScore() {
		return choiceScore;
	}

	public void setTotalCount(double totalCount) {
		this.totalCount = totalCount;
	}

	public double getTotalCount() {
		return totalCount;
	}

	public void setSubjectiveScore(String subjectiveScore) {
		this.subjectiveScore = subjectiveScore;
	}

	public String getSubjectiveScore() {
		return subjectiveScore;
	}

	public void setExamSubmitTime(long examSubmitTime) {
		this.examSubmitTime = examSubmitTime;
	}

	public long getExamSubmitTime() {
		return examSubmitTime;
	}

	public void setQuestionCount(int questionCount) {
		this.questionCount = questionCount;
	}

	public int getQuestionCount() {
		return questionCount;
	}

	public void setCorrectCount(int correctCount) {
		this.correctCount = correctCount;
	}

	public int getCorrectCount() {
		return correctCount;
	}

	public void setFinishedQuestionCount(int finishedQuestionCount) {
		this.finishedQuestionCount = finishedQuestionCount;
	}

	public int getFinishedQuestionCount() {
		return finishedQuestionCount;
	}

}