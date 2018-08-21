package cn.kgc.tiku.bluebird.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.kgc.tiku.bluebird.R;
import cn.kgc.tiku.bluebird.adapter.ClassRankingListItemAdapter;
import cn.kgc.tiku.bluebird.entity.Ranking;
import cn.kgc.tiku.bluebird.utils.ActivityUtils;
import cn.kgc.tiku.bluebird.utils.Contant;
import cn.kgc.tiku.bluebird.utils.HttpUtils;
import cn.kgc.tiku.bluebird.utils.UrlContant;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by star on 2018/8/18.
 */

public class MainTab3Activity extends BasicActivity {
    private Handler handler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView classRankingList;
    private ClassRankingListItemAdapter rankingListItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab03);
        initView();
        initData();
        initEvent();
        loadRanking();
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_srl);
        classRankingList = (ListView) findViewById(R.id.class_ranking_list);
    }

    private void initData() {
        rankingListItemAdapter = new ClassRankingListItemAdapter(this, R.layout.class_ranking_list_item, new ArrayList<Ranking>());
        classRankingList.setAdapter(rankingListItemAdapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    //获取排行榜成功
                    case 0x1:
                        JSONObject json = JSON.parseObject((String) msg.obj);
                        if (json.getInteger("code") == 0) {
                            ActivityUtils.showAlertMainThread(MainTab3Activity.this, Contant.SYS_ERROR_TITLE,
                                    Contant.LOGIN_ERROR, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            System.exit(0);
                                        }
                                    });
                        } else if (json.getInteger("code") != 1) {
                            ActivityUtils.showAlertMainThread(MainTab3Activity.this, Contant.TIKU_ERROR_TITLE,
                                    json.getString("msg"), null);
                        } else {
                            JSONArray studentList = json.getJSONArray("studentList");
                            rankingListItemAdapter.clear();
                            for (int i = 0; i < studentList.size(); i++) {
                                json = studentList.getJSONObject(i);
                                Ranking ranking = new Ranking();
                                ranking.setMc(i + 1);
                                ranking.setXm(json.getString("userName"));
                                ranking.setLjdt(json.getInteger("answerNo"));
                                ranking.setSjdt(json.getInteger("actualQuestionNoNow"));
                                ranking.setZql((json.getDouble("correctRate") * 100));
                                rankingListItemAdapter.add(ranking);
                            }
//                            Toast.makeText(getApplicationContext(), "获取排名完成",
//                                    Toast.LENGTH_SHORT).show();

                        }
                        break;
                    //获取排行榜失败
                    case 0x2:
                        ActivityUtils.showAlertMainThread(MainTab3Activity.this, Contant.SYS_ERROR_TITLE,
                                "获取排行榜错误，请刷新重试。", null);
                        break;
                    //取消下拉刷新
                    case 0x3:
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);//设置不刷新
                        }
                        break;
                }
            }
        };

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

    }

    private void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRanking();
            }
        });


        // 解决listview和swipeRefreshLayout的下拉冲突
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                System.out.println(classRankingList.getFirstVisiblePosition());
                return classRankingList.getFirstVisiblePosition() != 0;
            }
        });
    }

    private void loadRanking() {

        //获取排行榜
        HttpUtils.post(UrlContant.RANKING, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(0x3);
                handler.sendEmptyMessage(0x2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.sendEmptyMessage(0x3);
                Message message = new Message();
                message.obj = response.body().string();
                message.what = 0x1;
                handler.sendMessage(message);
            }
        });

    }
}
