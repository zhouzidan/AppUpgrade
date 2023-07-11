package com.zhou.appupgrade

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhou.appupgrade.local.AppItem

/**
 * @author guobao.zhou
 * @create date 2023/7/7
 **/
class AppListAdapter(): BaseQuickAdapter<AppItem, BaseViewHolder>(R.layout.item_add_app) {
    override fun convert(holder: BaseViewHolder, item: AppItem) {
        holder.setText(R.id.appName, item.buildName)
        val imageView = holder.getView<ImageView>(R.id.image)
        val iconUrl = "https://www.pgyer.com/image/view/app_icons/${item.buildIcon}"
        Glide.with(imageView).load(iconUrl).into(imageView)
        holder.setText(R.id.appKey, "appKey:${item.appKey}")
        holder.setText(R.id.appUrl, "https://www.pgyer.com/${item.buildShortcutUrl}")
    }
}