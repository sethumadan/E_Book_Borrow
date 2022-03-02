package com.example.libraryapplication.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "User")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val name:String,
    val phoneNo:String,
    @Embedded
    val address:Address
):Serializable

data class Address(
    val doorNo:String,
    val landMark:String,
    val streetName:String,
    val district:String,
    val pinCode:String
)