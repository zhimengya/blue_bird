package com.kgc.tiku.bluebird.utils

import android.util.Log
import okhttp3.Request
import java.net.URLDecoder
import java.util.*

class UrlConstant private constructor() {
    companion object {
        private const val TOW_LOGIN_URL = "http://tiku.kgc.cn/testing/exam/app/login"
        private const val ONE_LOGIN_URL = "http://a.bdqn.cn/pb/pbsub/web/login/user_login.action"
        private const val CHANGE_PRODUCT_URL =
            "http://a.bdqn.cn/pb/pbsub/web/qingqing/product_change.action?clientType=009&productId=%d&userId=%d"
        private const val NOW_WEEK_RANKING_URL = "http://tiku.kgc.cn/testing/exam/app/classRanking?monthOrWeek=nowWeek"
        private const val EXAM_URL = "http://tiku.kgc.cn/testing/exam/app/unified/classExam"
        private const val UNIFIED_EXAM_URL = "http://tiku.kgc.cn/testing/exam/app/unified/exam"
        private const val SUBMIT_UNIFIED_EXAM_URL = "http://tiku.kgc.cn/testing/exam/app/unified/answerAll"
        private const val SAVE_UNIFIED_EXAM_RESULT =
            "http://tiku.kgc.cn/testing/exam/app/unified/submitExam?submitWay=1"
        const val CHECK_URL = "https://gitee.com/starxg/bl/raw/master/switch"
        /**
         * 获取第二次登陆地址
         */
        fun getTwoLoginUrl(username: String, password: String): String? {
            val parameterUtils = ParameterUtils()
            parameterUtils.addParam("passport", username)
            parameterUtils.addParam("password", Md5Utils.md5Encode(password))
            parameterUtils.addParam("clientType", "009")
            parameterUtils.addParam("version", "Version_1.1.3")
            parameterUtils.addParam("_yl005_", parameterUtils.getAuthCnParam(false))
            return parameterUtils.getQuestionUrl(TOW_LOGIN_URL)
        }

        /**
         * 获取第一次登陆地址
         */
        fun getOneLoginUrl(username: String, password: String): String? {
            val parameterUtils = ParameterUtils()
            parameterUtils.addParam("passport", username)
            parameterUtils.addParam("password", Md5Utils.md5Encode(password))
            parameterUtils.addParam("encrypt", parameterUtils.getAuthCnParam(false))
            parameterUtils.addParam("clientType", "009")
            return parameterUtils.getQuestionUrl(ONE_LOGIN_URL)
        }

        /**
         * 切换产品
         */
        fun getChangeProductUrl(productId: Int, userId: Int): String {
            return String.format(CHANGE_PRODUCT_URL, productId, userId)
        }

        /**
         * 获取本周排名
         */
        fun getNowWeekRanking(): String {
            return NOW_WEEK_RANKING_URL
        }

        /**
         * 获取考试答案
         */
        fun getAnalysisTestPaper(paperId: Long): String {
            val parameterUtils = UrlConstant.ParameterUtils()
            parameterUtils.addParam("paperId", paperId.toString())
            parameterUtils.addParam("examResultId", "0")
            parameterUtils.addParam("_yl005_", parameterUtils.getAuthCnParam(false))
            return parameterUtils.getQuestionUrl("http://tiku.kgc.cn/testing/kgc/app/paper/solutions")!!
        }

        /**
         * 获取考试列表
         */
        fun getExamUrl(): String {
            return EXAM_URL
        }

        /**
         * 获取统一考试试卷
         */
        fun getUnifiedExamUrl(unifiedId: Long): String {
            val parameterUtils = UrlConstant.ParameterUtils()
            parameterUtils.addParam("unifiedId", unifiedId.toString())
            parameterUtils.addParam("_yl005_", parameterUtils.getAuthCnParam(false))
            return parameterUtils.getQuestionUrl(UNIFIED_EXAM_URL)!!
        }

        /**
         * 获取提交试卷的url
         */
        fun getSubmitUnifiedExamUrl(examResultId: Long, json: String, unifiedId: Long): String {
            Log.i("Test2", json)
            val parameterUtils = UrlConstant.ParameterUtils()
            parameterUtils.addParam("examResultId", examResultId.toString())
            parameterUtils.addParam("json", json)
            parameterUtils.addParam("unifiedId", unifiedId.toString())
            parameterUtils.addParam("_yl005_", parameterUtils.getAuthCnParam(false))
            val questionUrl = parameterUtils.getQuestionUrl(SUBMIT_UNIFIED_EXAM_URL)!!
            Log.i("Test3", questionUrl)
            return questionUrl
        }

        /**
         * 获取保存考试答案地址
         */
        fun getSaveUnifiedExamResultUrl(examResultId: Long, unifiedId: Long): Request {
            val params =
                mapOf("examResultId" to examResultId.toString(), "unifiedId" to unifiedId.toString())
            return HttpUtils.buildPost(SAVE_UNIFIED_EXAM_RESULT, params)
        }


    }

    private class ParameterUtils {
        private var mParameters: ArrayList<Parameter>? = null

        fun addParam(key: String, value: String) {
            val param = Parameter(key, value)
            if (this.mParameters == null) {
                this.mParameters = ArrayList()
            }
            this.mParameters!!.add(param)
        }

        fun getAuthCnParam(isExam: Boolean): String {
            val auth: String
            Collections.sort(mParameters, SortByKey())
            val url = addslashes()
            auth = if (isExam) {
                url + "cn_bdqn"
            } else {
                url + "cn__kgc"
            }
            return Md5Utils.md5Encode(URLDecoder.decode(auth, "UTF-8"))
        }


        fun getQuestionUrl(pathUrl: String): String? {
            var mUrl: String
            if (this.mParameters != null) {
                this.mParameters!!.sortWith(SortByKey())
                val url = addslashes()
                mUrl = pathUrl
                if (url.isNotEmpty()) {
                    mUrl = "$mUrl?$url"
                }
                return mUrl
            }
            return null
        }

        fun addslashes(): String {
            val url = StringBuilder()
            for (i in 0 until this.mParameters!!.size) {
                val parameter = this.mParameters!![i]
                url.append(parameter.key).append("=").append(parameter.value).append("&")
            }
            return url.substring(0, url.length - 1)
        }
    }

    private class Parameter constructor(key: String, value: String) {
        var key: String? = null
        var value: String? = null

        init {
            this.key = key
            this.value = value
        }
    }

    private class SortByKey : Comparator<Any> {
        override fun compare(o1: Any?, o2: Any?): Int {
            val s1: Parameter = o1 as Parameter
            val s2: Parameter = o2 as Parameter
            return Integer.compare(s1.key?.compareTo(s2.key!!)!!, 0)
        }
    }


}