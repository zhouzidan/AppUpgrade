package com.zhou.appupgrade.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "app_item")
data class AppItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "buildName") val buildName: String?,
    @ColumnInfo(name = "buildIcon") val buildIcon: String?,
    @ColumnInfo(name = "buildShortcutUrl") val buildShortcutUrl: String?,
    @ColumnInfo(name = "appKey") val appKey: String,
    @ColumnInfo(name = "apiKey") val apiKey: String,
    @ColumnInfo(name = "buildIdentifier") val buildIdentifier:String?,
)

