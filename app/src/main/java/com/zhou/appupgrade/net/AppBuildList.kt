package com.zhou.appupgrade.net

/**
 * @author guobao.zhou
 * @create date 2023/7/10
 **/
class AppBuildItem(
    val buildName: String,
    val buildVersion: String, //版本号, 默认为1.0 (是应用向用户宣传时候用到的标识，例如：1.1、8.2.1等。)
    val buildCreated: String, //应用上传时间
    val buildFileName: String, // App文件名
    val buildType:Int, // 应用类型（1:iOS; 2:Android）
    val buildKey: String, // Build Key是唯一标识应用的索引ID
    val buildVersionNo:String, // version code
    val buildBuildVersion:Int?, // 使用蒲公英生成的自增 Build 版本号
)


data class AppBuildListResponse(val list: List<AppBuildItem>?)