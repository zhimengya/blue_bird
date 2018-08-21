package cn.kgc.tiku.bluebird.activity;

import android.app.Activity;
import android.view.KeyEvent;

import cn.kgc.tiku.bluebird.utils.ActivityUtils;

/**
 * Created by star on 2018/8/18.
 */

public abstract class BasicActivity extends Activity {
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ActivityUtils.showToast(getApplicationContext(), "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


}
