package com.zhou.appupgrade

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.DialogCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.zhou.appupgrade.local.Local
import com.zhou.appupgrade.net.AppItemServer

/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
class AddActivity: AppCompatActivity() {

    private val REQUEST_CODE_SCAN_ONE = 1101

    lateinit var cameraPermissionActivityLauncher: ActivityResultLauncher<Array<String>>
    val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private val adapter = AddAppListAdapter()
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recyclerView) }

    private val addApiKey:Button by lazy { findViewById(R.id.addApiKey) }

    private var apiKey:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            showConfirmDialog(item)
        }

        addApiKey.setOnClickListener {
            cameraPermissionActivityLauncher.launch(arrayOf(/*Manifest.permission.READ_EXTERNAL_STORAGE,*/Manifest.permission.CAMERA))
        }

        cameraPermissionActivityLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
                if (map.filter { !it.value }.isEmpty()) {
                    startScan()
                } else {
                    ToastUtils.showLong("请授权：相机权限")
                }
            }
        viewModel.appListData.observe(this){
            adapter.setNewInstance(it?.toMutableList())
        }
    }
    
    private fun showConfirmDialog(item: AppItemServer){
        MaterialDialog(this)
            .title(text = "请确认")
            .message(text = "请确认添加应用 ${item.buildName}(${item.appKey}) 到关注列表?")
            .positiveButton(text = "确认", click = object :DialogCallback{
                override fun invoke(p1: MaterialDialog) {
                    apiKey?.let {
                        Local.delete(it, item.appKey)
                        Local.save(item, it)
                        finish()
                    }
                }
            })
            .negativeButton(text = "取消").show()
    }

    private fun startScan(){
        val options = HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).setViewType(1).setErrorCheck(true).create()
        ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCAN_ONE && resultCode == RESULT_OK && data != null){
            val errorCode: Int = data.getIntExtra(ScanUtil.RESULT_CODE, ScanUtil.SUCCESS)
            if (errorCode == ScanUtil.SUCCESS) {
                val hmsScan = data.getParcelableExtra(ScanUtil.RESULT) as? HmsScan
                hmsScan?.let {
                    addApiKey.text = it.getShowResult()
                    viewModel.loadAppList(it.getShowResult())
                    apiKey = it.getShowResult()
                }
                Log.e("TEST", "${GsonUtils.toJson(hmsScan)}")
            }
            if (errorCode == ScanUtil.ERROR_NO_READ_PERMISSION) {
                // 无文件权限，请求文件权限
                Log.e("TEST", "无文件权限，请求文件权限")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}