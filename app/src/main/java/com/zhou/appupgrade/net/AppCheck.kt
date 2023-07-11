package com.zhou.appupgrade.net

/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
data class AppCheckResponseBean(
    val buildHaveNewVersion:Boolean,
    val buildVersion: String,
    val downloadURL: String,
    val buildVersionNo:String,
    val buildBuildVersion:String,
    )