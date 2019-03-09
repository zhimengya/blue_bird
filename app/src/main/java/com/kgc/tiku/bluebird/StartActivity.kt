package com.kgc.tiku.bluebird

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kgc.tiku.bluebird.activity.LoginActivity

/**
 * 启动类
 */
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right)
    }
}