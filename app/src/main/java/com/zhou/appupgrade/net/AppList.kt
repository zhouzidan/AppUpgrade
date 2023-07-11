package com.zhou.appupgrade.net

/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
data class AppListRequest(val _api_key: String)
data class AppItemServer(
    val buildType: Int,
    val buildName: String?,
    val buildIcon: String?,
    val appKey: String,
    val buildShortcutUrl: String?,
    val buildIdentifier:String?, //包名
)

data class AppListResponse(val list: List<AppItemServer>?)