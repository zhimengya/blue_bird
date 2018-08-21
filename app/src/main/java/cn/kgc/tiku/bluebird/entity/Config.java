package cn.kgc.tiku.bluebird.entity;


public class Config {
    private int version;
    private int status;
    private String qqGroupApi;
    private String qqNumber;
    private String qqGroupNumber;
    private String alipay;
    private String msg;

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setQqGroupApi(String qqGroupApi) {
        this.qqGroupApi = qqGroupApi;
    }

    public String getQqGroupApi() {
        return qqGroupApi;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqGroupNumber(String qqGroupNumber) {
        this.qqGroupNumber = qqGroupNumber;
    }

    public String getQqGroupNumber() {
        return qqGroupNumber;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getAlipay() {
        return alipay;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
