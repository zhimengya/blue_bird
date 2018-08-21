package cn.kgc.tiku.bluebird.entity;

import android.graphics.Color;


public class LogListItem {
    public final static int ERROR = 1;
    public final static int SUCCESS = 2;
    public final static int WARNING = 3;
    private int level;
    private String message;
    private int color;

    public LogListItem(int level, String message) {
        this.setLevel(level);
        this.message = message;
    }

    public LogListItem(int level, String message, int color) {
        this(level, message);
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLevel() {
        switch (level) {
            case 1:
                return "错误";
            case 2:
                return "通知";
            case 3:
                return "警告";
        }
        return null;
    }

    public int getColor() {
        return color;
    }

    public void setLevel(int level) {
        this.level = level;
        switch (level) {
            case 1:
                color = Color.RED;
                break;
            case 2:
                color = Color.parseColor("#09BA07");
                break;
            case 3:
                color = Color.parseColor("#FCD113");
                break;
            default:
                color = Color.RED;
                break;
        }
    }
}
