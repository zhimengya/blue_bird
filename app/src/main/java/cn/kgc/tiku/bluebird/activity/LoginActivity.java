package cn.kgc.tiku.bluebird.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import cn.kgc.tiku.bluebird.R;
import cn.kgc.tiku.bluebird.entity.Config;
import cn.kgc.tiku.bluebird.entity.UserInfo;
import cn.kgc.tiku.bluebird.entity.result.AbstractResult;
import cn.kgc.tiku.bluebird.entity.result.BasicResult;
import cn.kgc.tiku.bluebird.utils.ActivityUtils;
import cn.kgc.tiku.bluebird.utils.Contant;
import cn.kgc.tiku.bluebird.utils.HttpUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity111 extends AppCompatActivity {
    private EditText txtUserName;
    private EditText txtPassword;
    private Button btnLogin;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityUtils.setSharedPreferences(getSharedPreferences("info", MODE_PRIVATE));
        initView();
        initData();
        initEvent();
        initConfig();
    }

    private void initView() {
        txtUserName = (EditText) findViewById(R.id.txtUserName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }

    private void initData() {
        txtPassword.setText(ActivityUtils.getSaveValue("password", ""));
        txtUserName.setText(ActivityUtils.getSaveValue("userName", ""));

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    //版本更新
                    case 0x1:
                        ActivityUtils.dismissProgressDialog();
                        if (ActivityUtils.checkApkExist(LoginActivity.this,
                                "com.tencent.mobileqq")) {
                            ActivityUtils.showAlert(LoginActivity.this, Contant.SYS_INFO_TITLE, "版本更新了，点击确定加群下载最新版。", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                            .parse(Contant.config.getQqGroupApi())));
                                }
                            });
                        } else {
                            ActivityUtils.showAlertMainThread(LoginActivity.this, Contant.SYS_INFO_TITLE, "版本更新了，请加入QQ群下载最新版，QQ群：" + Contant.config.getQqGroupNumber() + "。", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            });
                        }
                        break;
                    //状态不等于1
                    case 0x2:
                        ActivityUtils.dismissProgressDialog();
                        ActivityUtils.showAlertMainThread(LoginActivity.this, Contant.SYS_INFO_TITLE, Contant.config.getMsg(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        });
                        break;
                    //关闭加载框
                    case 0x3:
                        ActivityUtils.dismissProgressDialog();
                        break;
                }
            }
        };
    }

    private void initEvent() {

        ActivityUtils.setEditTextInhibitInputSpace(this.txtPassword);

        ActivityUtils.setEditTextInhibitInputSpace(this.txtUserName);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //校验用户密码是否输入
                final String userName = txtUserName.getText().toString();
                final String password = txtPassword.getText().toString();
                if (userName.length() == 0) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(Contant.SYS_ERROR_TITLE)
                            .setMessage("请输入青鸟网账号")
                            .setPositiveButton("确定", null)
                            .create().show();
                    txtUserName.requestFocus();
                    return;
                } else if (password.length() == 0) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(Contant.SYS_ERROR_TITLE)
                            .setMessage("请输入青鸟网密码")
                            .setPositiveButton("确定", null)
                            .create().show();
                    txtPassword.requestFocus();
                    return;
                }


                ActivityUtils.showProgressDialog(LoginActivity.this, "登陆中，请稍后...");

                //登录
                HttpUtils.post(HttpUtils.getVerificationUrl(userName, password), null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.sendEmptyMessage(0x3);
                        ActivityUtils.showAlert(LoginActivity.this, Contant.SYS_ERROR_TITLE, "登陆失败，请重新登陆。", null);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final UserInfo userInfo = JSON.parseObject(response.body().string(), UserInfo.class);
                        if (userInfo.getCode() != 1) {
                            ActivityUtils.dismissProgressDialog();
                            ActivityUtils.showAlert(LoginActivity.this, Contant.TIKU_ERROR_TITLE, userInfo.getMsg(), null);
                            return;
                        }

                        try {
                            //如果第一次校验成功,然后真实登录
                            String html = HttpUtils.get(HttpUtils.getLoginUrl(userName, password));
                            AbstractResult result = JSON.parseObject(html, BasicResult.class);
                            if (result.getCode() != 1) {
                                ActivityUtils.dismissProgressDialog();
                                ActivityUtils.showAlert(LoginActivity.this, Contant.TIKU_ERROR_TITLE, result.getMsg(), null);
                                return;
                            }

                            //然后切换产品
                            //System.out.println(HttpUtils.getSwitchProductUrl(userInfo));
                            html = HttpUtils.post(HttpUtils.getSwitchProductUrl(userInfo), null);
                            result = JSON.parseObject(html, BasicResult.class);
                            if (result.getCode() != 1) {
                                ActivityUtils.dismissProgressDialog();
                                ActivityUtils.showAlert(LoginActivity.this, Contant.TIKU_ERROR_TITLE, result.getMsg(), null);
                                return;
                            }
                            ActivityUtils.saveValue("password", password);
                            ActivityUtils.saveValue("userName", userName);
                            Contant.LOGIN_ACCOUNT = userName;
                            Contant.userInfo = userInfo;
                            ActivityUtils.dismissProgressDialog();
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            ActivityUtils.showAlert(LoginActivity.this, Contant.SYS_ERROR_TITLE, userInfo.getMsg(), null);
                        }
                    }
                });
            }
        });
    }

    private void initConfig() {
        ActivityUtils.showProgressDialog(this, "正在获取配置，请稍后...");
        HttpUtils.get("https://raw.githubusercontent.com/zzneof/topic/master/blue_bird/config/blue_bird_config.json", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Contant.config = new Config();
                Contant.config.setMsg("获取配置失败，请重新启动本程序。");
                handler.sendEmptyMessage(0x2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                Config config = JSON.parseObject(html, Config.class);
                Contant.config = config;

                try {
                    String h = HttpUtils.get("https://raw.githubusercontent.com/zzneof/topic/master/blue_bird/config/blue_bird_account.json");
                    Contant.accountList = JSON.parseArray(h, String.class);
                } catch (IOException e) {
                    Contant.config = new Config();
                    Contant.config.setMsg("获取配置失败，请重新启动本程序。");
                    handler.sendEmptyMessage(0x2);
                    return;
                }

                if(config.getAlipay()!=null||!config.getAlipay().trim().equals("")){
                    if (ActivityUtils.checkApkExist(getApplicationContext(), "com.eg.android.AlipayGphone")) {
                        //这个无法更换，因为已启动就要启动支付宝
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(config.getAlipay())));
                    }
                }

                if (config.getVersion() != Contant.VERSION) {
                    handler.sendEmptyMessage(0x1);
                } else if (config.getStatus() != 1) {
                    handler.sendEmptyMessage(0x2);
                } else {
                    handler.sendEmptyMessage(0x3);
                }
            }
        });

    }
}
