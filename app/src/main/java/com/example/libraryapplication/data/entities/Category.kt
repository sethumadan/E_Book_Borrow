package com.example.libraryapplication.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.security.auth.Subject

@Entity(tableName = "Category")
data class Category(
    @PrimaryKey
    val subjectName:String,
    val sectionName:String,
    val subjectImage:String
) {
}