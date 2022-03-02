package com.example.libraryapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.libraryapplication.data.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)


    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT phoneNo FROM user")
    suspend fun readAllUserMobileNo1(): List<String>

    /* @Query("SELECT password FROM User WHERE phoneNo LIKE :mobileNo")
     suspend fun getPassword(mobileNo: String): String*/

    @Query("SELECT * FROM User WHERE phoneNo LIKE :mobileNo")
    suspend fun getUser(mobileNo: String): User

    @Query("SELECT * FROM User WHERE id LIKE :userId")
    suspend fun getUser(userId: Int): User

    @Query("SELECT * FROM User")
    suspend fun readAllUser(): List<User>

    @Query("UPDATE User SET name = :userName,doorNo = :doorNo,pinCode =:pinCode,streetName=:streetName,landMark=:landMark ,district=:district WHERE id LIKE :userId")
    suspend fun updateUserDetails(
        userName: String,
        doorNo: String,
        landMark: String,
        district: String,
        streetName: String,
        pinCode: String,
        userId: Int
    )

    @Query("SELECT * FROM User WHERE id LIKE :userId")
    fun getUserDetail(userId: Int): LiveData<User>

    @Query("UPDATE User SET phoneNo =:mobileNumber WHERE id LIKE :userId")
    suspend fun updateUserMobileNo(mobileNumber: String, userId: Int)


}