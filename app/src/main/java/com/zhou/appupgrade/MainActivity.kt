package com.zhou.appupgrade

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.GsonUtils
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions


/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
class MainActivity: AppCompatActivity() {

    val recyclerView: RecyclerView by lazy { findViewById(R.id.recyclerView) }
    val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    val adapter = AppListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "应用列表"
        initView()
        initData()
    }

    private fun initData() {
        viewModel.localAppListData.observe(this){
            adapter.setNewInstance(it?.toMutableList())
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadLocalAppList()
    }

    private fun initView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { _, _, position ->
            val appItem = adapter.getItem(position)
            AppDetailActivity.start(this, appItem.apiKey, appItem.appKey, appItem.buildIdentifier)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add){
            startActivity(Intent(this, AddActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }


}