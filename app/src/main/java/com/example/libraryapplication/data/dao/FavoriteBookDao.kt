package com.example.libraryapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.libraryapplication.data.entities.FavoriteBook

@Dao
interface FavoriteBookDao {
    @Insert
    suspend fun insertFavorite(favoriteBook: FavoriteBook)

    @Delete
    suspend fun deleteFavorite(favoriteBook: FavoriteBook)

    @Query("SELECT * FROM FavoriteBook WHERE userId LIKE :userId")
    fun getFavoriteBooks(userId: Int): LiveData<List<FavoriteBook>>

    @Query("SELECT EXISTS(SELECT * FROM FavoriteBook WHERE userId LIKE :userId AND isbnId LIKE :isbnId)")
    fun isFavorite(userId: Int,isbnId:Int): LiveData<Int>
}