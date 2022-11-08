package com.example.tunisavia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Pilot {
    @PrimaryKey
    var id: Int? = null
    var name: String? = "alaaa"
    var code: String? = "HAB"
    var endroseement: String? = "C560XL"
    var pic: String? = null

    override fun toString(): String {
        return "Pilot(id=$id, name=$name, pic=$pic)"
    }
}