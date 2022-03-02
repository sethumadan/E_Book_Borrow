package com.example.libraryapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.libraryapplication.data.dao.*
import com.example.libraryapplication.data.entities.*

@Database(
    entities = [
        User::class,
        Book::class,
        BorrowDetail::class,
        Category::class,
        DonatedBook::class,
        MostBorrowed::class,
        FavoriteBook::class,
        AuthenticationDetail::class
    ], version = 1, exportSchema = true
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract val bookDao: BookDao
    abstract val categoryDao: CategoryDao
    abstract val userDao: UserDao
    abstract val donatedBookDao: DonatedBookDao
    abstract val borrowDetailDao: BorrowDetailDao
    abstract val mostBorrowedDao: MostBorrowedDao
    abstract val favoriteBookDao: FavoriteBookDao
    abstract val authenticationDetailDao: AuthenticationDetailDao
}