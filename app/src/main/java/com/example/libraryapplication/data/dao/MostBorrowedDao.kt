package com.example.libraryapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.libraryapplication.data.entities.MostBorrowed

@Dao
interface MostBorrowedDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(mostBorrows: List<MostBorrowed>)

    @Insert
    suspend fun insert(mostBorrows: MostBorrowed)

    @Query("SELECT * FROM MostBorrowed WHERE noOfBorrows NOT LIKE 0 ORDER BY noOfBorrows DESC LIMIT 20 ")
    fun readMostBorrowedBooks(): LiveData<List<MostBorrowed>>

    @Query("SELECT noOfBorrows FROM MostBorrowed WHERE isbnId=:isbnId")
   suspend fun getNoOfBorrows(isbnId: Int): Int

    @Query("UPDATE MostBorrowed SET noOfBorrows =:count WHERE isbnId=:isbnId")
    suspend fun updateNoOfBorrows(isbnId: Int, count: Int)
}
