package com.kgc.tiku.bluebird.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.kgc.tiku.bluebird.R
import com.kgc.tiku.bluebird.adapter.LogListItemAdapter
import com.kgc.tiku.bluebird.entity.GradeItem
import com.kgc.tiku.bluebird.service.HeartService
import com.kgc.tiku.bluebird.utils.Constant
import com.kgc.tiku.bluebird.utils.HttpUtils
import com.kgc.tiku.bluebird.utils.UrlConstant
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.QMUITopBar
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.qmuiteam.qmui.widget.popup.QMUIListPopup
import com.qmuiteam.qmui.widget.popup.QMUIPopup
import kotlinx.android.synthetic.main.fragment_tab_home.*
import okhttp3.*
import java.io.IOException
import java.util.*

class HomeFragment : BaseFragment() {

    private var rootView: View? = null
    @JvmField
    @BindView(R.id.topbar)
    var mTopBar: QMUITopBar? = null

    @JvmField
    @BindView(R.id.sbCorrectRate)
    var sbCorrectRate: SeekBar? = null

    @JvmField
    @BindView(R.id.btnStart)
    var btnStart: Button? = null

    private var mListPopup: QMUIListPopup? = null
    private var logListItemAdapter: LogListItemAdapter? = null
    //选择的考试
    private var gradeItem: GradeItem? = null
    //是否在刷题
    private var isRun = false
    private var threadRun = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_tab_home, container, false);
        }
        ButterKnife.bind(this, rootView!!)
        initTopBar(resources.getString(R.string.home_title))
        return rootView
    }


    override fun getQMUITopBar(): QMUITopBar {
        return mTopBar!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        login()
        initData()
    }

    /**
     * 登陆，获取tiku.kgc.cn的cookie
     */
    private fun login() {
        val intent = activity!!.intent
        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")
        val url = HttpUtils.buildGet(UrlConstant.getTwoLoginUrl(username, password)!!)
        val tipDialog = QMUITipDialog.Builder(activity)
            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("校验用户中")
            .create()
        tipDialog.setCancelable(false)
        tipDialog.show()
        HttpUtils.okHttpClient().newCall(url).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                var error: QMUITipDialog? = null
                rootView?.post {
                    error = QMUITipDialog.Builder(activity)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL).setTipWord(e!!.message)
                        .create()
                    error?.setCancelable(false)
                    tipDialog.dismiss()
                    error?.show()
                }
                rootView?.postDelayed({
                    error?.dismiss()
                    activity!!.finish()
                }, 1500)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val html = response!!.body().string()
                val result = JSON.parseObject(html)
                if (result.getIntValue("code") == 1) {
                    rootView?.post {
                        tipDialog.dismiss()
                        //启动心跳检测
                        val heartService = Intent(context, HeartService::class.java)
                        context!!.startService(heartService)
                        log("校验成功，尽情享用吧")
                    }
                } else {
                    onFailure(null, IOException(result.getString("msg")))
                }
            }

        })
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        logListItemAdapter = LogListItemAdapter(context!!, R.layout.log_list_item, ArrayList<String>(0))
        lvLogList!!.adapter = logListItemAdapter

        sbCorrectRate?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Constant.correctRate = seekBar?.progress!!
                val successMsg = QMUITipDialog.Builder(context)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING)
                    .setTipWord("当前正确率:" + Constant.correctRate)
                    .create()
                successMsg.setCancelable(false)
                successMsg.show()
                rootView?.postDelayed({
                    successMsg.dismiss()
                }, 1000)
            }
        })
    }

    @OnClick(R.id.btnStart)
    fun startClicked(view: View) {
        if (isRun) {
            setStatus(false)
            log("停止中...")
        } else {
            initListPopupIfNeed()
            mListPopup?.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
            mListPopup?.setPreferredDirection(QMUIPopup.DIRECTION_TOP)
            mListPopup?.show(view)
        }
    }

    /**
     * 开始答题
     */
    private fun startTopic() {
        Thread {
            setStatus(true)
            for (i in 1..5) {
                log("正在执行第 $i/5 次")
                //获取试卷
                val paper = getPaper() ?: continue

                val paperId = paper.getJSONObject("paper").getLongValue("id")
                val examResultId = paper.getLongValue("examResultId")

                //获取答案

                val answers = getAnswer(paperId,examResultId) ?: continue
                val subResult = submitPaper(paperId, answers)
                if (!subResult) continue
                //保存试卷
                savePaper(paperId)
                if (!isRun) break
            }
            setStatus(false)
            log("答题已结束")
            rootView?.post { btnStart?.isEnabled = true }
        }.start()
    }

    /**
     * 设置当前状态
     */
    private fun setStatus(isRun: Boolean) {
        this.isRun = isRun
        rootView?.post {
            btnStart?.text = if (isRun) "停止" else "开始"
            sbCorrectRate?.isEnabled = !isRun
            if (!isRun) {
                btnStart?.isEnabled = false
            }
        }
    }

    /**
     * 获取试卷
     */
    private fun getPaper(): JSONObject? {
        val response = HttpUtils.okHttpClient().newCall(HttpUtils.buildGet(gradeItem!!.itemUrl)).execute()

        if (response.isSuccessful) {
            val json = JSON.parseObject(response.body().string())

            if (json.getIntValue("code") != 1) {
                log("获取题目失败，可能已在它处登录")
                return null
            }
            return json
        }
        log("获取题目失败，请检查网络")
        return null
    }

    /**
     * 提交试卷，仅仅是提交
     */
    private fun submitPaper(paperId: Long, answers: JSONArray): Boolean {
        val url = "http://tiku.kgc.cn/testing/kgc/app/question/answer?paperId=$paperId"
        val body = FormBody.Builder()
        //拼接 topic id
        for (i in 0..(answers.size - 1)) {
            val answer = answers.getJSONObject(i)
            body.add("questionId", answer.getString("id"))
        }
        //拼接 psq id
        for (i in 0..(answers.size - 1)) {
            val answer = answers.getJSONObject(i)
            body.add("psqId", answer.getString("psqId"))
        }
        //拼接 time
        for (i in 0..(answers.size - 1)) {
            body.add("time", "25")
        }
        //拼接 answer
        val correctRate = Constant.correctRate / 100.00
        val correctTopic = answers.size * correctRate
        for (i in 0..(answers.size - 1)) {
            val answer = answers.getJSONObject(i)
            body.add("uAnswers", if (i < correctTopic) answer.getString("answers") else "5")
        }
        val request = Request.Builder().url(url).addHeader(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36"
        ).post(body.build()).build()
        val response = HttpUtils.okHttpClient().newCall(request).execute()
        if (response.isSuccessful) {
            val json = JSON.parseObject(response.body().string())
            if (json.getIntValue("code") != 1) {
                log("提交答案失败，可能已在它处登录")
                return false
            }
            return true
        }
        log("提交答案失败，请检查网络")
        return false
    }

    /**
     * 保存试卷，此时试卷被真正的提交上去
     */
    private fun savePaper(paperId: Long): Boolean {
        val url = "http://tiku.kgc.cn/testing/kgc/app/saveExamPaper?paperId=$paperId"
        val response = HttpUtils.okHttpClient().newCall(HttpUtils.buildGet(url)).execute()
        if (response.isSuccessful) {
            val json = JSON.parseObject(response.body().string())
            if (json.getIntValue("code") != 1) {
                log("提交答案失败，可能已在它处登录")
                return false
            }
            return true
        }
        log("提交答案失败，请检查网络")
        return false
    }

    /**
     * 获取答案
     */
    private fun getAnswer(paperId: Long,examResultId:Long): JSONArray? {
        val buildGet = HttpUtils.buildGet(UrlConstant.getAnalysisTestPaper(paperId,examResultId))

        val response = HttpUtils.okHttpClient().newCall(buildGet).execute()
        if (response.isSuccessful) {
            val json = JSON.parseObject(response.body().string())
            if (json.getIntValue("code") == 0) {
                log("获取答案失败，可能已在它处登录")
                return null
            }
            return JSON.parseArray(json.getString("cqList"))
        }
        log("获取题目失败，请检查网络")
        return null
    }

    /**
     * 初始化浮层
     */
    private fun initListPopupIfNeed() {
        if (mListPopup == null) {
            val stringArray = resources.getStringArray(R.array.exam_url)
            val listItems = ArrayList<GradeItem>(stringArray.size)
            for (item in stringArray) {
                val i = item.split("|")
                listItems.add(GradeItem(i[0], i[1]))
            }
            val adapter = ArrayAdapter(activity!!, R.layout.simple_list_item, listItems)
            mListPopup = QMUIListPopup(context, QMUIPopup.DIRECTION_NONE, adapter)
            mListPopup?.create(
                QMUIDisplayHelper.dp2px(context, 300), QMUIDisplayHelper.dp2px(context, 200)
            )
            { parent, _, i, _ ->
                gradeItem = parent.getItemAtPosition(i) as GradeItem
                Toast.makeText(activity, "当前选择:" + gradeItem?.itemName, Toast.LENGTH_SHORT).show()
                mListPopup?.dismiss()
                //开始刷题
                startTopic()
            }
        }
    }

    /**
     * 日志
     */
    private fun log(msg: Any) {
        rootView?.post {
            logListItemAdapter?.add(msg.toString())
        }
    }

}