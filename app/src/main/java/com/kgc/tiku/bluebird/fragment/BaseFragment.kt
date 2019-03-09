package com.kgc.tiku.bluebird.fragment

import android.content.Intent
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.kgc.tiku.bluebird.R
import com.kgc.tiku.bluebird.activity.AboutActivity
import com.qmuiteam.qmui.widget.QMUITopBar
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog

abstract class BaseFragment : Fragment() {
    private val handler = Handler()
    protected fun initTopBar(title: String) {
        getQMUITopBar().setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        getQMUITopBar().setTitle(title)
        getQMUITopBar().addRightImageButton(R.mipmap.icon_topbar_about, R.id.topbar_right_about_button)
            .setOnClickListener {
                startActivity(Intent(context, AboutActivity::class.java))
            }
    }

    abstract fun getQMUITopBar(): QMUITopBar
}