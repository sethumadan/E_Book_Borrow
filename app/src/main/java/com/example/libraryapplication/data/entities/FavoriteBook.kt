package com.example.libraryapplication.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["isbnId","userId"])
data class FavoriteBook(
    val isbnId: Int,
    val userId: Int
)
