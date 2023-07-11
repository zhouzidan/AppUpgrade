package com.zhou.appupgrade.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppItemDao {
    @Query("SELECT * FROM app_item")
    fun getAll(): List<AppItem>

    @Insert
    fun insertAll(vararg items: AppItem)

    @Delete
    fun delete(user: AppItem)

    @Query("DELETE FROM app_item WHERE appKey = :appKey AND apiKey = :apiKey")
    fun delete(apiKey: String, appKey:String)

    @Update
    fun update(vararg items: AppItem)

    @Query("SELECT * FROM app_item WHERE apiKey = :apiKey")
    fun findByApiKey(apiKey: String): List<AppItem>
}