package com.example.libraryapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.enums.Availability


@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(books: List<Book>)

    @Query("SELECT * FROM Book")
    fun readAll(): LiveData<List<Book>>

    @Query("SELECT * FROM Book WHERE id LIKE :bookId")
    fun readLiveBook(bookId: Int): LiveData<Book>

    @Query("SELECT * FROM Book WHERE subject LIKE :subject and name LIKE :searchQuery")
    fun searchSubjectBooks(
        searchQuery: String,
        subject: String
    ): LiveData<List<Book>>

    @Query("UPDATE book SET availability = :availability WHERE id LIKE :bookId")
    suspend fun updateBookAvailability(bookId: Int, availability: String)

    @Query("SELECT * FROM Book WHERE id LIKE :bookId")
    suspend fun readBook(bookId: Int): Book

    @Query("DELETE FROM Book WHERE id LIKE :bookId")
    suspend fun removeSharedBook(bookId: Int)

    @Query("SELECT * FROM Book WHERE subject LIKE :subject")
    suspend fun readSubjectBooks(subject: String): List<Book>

    @Query("SELECT * FROM Book WHERE id in (:idList) and name LIKE :searchQuery")
    fun searchSharedBooks(
        idList: List<Int>,
        searchQuery: String
    ): LiveData<List<Book>>

    @Insert
    suspend fun insertBook(book: Book): Long

    @Query("SELECT * FROM Book WHERE name LIKE :pattern or subject LIKE :pattern")
    fun searchAllBooks(pattern: String): LiveData<List<Book>>

    @Query("SELECT * FROM Book WHERE  subject in (:subjects)")
    fun getFilteredBooks(
        subjects: List<String>,
    ): LiveData<List<Book>>

    @Query("SELECT * FROM Book WHERE name LIKE :pattern and subject in (:subjects)")
    fun searchFilteredBooks(
        subjects: List<String>,
        pattern: String
    ): LiveData<List<Book>>

    @Query("SELECT COUNT(*) FROM Book WHERE isbnId = :isbnId AND availability LIKE :availability")
    suspend fun getNoOfCopies(isbnId:Int,availability: String = Availability.AVAILABLE.reason): Int

    @Query("SELECT * FROM Book WHERE isbnId=:isbnId AND availability LIKE :availability")
     suspend fun getAvailableCopiesOfBook(isbnId: Int,availability: String = Availability.AVAILABLE.reason):List<Book>

    @Query("SELECT * FROM Book WHERE isbnId=:isbnId")
     fun getLiveCopiesOfBook(isbnId: Int):LiveData<List<Book>>

     @Query("SELECT isbnId FROM book WHERE id =:bookId")
     suspend fun getIsbn(bookId: Int): Int

     @Query("UPDATE book SET isbnId = :isbn WHERE id LIKE :bookId")
     suspend fun setSharedBookIsbn(bookId: Int, isbn: Int)
    @Query("SELECT * FROM Book WHERE isbnId=:isbnId")
     suspend fun getCopiesOfBook(isbnId: Int): List<Book>

}