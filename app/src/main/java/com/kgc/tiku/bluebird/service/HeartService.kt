package com.kgc.tiku.bluebird.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.alibaba.fastjson.JSON
import com.kgc.tiku.bluebird.utils.HttpUtils
import com.kgc.tiku.bluebird.utils.UrlConstant

/**
 * 心跳检测
 */
class HeartService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Thread {
            while (true) {
                Thread.sleep(5000)
                val request = HttpUtils.buildGet(UrlConstant.getExamUrl())
                val response = HttpUtils.okHttpClient().newCall(request).execute()
                val result = JSON.parseObject(response.body().string())
                if (result.getIntValue("code") == 0) {
                    //发送广播
                    val broadcast = Intent()
                    broadcast.putExtra("code", 0)
                    broadcast.action = "com.kgc.tiku.bluebird.HeartService"
                    sendBroadcast(broadcast)
                    break
                }
            }
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}