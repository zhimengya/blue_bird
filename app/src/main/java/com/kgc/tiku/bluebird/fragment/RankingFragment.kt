package com.kgc.tiku.bluebird.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import butterknife.BindView
import butterknife.ButterKnife
import com.alibaba.fastjson.JSON
import com.kgc.tiku.bluebird.R
import com.kgc.tiku.bluebird.adapter.ClassRankingListItemAdapter
import com.kgc.tiku.bluebird.entity.Ranking
import com.kgc.tiku.bluebird.utils.HttpUtils
import com.kgc.tiku.bluebird.utils.UrlConstant
import com.qmuiteam.qmui.widget.QMUIEmptyView
import com.qmuiteam.qmui.widget.QMUITopBar
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class RankingFragment : BaseFragment() {

    private var rootView: View? = null
    @JvmField
    @BindView(R.id.topbar)
    var mTopBar: QMUITopBar? = null
    @JvmField
    @BindView(R.id.ranking_list)
    var rankingListView: ListView? = null

    @JvmField
    @BindView(R.id.emptyView)
    var mEmptyView: QMUIEmptyView? = null

    @JvmField
    @BindView(R.id.refreshLayout)
    var refreshLayout: QMUIPullRefreshLayout? = null

    private var rankingListItemAdapter: ClassRankingListItemAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_tab_ranking, container, false);
        }
        ButterKnife.bind(this, rootView!!)
        initTopBar(resources.getString(R.string.ranking_title))
        initData()
        return rootView
    }


    private fun initData() {
        rankingListItemAdapter =
            ClassRankingListItemAdapter(
                context!!,
                R.layout.class_ranking_list_item,
                ArrayList()
            )
        rankingListView?.adapter = rankingListItemAdapter
        mEmptyView?.show(true)
        getRanking()
        refreshLayout?.setOnPullListener(object : QMUIPullRefreshLayout.OnPullListener {
            override fun onMoveRefreshView(offset: Int) {
            }

            override fun onRefresh() {
                getRanking()
            }

            override fun onMoveTarget(offset: Int) {

            }

        })
    }


    override fun getQMUITopBar(): QMUITopBar {
        return mTopBar!!
    }

    /**
     * 获取数据
     */
    private fun getRanking() {
        val request = HttpUtils.buildGet(UrlConstant.getNowWeekRanking())
        HttpUtils.okHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                rootView!!.post {
                    mEmptyView!!.show(
                        false,
                        e!!.message,
                        null,
                        "点击重试"
                    ) {
                        getRanking()
                    }
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                val html = response!!.body().string()
                val rankingInfo = JSON.parseObject(html)
                if (rankingInfo.getIntValue("code") == 1) {
                    val studentList = rankingInfo.getJSONArray("studentList")
                    val rankings = ArrayList<Ranking>(studentList.size)
                    //封装数据
                    for (i in 0..(studentList.size - 1)) {
                        val ranking = Ranking()
                        val userInfo = studentList.getJSONObject(i)
                        ranking.userName = userInfo.getString("userName")
                        ranking.percentage = userInfo.getString("percentage")
                        ranking.index = i + 1
                        ranking.answerNo = userInfo.getIntValue("answerNo")
                        ranking.actualQuestionNoNow = userInfo.getIntValue("actualQuestionNoNow")
                        rankings.add(ranking)
                    }
                    rootView!!.post {
                        mEmptyView?.hide()
                        //清空适配器
                        rankingListItemAdapter?.clear()
                        //添加到适配器
                        rankingListItemAdapter?.addAll(rankings)
                        //停止旋转
                        refreshLayout?.finishRefresh()
                        //提示一下
                        val successMsg = QMUITipDialog.Builder(context)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING).setTipWord("加载数据成功")
                            .create()
                        successMsg.setCancelable(false)
                        successMsg.show()
                        rootView?.postDelayed({
                            successMsg.dismiss()
                        }, 1000)
                    }
                } else {
                    onFailure(null, IOException(rankingInfo.getString("msg")))
                }
            }

        })
    }

}