package com.kgc.tiku.bluebird;

import com.kgc.tiku.bluebird.utils.UrlConstant;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LoginTest {
    @Test
    public void test(){
        String loginUrl = UrlConstant.Companion.getOneLoginUrl("763860000@qq.com", "123456");
        System.out.println(loginUrl);
    }

    public static String getLoginUrl(String username, String password) {
        String passUserPassword = "";
        ParameterUtils parameterUtils = new ParameterUtils();
        try {
            passUserPassword = Md5Utils.md5Encode(password);
        } catch (Exception e) {
            throw new RuntimeException("构建登录URL出错");
        }
        parameterUtils.addParam("passport", username);
        parameterUtils.addParam("password", passUserPassword);
        parameterUtils.addParam("clientType", "009");
        parameterUtils.addParam("version", "Version_1.1.2");
        parameterUtils.addParam("_yl005_", parameterUtils.getAuthCnParam(false));
        return parameterUtils.getQuestionUrl("http://tiku.kgc.cn/testing/exam/app/login");
    }


    private static class ParameterUtils {
        public static final String TAG = "ParameterUtils";
        private ArrayList<Parameter> mParameters;


        public void addParam(String key, String value) {
            Parameter param = new Parameter(key, value);
            if (this.mParameters == null) {
                this.mParameters = new ArrayList();
            }
            this.mParameters.add(param);
        }

        public String getAuthCnParam(boolean isExam) {
            String auth;
            Collections.sort(this.mParameters, new SortByKey());
            String url = addslashes();
            if (isExam) {
                auth = url + "cn_bdqn";
            } else {
                auth = url + "cn__kgc";
            }
            try {
                auth = URLDecoder.decode(auth, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                auth = Md5Utils.md5Encode(auth);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return auth;
        }


        public String getQuestionUrl(String pathUrl) {
            if (this.mParameters != null) {
                Collections.sort(this.mParameters, new SortByKey());
                String url = addslashes();
                if (url!=null&&url.length()!=0) {
                    pathUrl = pathUrl + "?" + url;
                }
                return pathUrl;
            }
            return pathUrl;
        }

        public String addslashes() {
            String url = "";
            for (int i = 0; i < this.mParameters.size(); i++) {
                Parameter parameter = (Parameter) this.mParameters.get(i);
                if (parameter != null) {
                    url = url + parameter.getKey() + "=" + parameter.getValue() + "&";
                }
            }
            return url.substring(0, url.length() - 1);
        }
    }

    private static class Parameter {
        String key;
        String value;

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Parameter(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class SortByKey implements Comparator<Object> {
        public SortByKey() {
        }

        public int compare(Object o1, Object o2) {

            return 0;
        }
    }

}
