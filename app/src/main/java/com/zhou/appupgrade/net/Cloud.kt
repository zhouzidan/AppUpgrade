package com.zhou.appupgrade.net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.security.MessageDigest

/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
object Cloud {
    private val okHttpClient = OkHttpClient.Builder().build()
    private val retrofit: Retrofit = Retrofit.Builder().client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://www.pgyer.com/apiv2/")
        .build()
    val pgyerService = retrofit.create(PgyerService::class.java)
    suspend fun getAppDetail(requestBean: AppDetailRequestBean) = pgyerService.getAppDetail(requestBean)
    suspend fun checkNewVersion(apiKey: String,  appKey:String, buildVersion:String?, buildBuildVersion:Int?) = pgyerService.checkNewVersion(apiKey, appKey, buildVersion, buildBuildVersion)
    suspend fun getAppList(map: Map<String, String>) = pgyerService.getAppList(map)
    suspend fun getAppBuilds(apiKey: String, appKey: String) = pgyerService.getAppBuilds(apiKey, appKey)
}
interface PgyerService{
    @POST("app/view")
    suspend fun getAppDetail(@Body requestBean: AppDetailRequestBean): AppDetailResponseBean

    @FormUrlEncoded
    @POST("app/check")
    suspend fun checkNewVersion(@Field("_api_key") apiKey: String, @Field("appKey") appKey:String, @Field("buildVersion") buildVersion:String?, @Field("buildBuildVersion") buildBuildVersion:Int?): BaseResponseBean<AppCheckResponseBean>

    @FormUrlEncoded
    @POST("app/listMy")
    suspend fun getAppList(@FieldMap map: Map<String, String>): BaseResponseBean<AppListResponse>

    @FormUrlEncoded
    @POST("app/builds")
    suspend fun getAppBuilds(@Field("_api_key") apiKey:String, @Field("appKey") appKey:String): BaseResponseBean<AppBuildListResponse>

}

class BaseResponseBean<T>(val code:Int, val message:String?, val data:T?) {
    fun isSuccess():Boolean = code == 0
}