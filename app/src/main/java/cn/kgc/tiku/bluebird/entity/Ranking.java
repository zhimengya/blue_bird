package cn.kgc.tiku.bluebird.entity;


public class Ranking {
    private int mc;
    private String xm;
    private int ljdt;
    private int sjdt;
    private double zql;

    public Ranking() {
    }

    public Ranking(int mc, String xm, int ljdt, int sjdt, double zql) {
        this.mc = mc;
        this.xm = xm;
        this.ljdt = ljdt;
        this.sjdt = sjdt;
        this.zql = zql;
    }

    public int getMc() {
        return mc;
    }

    public void setMc(int mc) {
        this.mc = mc;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public int getLjdt() {
        return ljdt;
    }

    public void setLjdt(int ljdt) {
        this.ljdt = ljdt;
    }

    public int getSjdt() {
        return sjdt;
    }

    public void setSjdt(int sjdt) {
        this.sjdt = sjdt;
    }

    public double getZql() {
        return zql;
    }

    public void setZql(double zql) {
        this.zql = zql;
    }
}
