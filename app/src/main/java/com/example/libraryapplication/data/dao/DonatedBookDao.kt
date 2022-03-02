package com.example.libraryapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.libraryapplication.data.entities.DonatedBook

@Dao
interface DonatedBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonatedBooks(donatedBooks: List<DonatedBook>)

    @Insert
    suspend fun insertSharedBookDetail(donatedBook: DonatedBook)

    @Query("SELECT * FROM DonatedBook")
    fun readAll(): LiveData<List<DonatedBook>>

    @Query("SELECT bookId FROM DonatedBook")
    suspend fun readAllId(): List<Int>

    @Query("DELETE FROM DonatedBook WHERE bookId LIKE :bookId")
    suspend fun removeBook(bookId: Int)

    @Query("SELECT * FROM DonatedBook WHERE userId LIKE :userId")
    fun readBookFromUserLive(userId: Int): LiveData<List<DonatedBook>>

    @Query("SELECT * FROM DonatedBook WHERE userId LIKE :userId")
    suspend fun readBookFromUser(userId: Int): List<DonatedBook>


}