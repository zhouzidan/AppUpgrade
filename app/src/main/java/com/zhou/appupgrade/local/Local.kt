package com.zhou.appupgrade.local

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import com.zhou.appupgrade.net.AppItemServer

/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
object Local {
    var db :AppDatabase? = null
    @SuppressLint("StaticFieldLeak")
    private var context: Context? = null
    fun setContext(context: Context){
        this.context = context
    }
    private fun getDB():AppDatabase? {
        if (db == null){
            db = Room.databaseBuilder(
                context!!,
                AppDatabase::class.java, "database-name"
            ).allowMainThreadQueries()
                .build()
        }
        return db
    }

    fun save(item: AppItemServer, apiKey:String){
        val appItem = AppItem(
            buildIcon = item.buildIcon,
            buildName = item.buildName,
            buildShortcutUrl = item.buildShortcutUrl,
            appKey = item.appKey,
            apiKey = apiKey,
            buildIdentifier = item.buildIdentifier
        )
        getDB()?.userDao()?.insertAll(appItem)
    }

    fun loadAll():List<AppItem>?{
        return getDB()?.userDao()?.getAll()
    }

    fun delete(apiKey: String, appKey:String){
        getDB()?.userDao()?.delete(apiKey, appKey)
    }


}