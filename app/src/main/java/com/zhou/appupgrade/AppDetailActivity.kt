package com.zhou.appupgrade

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.Util
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener3
import com.zhou.appupgrade.net.AppCheckResponseBean
import java.io.File


/**
 * @author guobao.zhou
 * @create date 2023/7/10
 **/
class AppDetailActivity: AppCompatActivity() {

    companion object {
        const val AppKey = "app_key"
        const val ApiKey = "api_key"
        private const val BuildIdentifierKey = "package_name_key"
        fun start(context: Context, apiKey:String, appKey:String, buildIdentifier:String?){
            val intent = Intent(context, AppDetailActivity::class.java)
            intent.putExtra(AppKey, appKey)
            intent.putExtra(ApiKey, apiKey)
            intent.putExtra(BuildIdentifierKey, buildIdentifier)
            context.startActivity(intent)
        }
    }

    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private val recyclerView : RecyclerView by lazy { findViewById(R.id.recyclerView) }
    private val adapter = AppBuildListAdapter()
    val apiKey:String by lazy { intent.getStringExtra(ApiKey) ?: "" }
    val appKey:String by lazy { intent.getStringExtra(AppKey) ?: "" }
    private val buildIdentifier:String? by lazy { intent.getStringExtra(BuildIdentifierKey) }
    private val downloadBtn:Button by lazy { findViewById(R.id.downloadBtn) }
    private var task: DownloadTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            openWeb(item.buildKey)
        }
        downloadBtn.setOnClickListener {
            checkInstallPermission{
                download(viewModel.appCheckResponseBeanData.value ?: return@checkInstallPermission)
            }
        }
        viewModel.appBuildListData.observe(this){ buildList ->
            adapter.setNewInstance(buildList?.toMutableList())
            viewModel.checkUpdate(this,apiKey = apiKey, appKey = appKey, buildIdentifier)
        }
        viewModel.appCheckResponseBeanData.observe(this) { check ->
            if (check?.buildHaveNewVersion == true || !AppUtils.isAppInstalled(buildIdentifier)){
                downloadBtn.text = "有新版本${check?.buildVersion}_${check?.buildVersionNo},点击下载安装"
                downloadBtn.visibility = View.VISIBLE
            }else {
                downloadBtn.visibility = View.GONE
            }
        }
        viewModel.loadAppBuildList(apiKey = apiKey, appKey = appKey)

        Util.enableConsoleLog()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun checkInstallPermission(callback:()->Unit){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pm: PackageManager = packageManager
            //pm.canRequestPackageInstalls() 返回用户是否授予了安装apk的权限
            if(pm.canRequestPackageInstalls()){
                callback.invoke()
            }else{
                //跳转到该应用的安装应用的权限页面
                val packageURI: Uri =
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                //注意这个是8.0新API
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
                startActivityForResult(intent, 101)
            }
        }
    }
    private fun openWeb(buildKey:String){
        MaterialDialog(this)
            .message(text = "即将在浏览器上打开链接，请确认")
            .positiveButton(text = "确认", click = object :DialogCallback{
                override fun invoke(p1: MaterialDialog) {
                    val installUrl = "https://www.pgyer.com/${buildKey}"
                    val viewWebIntent = Intent(Intent.ACTION_VIEW)
                    val uri = Uri.parse(installUrl)
                    viewWebIntent.data = uri
                    startActivity(viewWebIntent)
                }
            })
            .negativeButton(text = "取消").show()
    }

    private fun download(checkBean: AppCheckResponseBean){
        val fileName = "${checkBean.buildVersion}_${checkBean.buildVersionNo}_${checkBean.buildBuildVersion}.apk"
        val parentFile = File(PathUtils.getExternalAppDownloadPath())
        val task = DownloadTask.Builder(checkBean.downloadURL, parentFile)
            .setFilename(fileName) // the minimal interval millisecond for callback progress
            .setMinIntervalMillisCallbackProcess(30) // do re-download even if the task has already been completed in the past.
            .setPassIfAlreadyCompleted(true)
            .setConnectionCount(1)
            .build()
        task.enqueue(downloadListener)
    }

    private var dialog: MaterialDialog? = null
    private var progressBar:ProgressBar? = null
    private fun showDownloadDialog(){
        val customView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_progress,null)
        dialog = MaterialDialog(this).customView(view = customView)
        progressBar = customView.findViewById(R.id.progress)
        val cancelBtn = customView.findViewById<Button>(R.id.cancel_button)
        cancelBtn.setOnClickListener { dialog?.cancel() }
        dialog?.cancelable(false)
        dialog?.cancelOnTouchOutside(false)
        dialog?.show()
    }

    private val downloadListener = object: DownloadListener3() {
        override fun retry(task: DownloadTask, cause: ResumeFailedCause) {

        }

        override fun connected(
            task: DownloadTask,
            blockCount: Int,
            currentOffset: Long,
            totalLength: Long
        ) {

        }

        override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
            progressBar?.setProgress((currentOffset * 100 / totalLength).toInt(), false)
        }

        override fun started(task: DownloadTask) {
            showDownloadDialog()
        }

        override fun completed(task: DownloadTask) {
            dialog?.cancel()
            AppUtils.installApp(task.file)
        }

        override fun canceled(task: DownloadTask) {
            dialog?.cancel()
            ToastUtils.showShort("取消下载")
        }

        override fun error(task: DownloadTask, e: Exception) {
            dialog?.cancel()
            Log.e("TEST", "error ${Log.getStackTraceString(e)}")
            ToastUtils.showShort("下载出错")
        }

        override fun warn(task: DownloadTask) {
            dialog?.cancel()
        }
    }
}