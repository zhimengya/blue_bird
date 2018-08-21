package cn.kgc.tiku.bluebird.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.kgc.tiku.bluebird.R;
import cn.kgc.tiku.bluebird.utils.ActivityUtils;
import cn.kgc.tiku.bluebird.utils.Contant;

/**
 * Created by star on 2018/8/18.
 */

public class MainTab4Activity extends BasicActivity {
    private TextView tvStcs, tvStms;
    private EditText txtStcs, txtStms;
    private Button btnStms, btnStcs, btnHongBao, btnLxzz, btnJiaQun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab04);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        tvStcs = (TextView) findViewById(R.id.tvStcs);
        tvStms = (TextView) findViewById(R.id.tvStms);
        txtStcs = (EditText) findViewById(R.id.txtStcs);
        txtStms = (EditText) findViewById(R.id.txtStms);
        btnStms = (Button) findViewById(R.id.btnStms);
        btnStcs = (Button) findViewById(R.id.btnStcs);
        btnHongBao = (Button) findViewById(R.id.btnHongBao);
        btnLxzz = (Button) findViewById(R.id.btnLxzz);
        btnJiaQun = (Button) findViewById(R.id.btnJiaQun);
    }

    private void initData() {
        String stms = ActivityUtils.getSaveValue("stms", "");
        if ("".equals(stms)) {
            ActivityUtils.saveValue("stms", "20");
        } else {
            System.out.println(stms);
            txtStms.setText(stms);
            Contant.shuaTiMiaoShu = Integer.valueOf(stms);
        }

        String stcs = ActivityUtils.getSaveValue("stcs", "");
        if ("".equals(stcs)) {
            ActivityUtils.saveValue("stcs", "10");
        } else {
            txtStcs.setText(stcs);
            Contant.shuaTiCiShu = Integer.valueOf(stcs);
        }
    }

    private void initEvent() {
        tvStms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.showAlertMainThread(MainTab4Activity.this, Contant.SYS_INFO_TITLE, "刷题秒数意思是：每次答题的每一道题的秒数（不影响刷题速度）。最大：999", null);
            }
        });

        tvStcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.showAlertMainThread(MainTab4Activity.this, Contant.SYS_INFO_TITLE, "刷题次数意思是：每次点击 “开始” 刷题按钮所刷的次数。最大：999", null);
            }
        });

        btnStcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.saveValue("stcs", txtStcs.getText().toString());
                Contant.shuaTiCiShu = Integer.valueOf(txtStcs.getText().toString());
                ActivityUtils.showToast(getApplicationContext(), "保存成功");
                txtStcs.clearFocus();
            }
        });

        btnStms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.saveValue("stms", txtStms.getText().toString());
                Contant.shuaTiMiaoShu = Integer.valueOf(txtStms.getText().toString());
                ActivityUtils.showToast(getApplicationContext(), "保存成功");
                txtStms.clearFocus();
            }
        });

        btnLxzz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityUtils.checkApkExist(MainTab4Activity.this,
                        "com.tencent.mobileqq")) {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + Contant.config.getQqNumber();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else {
                    ActivityUtils.showAlertMainThread(MainTab4Activity.this, Contant.SYS_INFO_TITLE, "尚未安装QQ，无法联系作者。", null);
                }
            }
        });

        btnJiaQun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityUtils.checkApkExist(MainTab4Activity.this,
                        "com.tencent.mobileqq")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(Contant.config.getQqGroupApi())));
                } else {
                    ActivityUtils.showAlertMainThread(MainTab4Activity.this, Contant.SYS_INFO_TITLE, "尚未安装QQ，无法加入QQ群，群号：。" + Contant.config.getQqGroupNumber(), null);
                }
            }
        });

        btnHongBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityUtils.checkApkExist(getApplicationContext(), "com.eg.android.AlipayGphone")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(Contant.config.getAlipay())));
                } else {
                    ActivityUtils.showAlertMainThread(MainTab4Activity.this, Contant.SYS_INFO_TITLE, "尚未安装支付宝，无法获取红包。", null);
                }
            }
        });
    }
}
