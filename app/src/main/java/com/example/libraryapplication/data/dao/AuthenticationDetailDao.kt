package com.example.libraryapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.libraryapplication.data.entities.AuthenticationDetail

@Dao
interface AuthenticationDetailDao {

    @Insert
    suspend fun insert(authenticationDetail:AuthenticationDetail)

    @Query("SELECT password FROM AuthenticationDetail WHERE userId LIKE :userId")
    suspend fun getPassword(userId:Int):String

    @Query("UPDATE AuthenticationDetail SET password=:password WHERE userId LIKE :userId")
    suspend fun updateUserPassword(password: String, userId: Int)
}