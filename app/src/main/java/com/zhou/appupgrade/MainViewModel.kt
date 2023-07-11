package com.zhou.appupgrade

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.GsonUtils
import com.zhou.appupgrade.local.AppItem
import com.zhou.appupgrade.local.Local
import com.zhou.appupgrade.net.AppBuildItem
import com.zhou.appupgrade.net.AppCheckResponseBean
import com.zhou.appupgrade.net.AppItemServer
import com.zhou.appupgrade.net.Cloud
import kotlinx.coroutines.launch


/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
class MainViewModel: ViewModel() {
    val appListData = MutableLiveData<List<AppItemServer>?>()
    val localAppListData = MutableLiveData<List<AppItem>?>()
    val appBuildListData = MutableLiveData<List<AppBuildItem>?>()
    val appCheckResponseBeanData = MutableLiveData<AppCheckResponseBean?>()

    fun loadAppList(apiKey:String){
        viewModelScope.launch {
            val appListResponse = Cloud.getAppList(mutableMapOf<String, String>().apply {
                put("_api_key", apiKey)
            })
            Log.e("TEST", "${GsonUtils.toJson(appListResponse)}")
            appListData.postValue(appListResponse.data?.list?.filter { it.buildType == 2 })
        }
    }

    fun loadLocalAppList(){
        viewModelScope.launch {
            val list = Local.loadAll()
            localAppListData.postValue(list)
        }
    }

    fun loadAppBuildList(apiKey: String, appKey:String){
        viewModelScope.launch {
            val list = Cloud.getAppBuilds(apiKey, appKey)
            appBuildListData.postValue(list.data?.list?.filter { it.buildType == 2 })
        }
    }

    fun getAppVersionName(context: Context, packageName:String?):String?{
        return kotlin.runCatching {
            if (packageName == null) return@runCatching null
            context.packageManager.getPackageInfo(packageName,0).versionName
        }.getOrNull()
    }

    fun getAppVersionCode(context: Context, packageName: String?):Int? {
        return kotlin.runCatching {
            if (packageName == null) return@runCatching null
            context.packageManager.getPackageInfo(packageName,0).versionCode
        }.getOrNull()
    }


    fun checkUpdate(context: Context, apiKey: String, appKey: String, buildIdentifier:String?) {
        val versionName = getAppVersionName(context, buildIdentifier)
        val versionCode = getAppVersionCode(context, buildIdentifier)
        val buildBuildVersion = appBuildListData.value?.firstOrNull { it.buildVersion == versionName && it.buildVersionNo == "$versionCode" }?.buildBuildVersion
        viewModelScope.launch {
            val appCheckResponseBean = Cloud.checkNewVersion(apiKey, appKey, versionName, buildBuildVersion)
            appCheckResponseBeanData.postValue(appCheckResponseBean.data)
        }
    }

}