package com.kgc.tiku.bluebird.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.kgc.tiku.bluebird.R
import com.qmuiteam.qmui.util.QMUIPackageHelper
import com.qmuiteam.qmui.widget.QMUITopBarLayout
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView


class AboutActivity : AppCompatActivity() {

    @JvmField
    @BindView(R.id.topbar)
    internal var mTopBar: QMUITopBarLayout? = null
    @JvmField
    @BindView(R.id.version)
    internal var mVersionTextView: TextView? = null
    @JvmField
    @BindView(R.id.about_list)
    internal var mAboutGroupListView: QMUIGroupListView? = null
    @JvmField
    @BindView(R.id.copyright)
    internal var mCopyrightTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.kgc.tiku.bluebird.R.layout.activity_about)
        ButterKnife.bind(this)
        initTopBar()
        mVersionTextView?.text = QMUIPackageHelper.getAppVersion(this)
        QMUIGroupListView.newSection(this)
            .addItemView(mAboutGroupListView?.createItemView("QQ群：532077356")) {
                jump("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3DMj49xWOy6GfYqoi_s5O85EnTvVvmrCbg")
            }
            .addTo(mAboutGroupListView)

        QMUIGroupListView.newSection(this)
            .setTitle("特别感谢，不分前后")
            .addItemView(mAboutGroupListView?.createItemView("Filmy")) {
                jump("mqqwpa://im/chat?chat_type=wpa&uin=82658186")
            }.addItemView(mAboutGroupListView?.createItemView("无理")) {
                jump("mqqwpa://im/chat?chat_type=wpa&uin=1664205893")
            }.addItemView(mAboutGroupListView?.createItemView("Belief-Life")) {
                jump("mqqwpa://im/chat?chat_type=wpa&uin=55205486")
            }
            .addTo(mAboutGroupListView)
        mCopyrightTextView?.text = "望君适可而止"
    }


    private fun initTopBar() {
        mTopBar?.addLeftBackImageButton()?.setOnClickListener { finish() }
        mTopBar?.setTitle("关于我们")
    }


    private fun checkApkExist(context: Context, packageName: String?): Boolean {
        if (packageName == null || "" == packageName)
            return false
        return try {
            context.packageManager.getApplicationInfo(
                packageName,
                PackageManager.MATCH_UNINSTALLED_PACKAGES
            )
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }

    private fun jump(url: String) {
        if (checkApkExist(this, "com.tencent.mobileqq")) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } else {
            Toast.makeText(this, "无法调起QQ", Toast.LENGTH_SHORT).show()
        }
    }
}