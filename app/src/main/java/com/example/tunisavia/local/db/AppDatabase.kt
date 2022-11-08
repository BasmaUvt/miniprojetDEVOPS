package com.example.tunisavia.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tunisavia.local.converter.Converters
import com.example.tunisavia.local.dao.PostDao
import com.example.tunisavia.local.dao.UserDao
import com.example.tunisavia.entity.User
import com.example.tunisavia.entity.Pilot

@Database(
    entities = [Pilot::class, User::class],
    version = AppDatabase.VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val postDao: PostDao
    abstract val userDao: UserDao

    companion object {
        const val VERSION = 10
        const val NAME = "challenge.db"

        private var instance: AppDatabase? = null
    }
}
