package com.example.libraryapplication.di

import android.content.Context
import androidx.room.Room
import com.example.libraryapplication.data.database.LibraryDatabase
import com.example.libraryapplication.data.repositary.LibraryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        LibraryDatabase::class.java,
        "library_database"
    ).build()


    @Singleton
    @Provides
    fun provideUserRepository(
        db: LibraryDatabase
    ) = LibraryRepository(
        db.userDao,
        db.borrowDetailDao,
        db.bookDao,
        db.categoryDao,
        db.donatedBookDao,
        db.mostBorrowedDao,
        db.favoriteBookDao,
        db.authenticationDetailDao
    )
}