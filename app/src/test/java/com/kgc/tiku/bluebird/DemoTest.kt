package com.kgc.tiku.bluebird

import com.alibaba.fastjson.JSON
import com.kgc.tiku.bluebird.utils.DesUtils
import org.junit.Test
import java.io.File
import java.nio.charset.Charset

class DemoTest {
    @Test
    fun test() {
        val file = File("C:\\Users\\Happi\\Desktop\\JSON.txt")
        val str = file.readText(Charset.forName("UTF-8"))
        val parseObject = JSON.parseObject(str)
        println(DesUtils.decrypt(parseObject.getString("cqList")))
    }
}