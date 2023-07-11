package com.zhou.appupgrade

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhou.appupgrade.net.AppBuildItem

/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
class AppBuildListAdapter(): BaseQuickAdapter<AppBuildItem, BaseViewHolder>(R.layout.item_app_build_item) {
    override fun convert(holder: BaseViewHolder, item: AppBuildItem) {
        holder.setText(R.id.buildFileName, "${item.buildFileName}")
        holder.setText(R.id.buildCreated, item.buildCreated)
    }
}