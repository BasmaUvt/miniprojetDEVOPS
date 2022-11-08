package com.example.tunisavia.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tunisavia.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(peoples: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(peoples: User)

    @Query("SELECT * FROM user")
    fun findAll(): List<User>

    @Query("SELECT * FROM user WHERE email = :user_email And password = :user_password")
    fun findUser(user_email: String, user_password: String): User

}
