package com.kgc.tiku.bluebird.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.kgc.tiku.bluebird.R
import com.kgc.tiku.bluebird.utils.Constant
import com.qmuiteam.qmui.widget.QMUITopBar

class SelfFragment : BaseFragment() {
    @JvmField
    @BindView(R.id.topbar)
    var mTopBar: QMUITopBar? = null

    @JvmField
    @BindView(R.id.listView)
    var listView: ListView? = null

    @JvmField
    @BindView(R.id.userName)
    var userName: TextView? = null

    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_tab_self, container, false);
        }
        ButterKnife.bind(this, rootView!!)
        initTopBar(resources.getString(R.string.self_title))
        userName?.text = Constant.userInfo?.getString("userName")
        return rootView
    }

    override fun getQMUITopBar(): QMUITopBar {
        return mTopBar!!
    }

}