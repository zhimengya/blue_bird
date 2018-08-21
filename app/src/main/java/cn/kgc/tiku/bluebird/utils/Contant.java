package cn.kgc.tiku.bluebird.utils;


import java.util.List;

import cn.kgc.tiku.bluebird.entity.Config;
import cn.kgc.tiku.bluebird.entity.UserInfo;

public class Contant {
    public static final int VERSION = 1;
    public static final String TIKU_ERROR_TITLE = "题库错误";
    public static final String TIKU_INFO_TITLE = "题库提示";
    public static final String SYS_ERROR_TITLE = "系统错误";
    public static final String SYS_INFO_TITLE = "系统提示";
    public static String LOGIN_ACCOUNT = "";
    public static final String LOGIN_ERROR = "账号在其其它处登陆或者登陆已经失效。";
    public static int shuaTiMiaoShu = 20;
    public static int shuaTiCiShu = 10;
    public static int ZQL = 80;
    public static UserInfo userInfo;
    public static Config config;
    public static boolean isJiaQun = false;
    public static List<String> accountList;
}
