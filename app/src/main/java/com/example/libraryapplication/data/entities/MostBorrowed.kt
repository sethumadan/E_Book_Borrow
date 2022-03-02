package com.example.libraryapplication.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MostBorrowed")
data class MostBorrowed(
    @PrimaryKey
    val isbnId : Int,
    val noOfBorrows:Int
) {
}