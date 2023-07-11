package com.zhou.appupgrade.net

/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
data class AppDetailRequestBean(val _api_key:String, val appKey:String)
data class AppDetailResponseBean(val buildName:String, val iconUrl: String)