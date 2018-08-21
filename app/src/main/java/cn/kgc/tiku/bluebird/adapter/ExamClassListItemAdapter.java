package cn.kgc.tiku.bluebird.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.kgc.tiku.bluebird.R;
import cn.kgc.tiku.bluebird.activity.MainTab2Activity;
import cn.kgc.tiku.bluebird.entity.ClassExamPage;
import cn.kgc.tiku.bluebird.entity.ClassExamResult;
import cn.kgc.tiku.bluebird.entity.Topic;
import cn.kgc.tiku.bluebird.entity.UnifiedList;
import cn.kgc.tiku.bluebird.entity.result.AbstractResult;
import cn.kgc.tiku.bluebird.entity.result.BasicResult;
import cn.kgc.tiku.bluebird.utils.ActivityUtils;
import cn.kgc.tiku.bluebird.utils.Contant;
import cn.kgc.tiku.bluebird.utils.HttpUtils;
import cn.kgc.tiku.bluebird.utils.UrlContant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.alibaba.fastjson.JSON.parseObject;

public class ExamClassListItemAdapter extends ArrayAdapter {
    private int resourceId;
    private Handler handler;
    private int isJx = 0;

    public ExamClassListItemAdapter(Context context, int textViewResourceId, List<UnifiedList> objects, Handler handler) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.handler = handler;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UnifiedList unifiedList = (UnifiedList) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView txtExamName = (TextView) view.findViewById(R.id.txtExamName);
        TextView txtExamTime = (TextView) view.findViewById(R.id.txtExamTime);
        Button btnStart = (Button) view.findViewById(R.id.btnStart);
        txtExamName.setText(unifiedList.getTitle());
        txtExamTime.setText(ActivityUtils.convertToDataString(unifiedList.getExamBeginTime()) + "-" +
                ActivityUtils.convertToDataString(unifiedList.getExamEndTime()));
        MainTab2Activity mainTab2Activity = (MainTab2Activity) getContext();
        System.out.println(mainTab2Activity);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ActivityUtils.c()) {
                    handler.sendEmptyMessage(0x5);
                    return;
                }
                long beginTime = unifiedList.getExamBeginTime();
                long currTime = System.currentTimeMillis();
                if (currTime >= beginTime) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(Contant.SYS_INFO_TITLE)
                            .setMessage("当前正确率："+Contant.ZQL+"%，你确定要开始考试吗？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityUtils.showProgressDialog(getContext(), "考试中，请耐心等待...");
                                    HttpUtils.post(UrlContant.EXAM_CLASS_PAPER, new HashMap<String, String>() {{
                                        put("unifiedId", String.valueOf(unifiedList.getId()));
                                    }}, new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String html = response.body().string();
                           /* Message message = new Message();
                            message.obj = response.body().string();
                            message.what = 0x3;
                            handler.sendMessage(message);*/
                                            ClassExamResult classExamResult = parseObject(html, ClassExamResult.class);
                                            if (classExamResult.getCode() != 1) {
                                                Message message = new Message();
                                                message.obj = classExamResult;
                                                message.what = 0x3;
                                                handler.sendMessage(message);
                                                return;
                                            }


                                            //走到这里表示可以答题
                                            final ClassExamPage classExamPage = parseObject(html, ClassExamPage.class);
                                            //获取答案
                                            html = HttpUtils.get(UrlContant.EXAM_ANSWER_URL + "?examResultId=0&paperId=" + classExamPage.getPaper().getId());
                                            System.out.println(html);

                                            List<Topic> answers = JSON.parseArray(parseObject(html).getString("cqList"), Topic.class);
                                            html = HttpUtils.post(UrlContant.EXAM_SUBMIT, classExamPage.submitParams(answers, Contant.ZQL));

                                            AbstractResult result = JSON.parseObject(html, BasicResult.class);
                                            if (result.getCode() != 1) {
                                                Message message = new Message();
                                                message.obj = result;
                                                message.what = 0x4;
                                                handler.sendMessage(message);
                                                return;
                                            }
                                            html = HttpUtils.post(UrlContant.EXAM_CLASS_SAVE_PAPER, new HashMap<String, String>() {{
                                                put("unifiedId", String.valueOf(unifiedList.getId()));
                                                put("examResultId", String.valueOf(classExamPage.getExamResultId()));
                                            }});
                                            classExamResult = JSON.parseObject(html, ClassExamResult.class);
                                            Message message = new Message();
                                            message.obj = classExamResult;
                                            message.what = 0x4;
                                            handler.sendMessage(message);

                                        }
                                    });
                                }
                            }).setCancelable(false)
                            .create().show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle(Contant.SYS_INFO_TITLE)
                            .setMessage("考试时间未到，不能考试。")
                            .setPositiveButton("确定", null).setCancelable(false)
                            .create().show();
                }
            }
        });
        return view;
    }

}
