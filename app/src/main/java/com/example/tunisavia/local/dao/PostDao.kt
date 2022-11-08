package com.example.tunisavia.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tunisavia.entity.Pilot

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(peoples: List<Pilot>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(peoples: Pilot)

    @Query("SELECT * FROM pilot")
    fun findAll(): List<Pilot>

    @Query("SELECT COUNT(id) FROM pilot")
    fun countPost(): Int
}
