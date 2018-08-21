package cn.kgc.tiku.bluebird.activity;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.kgc.tiku.bluebird.R;
import cn.kgc.tiku.bluebird.adapter.ViewPagerAdapter;
import cn.kgc.tiku.bluebird.utils.ActivityUtils;
import cn.kgc.tiku.bluebird.utils.Contant;


public class MainActivity extends BasicActivity implements View.OnClickListener {
    private Spinner spExamList;
    private LocalActivityManager manager;
    private ViewPager viewPager;
    private LinearLayout llChat, llFriends, llContacts, llSettings;
    private ImageView ivChat, ivFriends, ivContacts, ivSettings, ivCurrent;
    private TextView tvChat, tvFriends, tvContacts, tvSettings, tvCurrent;
    private List<View> mViews = new ArrayList<View>();
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);//必须写上这一行代码，不然会报错
        initView();
        initData();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        llChat = (LinearLayout) findViewById(R.id.llChat);
        llFriends = (LinearLayout) findViewById(R.id.llFriends);
        llContacts = (LinearLayout) findViewById(R.id.llContacts);
        llSettings = (LinearLayout) findViewById(R.id.llSettings);

        llChat.setOnClickListener(this);
        llFriends.setOnClickListener(this);
        llContacts.setOnClickListener(this);
        llSettings.setOnClickListener(this);

        ivChat = (ImageView) findViewById(R.id.ivChat);
        ivFriends = (ImageView) findViewById(R.id.ivFriends);
        ivContacts = (ImageView) findViewById(R.id.ivContacts);
        ivSettings = (ImageView) findViewById(R.id.ivSettings);

        tvChat = (TextView) findViewById(R.id.tvChat);
        tvFriends = (TextView) findViewById(R.id.tvFriends);
        tvContacts = (TextView) findViewById(R.id.tvContacts);
        tvSettings = (TextView) findViewById(R.id.tvSettings);

        ivChat.setSelected(true);
        tvChat.setSelected(true);
        ivCurrent = ivChat;
        tvCurrent = tvChat;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void initData() {
        Intent intent = new Intent();

        intent.setClass(this, MainTab1Activity.class);
        intent.putExtra("id", 1);
        mViews.add(getView("MainTab1Activity", intent));

        intent.setClass(this, MainTab2Activity.class);
        intent.putExtra("id", 2);
        mViews.add(getView("MainTab2Activity", intent));

        intent.setClass(this, MainTab3Activity.class);
        intent.putExtra("id", 3);
        mViews.add(getView("MainTab3Activity", intent));

        intent.setClass(this, MainTab4Activity.class);
        intent.putExtra("id", 4);
        mViews.add(getView("MainTab4Activity", intent));

        viewPager.setAdapter(new ViewPagerAdapter(mViews));
        tvChat.setTextColor(Color.parseColor("#ff4081"));
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    //没有加群的用户
                    case 0x1:
                        if (ActivityUtils.checkApkExist(MainActivity.this,
                                "com.tencent.mobileqq")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(Contant.config.getQqGroupApi())));
                        } else {
                            ActivityUtils.showAlertMainThread(MainActivity.this, Contant.SYS_INFO_TITLE, "当前登陆账号 " + Contant.LOGIN_ACCOUNT + " 未加群，群号：" + Contant.config.getQqGroupNumber(), null);
                        }
                        break;
                }
            }
        };

    }

    private void changeTab(int id) {
        ivCurrent.setSelected(false);
        tvCurrent.setSelected(false);
        tvChat.setTextColor(Color.BLACK);
        tvCurrent.setTextColor(Color.BLACK);
        tvFriends.setTextColor(Color.BLACK);
        tvSettings.setTextColor(Color.BLACK);

        switch (id) {
            case R.id.llChat:
                viewPager.setCurrentItem(0);
            case 0:
                ivChat.setSelected(true);
                ivCurrent = ivChat;
                tvChat.setSelected(true);
                tvCurrent = tvChat;
                tvChat.setTextColor(Color.parseColor("#ff4081"));
                break;
            case R.id.llFriends:
                viewPager.setCurrentItem(1);
            case 1:
                ivFriends.setSelected(true);
                ivCurrent = ivFriends;
                tvFriends.setSelected(true);
                tvCurrent = tvFriends;
                tvFriends.setTextColor(Color.parseColor("#ff4081"));
                break;
            case R.id.llContacts:
                viewPager.setCurrentItem(2);
            case 2:
                ivContacts.setSelected(true);
                ivCurrent = ivContacts;
                tvContacts.setSelected(true);
                tvCurrent = tvContacts;
                tvContacts.setTextColor(Color.parseColor("#ff4081"));
                break;
            case R.id.llSettings:
                viewPager.setCurrentItem(3);
            case 3:
                ivSettings.setSelected(true);
                ivCurrent = ivSettings;
                tvSettings.setSelected(true);
                tvCurrent = tvSettings;
                tvSettings.setTextColor(Color.parseColor("#ff4081"));
                break;
            default:
                break;
        }
    }

    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }


    @Override
    public void onClick(View v) {
        changeTab(v.getId());
    }

}
