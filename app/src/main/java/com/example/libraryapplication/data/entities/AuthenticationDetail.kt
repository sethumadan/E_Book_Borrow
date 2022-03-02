package com.example.libraryapplication.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AuthenticationDetail")
data class AuthenticationDetail(
    @PrimaryKey
    val userId: Int,
    val password: String
)