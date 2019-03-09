package com.kgc.tiku.bluebird.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.kgc.tiku.bluebird.R
import com.kgc.tiku.bluebird.fragment.ExamFragment
import com.kgc.tiku.bluebird.fragment.HomeFragment
import com.kgc.tiku.bluebird.fragment.RankingFragment
import com.kgc.tiku.bluebird.fragment.SelfFragment
import com.kgc.tiku.bluebird.service.HeartService
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog


class MainActivity : FragmentActivity() {
    @JvmField
    @BindView(R.id.btnHome)
    var btnHome: ImageButton? = null
    @JvmField
    @BindView(R.id.btnExam)
    var btnExam: ImageButton? = null
    @JvmField
    @BindView(R.id.btnRanking)
    var btnRanking: ImageButton? = null
    @JvmField
    @BindView(R.id.btnSelf)
    var btnSelf: ImageButton? = null
    private var exitTime: Long = 0

    private var homeFragment: Fragment? = null;
    private var rankingFragment: Fragment? = null;
    private var examFragment: Fragment? = null;
    private var selfFragment: Fragment? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        QMUIStatusBarHelper.translucent(this)
        broadcastListener()
        ButterKnife.bind(this)
        setSelected(R.id.homeLayout)
    }

    /**
     * 初始化FragmentTabHost
     */

    override fun onDestroy() {
        super.onDestroy()
        //停止心跳检测
        val heartService = Intent(this, HeartService::class.java)
        stopService(heartService)
        finish()
    }

    /**
     * 获取广播数据
     */
    class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            val count = bundle!!.getInt("count")
            if (count == 0) {
                val tipDialog = QMUIDialog.MessageDialogBuilder(context).setTitle("提示").setMessage("你的账号在其它处登陆，请重新登录")
                    .addAction("确定") { dialog, _ ->
                        dialog?.dismiss()
                        if (context is Activity) {
                            val loginIntent = Intent(context, LoginActivity::class.java)
                            context.startActivity(loginIntent)
                            context.finish()
                        } else {
                            System.exit(0)
                        }
                    }.create()
                tipDialog.setCancelable(false)
                tipDialog.show()
            }

        }
    }

    /**
     * 监听广播
     */
    private fun broadcastListener() {
        val filter = IntentFilter()
        filter.addAction("com.kgc.tiku.bluebird.HeartService")
        registerReceiver(MyReceiver(), filter)
    }

    @OnClick(R.id.homeLayout, R.id.examLayout, R.id.rankingLayout, R.id.selfLayout)
    fun bottomNarBarClicked(view: View) {
        setSelected(view.id)
    }

    private fun setSelected(id: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        //隐藏fragment
        hideFragment(transaction)
        //置灰
        resetImgs()
        var fragment: Fragment? = null
        when (id) {
            R.id.homeLayout -> {
                if (homeFragment == null) {
                    homeFragment = HomeFragment()
                    transaction.add(R.id.fl_content, homeFragment!!)
                }
                fragment = homeFragment
                btnHome?.setImageResource(R.mipmap.tab_home_active)
            }
            R.id.examLayout -> {
                if (examFragment == null) {
                    examFragment = ExamFragment()
                    transaction.add(R.id.fl_content, examFragment!!)
                }
                fragment = examFragment
                btnExam?.setImageResource(R.mipmap.tab_exam_active)
            }
            R.id.rankingLayout -> {
                if (rankingFragment == null) {
                    rankingFragment = RankingFragment()
                    transaction.add(R.id.fl_content, rankingFragment!!)
                }
                fragment = rankingFragment
                btnRanking?.setImageResource(R.mipmap.tab_ranking_active)
            }
            R.id.selfLayout -> {
                if (selfFragment == null) {
                    selfFragment = SelfFragment()
                    transaction.add(R.id.fl_content, selfFragment!!)
                }
                fragment = selfFragment!!
                btnSelf?.setImageResource(R.mipmap.tab_self_active)
            }
        }
        transaction.show(fragment!!)
        transaction.commit()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        homeFragment?.let {
            transaction.hide(homeFragment!!)
        }
        examFragment?.let {
            transaction.hide(examFragment!!)
        }
        selfFragment?.let {
            transaction.hide(selfFragment!!)
        }
        rankingFragment?.let {
            transaction.hide(rankingFragment!!)
        }
    }

    /**
     * 置灰
     */
    private fun resetImgs() {
        btnHome?.setImageResource(R.mipmap.tab_home)
        btnExam?.setImageResource(R.mipmap.tab_exam)
        btnSelf?.setImageResource(R.mipmap.tab_self)
        btnRanking?.setImageResource(R.mipmap.tab_ranking)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

}