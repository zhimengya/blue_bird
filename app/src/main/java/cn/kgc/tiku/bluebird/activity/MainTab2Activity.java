package cn.kgc.tiku.bluebird.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.ArrayList;

import cn.kgc.tiku.bluebird.R;
import cn.kgc.tiku.bluebird.adapter.ExamClassListItemAdapter;
import cn.kgc.tiku.bluebird.entity.ClassExam;
import cn.kgc.tiku.bluebird.entity.ClassExamPage;
import cn.kgc.tiku.bluebird.entity.ClassExamResult;
import cn.kgc.tiku.bluebird.entity.Topic;
import cn.kgc.tiku.bluebird.entity.UnifiedList;
import cn.kgc.tiku.bluebird.entity.result.AbstractResult;
import cn.kgc.tiku.bluebird.utils.ActivityUtils;
import cn.kgc.tiku.bluebird.utils.Contant;
import cn.kgc.tiku.bluebird.utils.HttpUtils;
import cn.kgc.tiku.bluebird.utils.UrlContant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainTab2Activity extends BasicActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler mHandler;
    private ExamClassListItemAdapter examClassListItemAdapter;
    private ListView examListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab02);
        initView();
        initData();
        initEvent();
        loadExamClass();
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_srl);
        examListView = (ListView) findViewById(R.id.main_lv);
    }

    private void initData() {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x1:
                        String message = (String) msg.obj;
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);//设置不刷新
                        }
                        if (message != null)
                            ActivityUtils.showToast(getApplicationContext(), message);
                        break;
                    case 0x2:
                        examClassListItemAdapter.clear();
                        ClassExam classExam = (ClassExam) msg.obj;
                        if (classExam.getCode() == 0) {
                            new AlertDialog.Builder(MainTab2Activity.this)
                                    .setTitle(Contant.SYS_ERROR_TITLE)
                                    .setMessage("账号在其其它处登陆或者登陆已经失效，点击确定退出。")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            System.exit(0);
                                        }
                                    }).setCancelable(false)
                                    .create().show();
                        } else if (classExam.getCode() != 1) {
                            new AlertDialog.Builder(MainTab2Activity.this)
                                    .setTitle(Contant.TIKU_ERROR_TITLE)
                                    .setMessage(classExam.getMsg())
                                    .setPositiveButton("确定", null).setCancelable(false)
                                    .create().show();
                            return;
                        }
                        for (UnifiedList unifiedList : classExam.getUnifiedList()) {
                            examClassListItemAdapter.add(unifiedList);
                        }
                        break;
                    case 0x3://考试的
                        // examResult((String) msg.obj);
                        ClassExamResult classExamResult = (ClassExamResult) msg.obj;

                        if (classExamResult.getCode() == 0) {
                            ActivityUtils.dismissProgressDialog();
                            ActivityUtils.showAlertMainThread(MainTab2Activity.this, Contant.SYS_ERROR_TITLE, "账号在其其它处登陆或者登陆已经失效，" +
                                    "点击确定退出。", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            });
                            return;
                        } else if (classExamResult.getCode() == 2) {
                            ActivityUtils.dismissProgressDialog();
                            ActivityUtils.showAlertMainThread(MainTab2Activity.this,
                                    Contant.SYS_INFO_TITLE, classExamResult.getMsg() + "，本次考试得分：" + classExamResult.
                                            getUnifiedResult().getExamScore(), null);
                            return;
                        } else if (classExamResult.getCode() != 1) {
                            ActivityUtils.dismissProgressDialog();
                            ActivityUtils.showAlertMainThread(MainTab2Activity.this,
                                    Contant.TIKU_ERROR_TITLE, classExamResult.getMsg(), null);
                            return;
                        }
                        break;
                    case 0x4://弹出框,确定
                        AbstractResult result = (AbstractResult) msg.obj;
                        if (result.getCode() == 0) {
                            ActivityUtils.dismissProgressDialog();
                            ActivityUtils.showAlertMainThread(MainTab2Activity.this, Contant.SYS_ERROR_TITLE, "账号在其其它处登陆或者登陆已经失效，" +
                                    "点击确定退出。", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            });
                        } else if (result.getCode() != 1) {
                            ActivityUtils.dismissProgressDialog();
                            ActivityUtils.showAlertMainThread(MainTab2Activity.this,
                                    Contant.TIKU_ERROR_TITLE, result.getMsg(), null);
                        } else if (result.getCode() == 1) {
                            ActivityUtils.dismissProgressDialog();
                            ClassExamResult classExamPage = (ClassExamResult) result;
                            ActivityUtils.showAlertMainThread(MainTab2Activity.this,
                                    Contant.SYS_INFO_TITLE, "考试结束，本次考试得分：" + classExamPage.
                                            getUnifiedResult().getExamScore(), null);
                        }
                        break;
                    //未加群用户
                    case 0x5:
                        if (ActivityUtils.checkApkExist(MainTab2Activity.this,
                                "com.tencent.mobileqq")) {
                            ActivityUtils.showToast(MainTab2Activity.this, "当前登陆账号 " + Contant.LOGIN_ACCOUNT + " 尚未加群，群号：" + Contant.config.getQqGroupNumber());
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(Contant.config.getQqGroupApi())));
                        } else {
                            ActivityUtils.showAlertMainThread(MainTab2Activity.this, Contant.SYS_INFO_TITLE, "当前登陆账号 " + Contant.LOGIN_ACCOUNT + " 尚未加群，群号：" + Contant.config.getQqGroupNumber(), null);
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        examClassListItemAdapter = new ExamClassListItemAdapter(this, R.layout.exam_class_list_item, new ArrayList<UnifiedList>(), mHandler);
        examListView.setAdapter(examClassListItemAdapter);
        //加载考试列表

    }

    private void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadExamClass();
            }
        });
    }


    private void loadExamClass() {
        HttpUtils.get(UrlContant.EXAM_CLASS, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = "获取考试列表失败，请下拉重试。";
                message.what = 0x1;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ClassExam classExam = JSON.parseObject(response.body().string(), ClassExam.class);
                mHandler.sendEmptyMessage(0x1);
                Message message = new Message();
                message.what = 0x2;
                message.obj = classExam;
                mHandler.sendMessage(message);
            }
        });
    }

    private void examResult(String html) {
        ClassExamResult classExamResult = JSON.parseObject(html, ClassExamResult.class);

        if (classExamResult.getCode() == 0) {
            ActivityUtils.showAlertMainThread(MainTab2Activity.this, Contant.SYS_ERROR_TITLE, "账号在其其它处登陆或者登陆已经失效，" +
                    "点击确定退出。", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            return;
        } else if (classExamResult.getCode() == 2) {
            ActivityUtils.showAlertMainThread(MainTab2Activity.this,
                    Contant.TIKU_ERROR_TITLE, classExamResult.getMsg() + "，本次考试得分：" + classExamResult.
                            getUnifiedResult().getExamScore(), null);
            return;
        } else if (classExamResult.getCode() != 1) {
            ActivityUtils.showAlertMainThread(MainTab2Activity.this,
                    Contant.TIKU_ERROR_TITLE, classExamResult.getMsg(), null);
            return;
        }
        //走到这里表示可以答题

        ClassExamPage classExamPage = JSON.parseObject(html, ClassExamPage.class);
        for (Topic topic : classExamPage.getTopics()) {
            System.out.println(topic.getId());
        }

    }
}
