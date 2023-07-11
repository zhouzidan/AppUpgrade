package com.zhou.appupgrade

import android.app.Application
import com.zhou.appupgrade.local.Local

/**
 * @author guobao.zhou
 * @create date 2023/7/10
 **/
class App:Application() {
    override fun onCreate() {
        super.onCreate()
        Local.setContext(this)
    }
}