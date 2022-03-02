package com.example.libraryapplication.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Book")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val name:String,
    val authorName:String,
    val subject:String,
    val noOfPages:Int,
    val availability:String,
    val bookImage:String,
    val isbnId:Int
):Serializable
