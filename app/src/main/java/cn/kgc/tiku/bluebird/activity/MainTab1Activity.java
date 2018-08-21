package cn.kgc.tiku.bluebird.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.kgc.tiku.bluebird.R;
import cn.kgc.tiku.bluebird.adapter.LogListItemAdapter;
import cn.kgc.tiku.bluebird.entity.ExamPage;
import cn.kgc.tiku.bluebird.entity.ExamResult;
import cn.kgc.tiku.bluebird.entity.LogListItem;
import cn.kgc.tiku.bluebird.entity.ProductList;
import cn.kgc.tiku.bluebird.entity.SpinnerItem;
import cn.kgc.tiku.bluebird.entity.Topic;
import cn.kgc.tiku.bluebird.entity.result.AbstractResult;
import cn.kgc.tiku.bluebird.entity.result.BasicResult;
import cn.kgc.tiku.bluebird.utils.ActivityUtils;
import cn.kgc.tiku.bluebird.utils.Contant;
import cn.kgc.tiku.bluebird.utils.HttpUtils;
import cn.kgc.tiku.bluebird.utils.UrlContant;

import static cn.kgc.tiku.bluebird.utils.HttpUtils.post;
import static com.alibaba.fastjson.JSON.parseObject;


public class MainTab1Activity extends BasicActivity {
    private Spinner spExamList;
    private ListView lvLogList;
    private SeekBar sbCorrectRate;
    private LogListItemAdapter logListItemAdapter;
    private Button btnStart;
    private boolean isStart = false;
    private Thread topicThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab01);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        spExamList = (Spinner) findViewById(R.id.spExamList);
        lvLogList = (ListView) findViewById(R.id.lvLogList);
        sbCorrectRate = (SeekBar) findViewById(R.id.sbCorrectRate);
        btnStart = (Button) findViewById(R.id.btnStart);
    }

    private void initData() {
        ArrayList<SpinnerItem> all = new ArrayList<SpinnerItem>();
        all.add(new SpinnerItem("北大青鸟_ACCP_S1", UrlContant.EXAM_ACCP_S1));
        all.add(new SpinnerItem("北大青鸟_ACCP_S2", UrlContant.EXAM_ACCP_S2));
        all.add(new SpinnerItem("北大青鸟_ACCP_Y2", UrlContant.EXAM_ACCP_Y2));
        all.add(new SpinnerItem("北大青鸟_BENET_S1", UrlContant.EXAM_BENET_S1));
        all.add(new SpinnerItem("北大青鸟_BENET_S2", UrlContant.EXAM_BENET_S2));
        all.add(new SpinnerItem("北大青鸟_BENET_Y2", UrlContant.EXAM_BENET_Y2));
        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<SpinnerItem>(this, R.layout.activity_spinner, all);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spExamList.setAdapter(adapter);
        //设置默认值
        spExamList.setVisibility(View.VISIBLE);
        logListItemAdapter = new LogListItemAdapter(MainTab1Activity.this, R.layout.log_list_item,
                new ArrayList<LogListItem>() {{
                    add(new LogListItem(LogListItem.SUCCESS, "程序加载完成，尽情开始吧。"));
                }});
        //显示当前选中产品
        for (ProductList productList : Contant.userInfo.getProductList()) {
            if (productList.getIsLastLoginProduct()) {
                logListItemAdapter.add(new LogListItem(LogListItem.SUCCESS, "当前选中产品：" + productList.getProductName() + "。"));
            }
        }
        lvLogList.setAdapter(logListItemAdapter);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        String zql = ActivityUtils.getSaveValue("zql", "");
        if ("".equals(zql)) {
            ActivityUtils.saveValue("zql", "80");
        } else {
            Contant.ZQL = Integer.valueOf(zql);
            sbCorrectRate.setProgress(Contant.ZQL);
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    //账号在其他处登陆
                    case 0x1:
                        new AlertDialog.Builder(MainTab1Activity.this)
                                .setTitle(Contant.SYS_ERROR_TITLE)
                                .setMessage(Contant.LOGIN_ERROR)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.exit(0);
                                    }
                                }).setCancelable(false)
                                .create().show();
                        break;
                }
            }
        };
    }

    private void start() {
        if (!ActivityUtils.c()) {

            if (ActivityUtils.checkApkExist(MainTab1Activity.this,
                    "com.tencent.mobileqq")) {
                ActivityUtils.showToast(this, "当前登陆账号 " + Contant.LOGIN_ACCOUNT + " 尚未加群，群号：" + Contant.config.getQqGroupNumber());
                startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse(Contant.config.getQqGroupApi())));
            } else {
                ActivityUtils.showAlertMainThread(MainTab1Activity.this, Contant.SYS_INFO_TITLE, "当前登陆账号 " + Contant.LOGIN_ACCOUNT + " 尚未加群，群号：" + Contant.config.getQqGroupNumber(), null);
            }
            return;
        }
        if (!isStart) {
            isStart = true;
            this.btnStart.setText("停止");
            this.spExamList.setEnabled(false);
            this.sbCorrectRate.setEnabled(false);
            logListItemAdapter.add(new LogListItem(LogListItem.SUCCESS, "当前答题学期：" + spExamList.getSelectedItem() + "，当前正确率 " + this.sbCorrectRate.getProgress() + "%。"));
        } else if (isStart) {
            this.btnStart.setText("开始");
            this.spExamList.setEnabled(true);
            this.sbCorrectRate.setEnabled(true);
            logListItemAdapter.add(new LogListItem(LogListItem.WARNING, "正在停止，请稍等几秒钟..."));
            isStart = false;
            return;
        }

        if (logListItemAdapter.getCount() > 200) {
            logListItemAdapter.clear();
        }

        topicThread = new Thread() {
            @Override
            public void run() {
                boolean isUserExpiration = true;

                for (int i = 0; i < Contant.shuaTiCiShu && isStart; i++) {
                    try {
                        subThreadAddLogItem(new LogListItem(LogListItem.SUCCESS, "正在答题，第 " + (i + 1) + " / " + Contant.shuaTiCiShu + " 次..."));
                        AbstractResult result = null;
                        //获取题目
                        String html = HttpUtils.get(((SpinnerItem) spExamList.getSelectedItem()).getUrl());
                        final ExamPage examPage = parseObject(html, ExamPage.class);
                        if (examPage.getCode() == 0) {
                            isUserExpiration = false;
                            break;
                        } else if (examPage.getCode() != 1) {
                            subThreadAddLogItem(new LogListItem(LogListItem.SUCCESS, "获取题目失败，跳过本次答题..."));
                            continue;
                        }
                        //判断是否停止
                        if (!isStart) {
                            break;
                        }
                        subThreadAddLogItem(new LogListItem(LogListItem.SUCCESS, "获取题目成功，开始获取填充答案..."));

                        //获取答案
                        html = HttpUtils.get(UrlContant.EXAM_ANSWER_URL + "?examResultId=0&paperId=" + examPage.getPaper().getId());
                        List<Topic> answers = JSON.parseArray(parseObject(html).getString("cqList"), Topic.class);
                        subThreadAddLogItem(new LogListItem(LogListItem.SUCCESS, "答案填充成功，开始提交试卷..."));

                        //判断是否停止
                        if (!isStart) {
                            break;
                        }

                        //提交试卷
                        html = post(UrlContant.EXAM_SUBMIT, examPage.submitParams(answers, Contant.ZQL));
                        result = JSON.parseObject(html, BasicResult.class);
                        if (examPage.getCode() == 0) {
                            isUserExpiration = false;
                            break;
                        } else if (result.getCode() != 1) {
                            subThreadAddLogItem(new LogListItem(LogListItem.ERROR, "试卷提交失败，跳过本次答题..."));
                            continue;
                        }

                        //判断是否停止
                        if (!isStart) {
                            break;
                        }

                        //保存试卷
                        html = post(UrlContant.EXAM_SAVE, new HashMap<String, String>() {{
                            put("paperId", String.valueOf(examPage.getPaper().getId()));
                        }});
                        //判断是否停止
                        if (!isStart) {
                            break;
                        }

                        result = JSON.parseObject(html, BasicResult.class);
                        if (examPage.getCode() == 0) {
                            isUserExpiration = false;
                            break;
                        } else if (result.getCode() != 1) {
                            subThreadAddLogItem(new LogListItem(LogListItem.ERROR, "试卷提交失败，跳过本次答题..."));
                            continue;
                        }
                        //判断是否停止
                        if (!isStart) {
                            break;
                        }

                        subThreadAddLogItem(new LogListItem(LogListItem.SUCCESS, "试卷提交成功，开始获取分数..."));

                        //获取答题结果
                        html = HttpUtils.post(UrlContant.EXAM_RESULT, new HashMap<String, String>() {{
                            put("examResultId", String.valueOf(examPage.getExamResultId()));
                            put("paperId", String.valueOf(examPage.getPaper().getId()));
                        }});

                        //判断是否停止
                        if (!isStart) {
                            break;
                        }

                        ExamResult examResult = JSON.parseObject(html, ExamResult.class);
                        if (examPage.getCode() == 0) {
                            isUserExpiration = false;
                            break;
                        } else if (examResult.getCode() != 1) {
                            subThreadAddLogItem(new LogListItem(LogListItem.ERROR, "获取分数失败，跳过本次答题..."));
                            continue;
                        }

                        //判断是否停止
                        if (!isStart) {
                            break;
                        }

                        subThreadAddLogItem(new LogListItem(LogListItem.SUCCESS, "答题得分：" + examResult.getExamReport().
                                getTotalScore() + "。"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        subThreadAddLogItem(new LogListItem(LogListItem.ERROR, "答题出错，错误原因：" + e.getMessage()));
                        break;
                    }
                }
                if (!isUserExpiration) {
                    handler.sendEmptyMessage(0x1);
                }
                subThreadAddLogItem(new LogListItem(LogListItem.WARNING, "答题已经停止。"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainTab1Activity.this.btnStart.setText("开始");
                        MainTab1Activity.this.spExamList.setEnabled(true);
                        MainTab1Activity.this.sbCorrectRate.setEnabled(true);
                        isStart = false;
                    }
                });
            }
        };
        topicThread.start();
    }

    private void subThreadAddLogItem(final LogListItem item) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logListItemAdapter.add(item);
            }
        });
    }

    private void initEvent() {
        //添加事件Spinner事件监听
        spExamList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sbCorrectRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Contant.ZQL = seekBar.getProgress();
                ActivityUtils.saveValue("zql", String.valueOf(Contant.ZQL));
                ActivityUtils.showToast(getApplicationContext(), "设置成功，当前正确率：" + Contant.ZQL + "%");
            }
        });
    }


}
