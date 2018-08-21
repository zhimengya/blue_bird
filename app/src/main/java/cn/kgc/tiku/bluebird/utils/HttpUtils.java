package cn.kgc.tiku.bluebird.utils;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.kgc.tiku.bluebird.entity.ProductList;
import cn.kgc.tiku.bluebird.entity.UserInfo;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public final class HttpUtils {
    private final static OkHttpClient HTTP_CLIENT;

    static {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.cookieJar(new CookieJar() {
            private final Map<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        });
        HTTP_CLIENT = builder.connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
                .proxy(Proxy.NO_PROXY).build();
    }

    public static void get(String url, Callback callback) {
        HTTP_CLIENT.newCall(createReqeust(url)).enqueue(callback);
    }

    public static void post(String url, Map map, Callback callback) {
        HTTP_CLIENT.newCall(createReqeust(url, map)).enqueue(callback);
    }

    public static String get(String url) throws IOException {
        Response response = HTTP_CLIENT.newCall(createReqeust(url)).execute();
        return response.body().string();
    }

    public static String post(String url, Map map) throws IOException {
        Response response = HTTP_CLIENT.newCall(createReqeust(url, map)).execute();
        return response.body().string();
    }


    private static Request createReqeust(String url, Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null && !map.isEmpty())
            for (String key : map.keySet()) {
                builder.add(key, map.get(key));
            }
        return new Request.Builder().url(url).addHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 7.1.2; zh-cn; vivo Y79A " +
                "Build/N2G47H) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1").post(builder.build())
                .build();
    }

    private static Request createReqeust(String url) {
        return new Request.Builder().url(url).addHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android 7.1.2; zh-cn; vivo Y79A " +
                "Build/N2G47H) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
                .build();
    }

    //第一次验证登录
//    public static UserInfo verificationLogin(String userName, String password,Callback callback) throws IOException {
//        try {
//            HTTP_CLIENT.newCall(createReqeust(getVerificationUrl(userName, password))).enqueue(callback);
//        } catch (IOException e) {
//            throw new IOException("验证登陆失败");
//        }
//    }


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
        parameterUtils.addParam("_yl005_", parameterUtils.getAuthCnParam());
        return parameterUtils.getQuestionUrl(UrlContant.LOGIN_SYS);
    }

    private static class ParameterUtils {
        private ArrayList<Parameter> mParameters;

        public String getAuthCnParam() {
            Collections.sort(this.mParameters, new SortByKey());
            String auth = addslashes() + "cn_bdqn";
            try {
                auth = Md5Utils.md5Encode(auth);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return auth;
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

        public String getQuestionUrl(String pathUrl) {
            Collections.sort(this.mParameters, new SortByKey());
            String url = addslashes();
            pathUrl = pathUrl + "?" + url;
            return pathUrl;
        }

        public void addParam(String key, String value) {
            Parameter param = new Parameter(key, value);
            if (this.mParameters == null) {
                this.mParameters = new ArrayList<Parameter>();
            }
            this.mParameters.add(param);
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
            Parameter s1 = (Parameter) o1;
            Parameter s2 = (Parameter) o2;
            if (s1.getKey().compareTo(s2.getKey()) < 0) {
                return -1;
            }
            if (s1.getKey().compareTo(s2.getKey()) > 0) {
                return 1;
            }
            return 0;
        }
    }

    public static String getVerificationUrl(String username, String password) {
        String auth = null;
        try {
            auth = addslashes(username, password);
        } catch (Exception e) {
            throw new RuntimeException("构建登录URL出错");
        }
        String url = "http://a.bdqn.cn/pb//pbsub/web/login/user_login.action?clientType=009&encrypt=" + auth + "&passport="
                + username + "&password=" + Md5Utils.encode(password);
        return url;
    }

    private static String addslashes(String username, String pwd) throws Exception {
        return Md5Utils.encode("passport=" + username + "&password=" + Md5Utils.md5Encode(pwd) + "cn_bdqn");
    }

    public static String getSwitchProductUrl(UserInfo userInfo) {
        for (ProductList productList : userInfo.getProductList()) {
            if (productList.getIsLastLoginProduct()) {
                System.out.println(productList.getProductName());
                System.out.println(UrlContant.ROOT_URL + UrlContant.SWITCH_PRODUCT + "?clientType=009&productId=" + productList.getProductId() +
                        "&userId=" + userInfo.getUserId());
                return UrlContant.ROOT_URL + UrlContant.SWITCH_PRODUCT + "?clientType=009&productId=" + productList.getProductId() +
                        "&userId=" + userInfo.getUserId();
            }
        }
        return UrlContant.ROOT_URL + UrlContant.SWITCH_PRODUCT + "?clientType=009&productId=" + userInfo.getProductList().get(0).getProductId() +
                "&userId=" + userInfo.getUserId();
    }
}
