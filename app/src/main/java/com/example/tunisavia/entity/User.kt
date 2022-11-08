package com.example.tunisavia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class User {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var _id: String? = null
    @SerializedName("full_name")
    @Expose
    var fullName: String? = null
    var email: String? = null
    var password: String? = null
    var type: String? = null
    var pic: String? = null
    override fun toString(): String {
        return "User(id=$id, fullName=$fullName, email=$email, password=$password, type=$type, pic=$pic)"
    }
}