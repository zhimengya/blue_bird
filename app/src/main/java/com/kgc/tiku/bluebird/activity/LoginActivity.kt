package com.kgc.tiku.bluebird.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.kgc.tiku.bluebird.R
import com.kgc.tiku.bluebird.utils.Constant
import com.kgc.tiku.bluebird.utils.HttpUtils
import com.kgc.tiku.bluebird.utils.UrlConstant
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.QMUITopBar
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * 启动类
 */
class LoginActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.btnLogin)
    var btnLogin: Button? = null

    @JvmField
    @BindView(R.id.txtPassword)
    var txtPwd: EditText? = null

    @JvmField
    @BindView(R.id.txtUserName)
    var txtUsername: EditText? = null

    @JvmField
    @BindView(R.id.topbar)
    var mTopBar: QMUITopBar? = null

    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QMUIStatusBarHelper.translucent(this)
        //初始化状态栏
        val root = LayoutInflater.from(this).inflate(R.layout.activity_login, null)
        ButterKnife.bind(this, root)
        pref = PreferenceManager.getDefaultSharedPreferences(this)
        editor = pref?.edit()
        check()
        initTopBar()
        getMe()
        setContentView(root)
    }

    private fun initTopBar() {
        mTopBar?.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        mTopBar?.setTitle(resources.getString(R.string.login_title))
    }

    @OnClick(R.id.btnLogin)
    fun onClicked() {
        when {
            txtUsername!!.text.trim().isEmpty() -> {
                QMUIDialog.MessageDialogBuilder(this).setTitle("提示").setMessage("请输入用户名")
                    .addAction("确定") { dialog, _ -> dialog?.dismiss() }
                    .show()
                txtUsername?.requestFocus()
            }
            txtPwd!!.text.trim().isEmpty() -> {
                QMUIDialog.MessageDialogBuilder(this).setTitle("提示").setMessage("请输入密码")
                    .addAction("确定") { dialog, _ -> dialog?.dismiss() }
                    .show()
                txtPwd?.requestFocus()
            }
            else -> login()
        }
    }

    /**
     * 登陆
     */
    private fun login() {
        val tipDialog = QMUITipDialog.Builder(this)
            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("登陆中")
            .create()
        tipDialog.setCancelable(false)
        tipDialog.show()
        val url = UrlConstant.getOneLoginUrl(txtUsername!!.text.toString(), txtPwd!!.text.toString())
        HttpUtils.okHttpClient().newCall(HttpUtils.buildGet(url!!)).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                var error: QMUITipDialog? = null
                handler.post {
                    error = QMUITipDialog.Builder(this@LoginActivity)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL).setTipWord(e!!.message)
                        .create()
                    error?.setCancelable(false)
                    tipDialog.dismiss()
                    error?.show()
                }
                handler.postDelayed({
                    error?.dismiss()
                }, 1500)
            }

            override fun onResponse(call: Call?, response: Response?) {
                var success: QMUITipDialog? = null
                val html = response!!.body().string()
                val userInfo = JSON.parseObject(html)
                if (userInfo.getIntValue("code") == 1) {
                    //第一次登陆成功之后要切换产品才是真正的登陆（才会写入cookie）
                    val userId = userInfo.getIntValue("userId")
                    val productId = getProductId(userInfo.getJSONArray("productList"));
                    val changeProduct = HttpUtils.buildGet(UrlConstant.getChangeProductUrl(productId, userId))
                    val execute = HttpUtils.okHttpClient().newCall(changeProduct).execute()
                    val json = JSON.parseObject(execute.body().string())
                    if (json.getIntValue("code") != 1) {
                        onFailure(null, IOException(json.getString("msg")))
                        return
                    }
                    handler.post {
                        success = QMUITipDialog.Builder(this@LoginActivity)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                            .setTipWord("欢迎你," + userInfo.getString("userName"))
                            .create()
                        success?.setCancelable(false)
                        tipDialog.dismiss()
                        success?.show()
                    }
                    handler.postDelayed({
                        success?.dismiss()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("productId", productId)
                        intent.putExtra("username", txtUsername?.text.toString())
                        intent.putExtra("password", txtPwd?.text.toString())
                        Constant.userInfo = userInfo
                        rememberMe(txtUsername?.text.toString(), txtPwd?.text.toString())
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right)
                    }, 1000)
                } else {
                    onFailure(null, IOException(userInfo.getString("msg")))
                }
            }
        })
    }

    /**
     * 获取最后一次选中的产品id
     */
    private fun getProductId(json: JSONArray): Int {
        for (i in 0..(json.size - 1)) {
            val j = json.getJSONObject(i)
            if (j.getBooleanValue("isLastLoginProduct"))
                return j.getIntValue("productId")
        }
        return 0
    }

    /**
     * 记住我
     */
    private fun rememberMe(username: String, password: String) {
        editor?.putString("username", username)
        editor?.putString("password", password)
        editor?.commit()
    }

    private fun getMe() {
        txtUsername?.setText(pref?.getString("username", ""))
        txtPwd?.setText(pref?.getString("password", ""))
    }

    private fun check() {
        HttpUtils.okHttpClient().newCall(HttpUtils.buildGet(UrlConstant.CHECK_URL)).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                handler.post {
                    val errorDialog =
                        QMUIDialog.MessageDialogBuilder(this@LoginActivity).setTitle("提示")
                            .setMessage("启动失败，请重新启动试试。如果多次弹出此框，请联系QQ群：532077356")
                            .addAction("确定") { _, _ ->
                                run {
                                    finish()
                                }
                            }
                            .show()
                    errorDialog?.setCancelable(false)
                    errorDialog?.show()
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                val html = response!!.body().string()
                if (html.trim() != "on") {
                    this.onFailure(null, null)
                }
            }

        })
    }
}

