package com.kgc.tiku.bluebird.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.kgc.tiku.bluebird.R
import com.kgc.tiku.bluebird.utils.Constant
import com.kgc.tiku.bluebird.utils.DesUtils
import com.kgc.tiku.bluebird.utils.HttpUtils
import com.kgc.tiku.bluebird.utils.UrlConstant
import com.qmuiteam.qmui.widget.QMUITopBar
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class ExamFragment : BaseFragment() {

    private var rootView: View? = null
    @JvmField
    @BindView(R.id.topbar)
    var mTopBar: QMUITopBar? = null
    @JvmField
    @BindView(R.id.groupListView)
    var groupListView: QMUIGroupListView? = null
    @JvmField
    @BindView(R.id.refreshLayout)
    var refreshLayout: QMUIPullRefreshLayout? = null

    private val sdf = SimpleDateFormat("MM-dd HH:mm:ss")
    private var loadingDialog: QMUITipDialog? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_tab_exam, container, false);
        }
        ButterKnife.bind(this, rootView!!)
        initTopBar(resources.getString(R.string.exam_title))
        initGroupListView()
        initData()
        return rootView
    }


    override fun getQMUITopBar(): QMUITopBar {
        return mTopBar!!
    }


    private fun initGroupListView() {

    }

    private fun initData() {
        refreshLayout?.setOnPullListener(object : QMUIPullRefreshLayout.OnPullListener {
            override fun onMoveRefreshView(offset: Int) {
            }

            override fun onRefresh() {
                getExamList()
            }

            override fun onMoveTarget(offset: Int) {

            }

        })
        getExamList()
    }

    /**
     * 获取考试列表
     */
    private fun getExamList() {
        val tipDialog = QMUITipDialog.Builder(context)
            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("获取列表中")
            .create()
        tipDialog.setCancelable(false)
        tipDialog.show()
        val request = HttpUtils.buildGet(UrlConstant.getExamUrl())
        HttpUtils.okHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                rootView?.post {
                    groupListView?.removeAllViews()
                    refreshLayout?.finishRefresh()
                    tipDialog.dismiss()
                    val successMsg = QMUITipDialog.Builder(context)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING).setTipWord("获取失败，下拉重试")
                        .create()
                    successMsg.show()
                    rootView?.postDelayed({
                        successMsg.dismiss()
                    }, 1000)
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                val json = JSON.parseObject(response!!.body().string())
                if (json.getIntValue("code") == 0) {
                    onFailure(null, null)
                    return
                }
                val unifiedList = json.getJSONArray("unifiedList")
                val glv = QMUIGroupListView.newSection(context)
                for (i in 0..(unifiedList.size - 1)) {
                    val exam = unifiedList.getJSONObject(i)
                    var title = exam.getString("title")
                    title += " "
                    title += sdf.format(Date(exam.getLongValue("examBeginTime")))
                    title += "-"
                    title += sdf.format(Date(exam.getLongValue("examEndTime")))
                    val itemWithCustom = groupListView!!.createItemView(title)
                    itemWithCustom.tag = exam
                    itemWithCustom.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON
                    glv.addItemView(itemWithCustom) { v: View? ->
                        executeExam(v?.tag as JSONObject)
                    }
                }

                rootView?.post {
                    groupListView?.removeAllViews()
                    glv.addTo(groupListView)
                    tipDialog.dismiss()
                    refreshLayout?.finishRefresh()
                }
            }

        })
    }

    /**
     * 执行考试
     */
    private fun executeExam(exam: JSONObject) {
        val nowTime = System.currentTimeMillis()
        val examBeginTime = exam.getLongValue("examBeginTime")
        val examEndTime = exam.getLongValue("examEndTime")
        var dialog: QMUIDialog? = null
        when {
            examBeginTime > nowTime -> {
                dialog =
                    QMUIDialog.MessageDialogBuilder(context).setTitle("提示")
                        .setMessage("考试尚未开始。开始时间:" + sdf.format(Date(examBeginTime)))
                        .addAction("确定") { _, _ ->
                            dialog?.dismiss()
                        }.create()
            }
            nowTime > examEndTime -> {
                dialog = QMUIDialog.MessageDialogBuilder(context).setTitle("提示")
                    .setMessage("考试已结束")
                    .addAction("确定") { _, _ ->
                        dialog?.dismiss()
                    }.create()
            }
            else -> {
                Toast.makeText(context, "当前正确率：" + Constant.correctRate + "%，你确定要考试吗?", Toast.LENGTH_SHORT).show()
            }
        }
        dialog?.setCancelable(false)
        dialog?.show()
    }

    /**
     * 根据题目获取id
     */
    private fun getAnswerByTopicId(answers: JSONArray, topicId: Int): String? {
        for (i in 0..(answers.size - 1)) {
            val answer = answers.getJSONObject(i)
            if (answer.getIntValue("id") == topicId) {
                return answer.getString("answers")
            }
        }
        return null
    }

    /**
     * 考试完毕
     */
    private fun examOver(json: JSONObject) {
        var dialog: QMUIDialog? = null
        rootView?.post {
            loadingDialog?.dismiss()
            dialog =
                QMUIDialog.MessageDialogBuilder(context).setTitle("提示")
                    .setMessage("考试完毕，分数:" + json.getJSONObject("unifiedResult").getDoubleValue("examScore"))
                    .addAction("确定") { _, _ ->
                        dialog?.dismiss()
                    }.create()
            dialog?.show()
        }
    }

    /**
     * 开始考试
     */
    private fun startExam(examId: Long) {
        var dialog: QMUIDialog? = null
        loadingDialog = QMUITipDialog.Builder(context)
            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("考试中")
            .create()
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
        //获取考试试卷
        val examPaperRequest = HttpUtils.buildGet(UrlConstant.getUnifiedExamUrl(examId))
        HttpUtils.okHttpClient().newCall(examPaperRequest).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                rootView?.post {
                    loadingDialog?.dismiss()
                    dialog =
                        QMUIDialog.MessageDialogBuilder(context).setTitle("提示")
                            .setMessage(e?.message)
                            .addAction("确定") { _, _ ->
                                dialog?.dismiss()
                            }.create()
                    dialog?.show()
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                val paper = JSON.parseObject(response!!.body().string())
                val paperCode = paper.getIntValue("code")
                //code==2表示已经考试过了
                if (paperCode == 2) {
                    examOver(paper)
                    return
                } else if (paperCode != 1) {
                    onFailure(null, IOException("获取题目，状态码不为1，msg:" + paper.getString("msg")))
                    return
                }
                //获取题目
                val cqList = JSON.parseArray(DesUtils.decrypt(paper.getString("cqList")))
                val paperId = paper.getJSONObject("paper").getLongValue("id")
                //获取答案
                val answerRequest =
                    HttpUtils.buildGet(UrlConstant.getAnalysisTestPaper(paperId))
                val answerResponse = HttpUtils.okHttpClient().newCall(answerRequest).execute()
                if (!answerResponse.isSuccessful) {
                    onFailure(null, IOException("获取考试答案失败"))
                    return
                }
                val answer = JSON.parseObject(answerResponse.body().string())
                if (answer.getIntValue("code") != 1) {
                    onFailure(null, IOException("获取答案，状态码不为1，msg:" + paper.getString("msg")))
                    return
                }
                val topicAnswer = JSON.parseArray(answer.getString("cqList"))
                val params = HashMap<String, Map<String, Any>>(cqList.size)
                var errorTopic = 0
                val correctRate = Constant.correctRate / 100.00
                val correctTopic = cqList.size * correctRate
                for (i in 0..(cqList.size - 1)) {
                    val topic = cqList.getJSONObject(i)
                    val id = topic.getIntValue("id")
                    var ans = getAnswerByTopicId(topicAnswer, id)
                    if (ans == null || i > correctTopic) {
                        ans = "5"
                        errorTopic++
                    }
                    params[id.toString()] = mapOf(
                        "position" to i, "psqId" to topic.getLongValue("psqId")
                        , "questionId" to id, "time" to 25,
                        "uAnswer" to ans
                    )
                }
                val examResultId = paper.getLongValue("examResultId")
                Log.i("Test0", JSON.toJSONString(params))
                val json = URLEncoder.encode(DesUtils.encrypt(JSON.toJSONString(params)), "UTF-8")
                Log.i("Test1", json)
                val submitUnifiedExamUrl = UrlConstant.getSubmitUnifiedExamUrl(examResultId, json, examId)
                val submitUnifiedExamReq = HttpUtils.buildGet(submitUnifiedExamUrl)
                val resp = HttpUtils.okHttpClient().newCall(submitUnifiedExamReq).execute()
                val unifiedRes = JSON.parseObject(resp.body().string())
                if (unifiedRes.getIntValue("code") != 1) {
                    onFailure(null, IOException("提交试卷失败，msg:" + paper.getString("msg")))
                    return
                }
                //判断错题是否是当前题目的一半
                if (errorTopic > cqList.size / 2) {
                    onFailure(
                        null,
                        IOException("当前错题数:" + errorTopic + "，总题数:" + cqList.size + "。触发保险，禁止提交。调高正确率重新考试 或者 请打开青鸟云题库手动提交，答案已设置！")
                    )
                    return
                }
                val saveExamResp =
                    HttpUtils.okHttpClient().newCall(UrlConstant.getSaveUnifiedExamResultUrl(examResultId, examId))
                        .execute()
                val saveExamRes = JSON.parseObject(saveExamResp.body().string())
                if (saveExamRes.getIntValue("code") != 1) {
                    onFailure(null, IOException("保存试卷失败，msg:" + paper.getString("msg")))
                    return
                }
                //考试结束
                examOver(saveExamRes)
            }

        })
    }
}