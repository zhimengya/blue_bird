package cn.kgc.tiku.bluebird.entity;

import java.util.List;

public class Pagination {

    private String url;
    private int totalCount;
    private int startItemNum;
    private int pageSize;
    private List<String> list;
    private int totalPage;
    private int currPage;
    private int prePage;
    private int nextPage;
    private int endItemNum;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setStartItemNum(int startItemNum) {
        this.startItemNum = startItemNum;
    }

    public int getStartItemNum() {
        return startItemNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setEndItemNum(int endItemNum) {
        this.endItemNum = endItemNum;
    }

    public int getEndItemNum() {
        return endItemNum;
    }

}