package com.example.libraryapplication.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DonatedBook(
    @PrimaryKey
    val bookId:Int,
    val userId:Int
) {
}