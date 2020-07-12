package com.example.callblocker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class BlockedNumber(

    @PrimaryKey()
    @ColumnInfo(name = "phone_number")
    var number:String,

    @ColumnInfo(name = "name")
    var name:String?

)