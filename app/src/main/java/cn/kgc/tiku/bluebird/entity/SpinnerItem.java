package cn.kgc.tiku.bluebird.entity;

/**
 * Created by star on 2018/8/18.
 */

public class SpinnerItem {
    private String name;
    private String url;

    public SpinnerItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return name;
    }
}
