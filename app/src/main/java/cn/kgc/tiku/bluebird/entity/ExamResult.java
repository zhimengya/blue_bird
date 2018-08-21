package cn.kgc.tiku.bluebird.entity;


import cn.kgc.tiku.bluebird.entity.result.AbstractResult;

public class ExamResult extends AbstractResult {

    private Paper paper;
    private ExamReport examReport;

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setExamReport(ExamReport examReport) {
        this.examReport = examReport;
    }

    public ExamReport getExamReport() {
        return examReport;
    }


}