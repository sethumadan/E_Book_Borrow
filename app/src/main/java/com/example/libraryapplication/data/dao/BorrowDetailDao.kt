package com.example.libraryapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.libraryapplication.data.entities.BorrowDetail

@Dao
interface BorrowDetailDao {
    @Insert
    suspend fun insertBorrowDetail(borrowDetail: BorrowDetail)

    @Query("SELECT * FROM BorrowDetail")
    fun readAllBorrowDetail(): LiveData<List<BorrowDetail>>

    @Query("SELECT * FROM BorrowDetail WHERE userId LIKE :userId")
    suspend fun readBorrowedBooks(userId: Int): Array<BorrowDetail>

    @Query("UPDATE BorrowDetail SET dueAmount = :dueAmount WHERE bookId = :bookId ")
    suspend fun updateDue(bookId: Int, dueAmount: Int)

    @Query("DELETE FROM BorrowDetail WHERE bookId LIKE :bookId")
    suspend fun deleteBorrowedBook(bookId:Int)

    @Query("SELECT * FROM BorrowDetail WHERE dueAmount NOT LIKE 0 and userId LIKE :userId")
    fun readUserDueBooks(userId: Int): LiveData<List<BorrowDetail>>

    @Query("SELECT * FROM BorrowDetail WHERE userId LIKE :userId")
    fun getBorrowedBooks(userId: Int): LiveData<List<BorrowDetail>>

    @Query("SELECT * FROM BorrowDetail WHERE bookId LIKE :bookId")
    suspend fun getBorrowDetail(bookId: Int): BorrowDetail

    @Query("SELECT COUNT(*) FROM BorrowDetail WHERE userId = :userId")
     fun getUserBorrowedBookCount(userId:Int):LiveData<Int>
}