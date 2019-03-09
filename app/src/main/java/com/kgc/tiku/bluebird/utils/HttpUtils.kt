package com.kgc.tiku.bluebird.utils

import okhttp3.*
import java.net.Proxy


class HttpUtils {
    companion object {
        private val client = OkHttpClient().newBuilder().cookieJar(object : CookieJar {
            private val cookieStore = HashMap<String, List<Cookie>>()
            override fun saveFromResponse(url: HttpUrl?, cookies: List<Cookie>?) {
                cookieStore.put(url!!.host(), cookies!!);
            }

            override fun loadForRequest(url: HttpUrl?): List<Cookie> {
                val cookies = cookieStore[url!!.host()]
                return cookies ?: ArrayList(0)
            }

        }).proxy(Proxy.NO_PROXY).build()

        init {
        }

        fun okHttpClient(): OkHttpClient {
            return client
        }

        fun buildGet(url: String): Request {
            return Request.Builder().url(url).addHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36"
            )
                .build()
        }

        fun buildPost(url: String, params: Map<String, String>): Request {
            val formBody = FormBody.Builder()
            for (i in params) {
                formBody.add(i.key, i.value)
            }
            return Request.Builder().url(url).addHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36"
            ).post(formBody.build())
                .build()
        }

    }
}