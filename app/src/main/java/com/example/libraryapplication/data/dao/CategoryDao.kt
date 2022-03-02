package com.example.libraryapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.libraryapplication.data.entities.Category

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: List<Category>)

    @Query("SELECT * FROM Category ")
     fun readAll(): LiveData<List<Category>>

    @Query("SELECT subjectImage FROM Category WHERE SectionName LIKE :section")
     fun readSection(section: String): LiveData<List<String>>

    @Query("SELECT subjectName FROM Category")
    suspend fun readAllSubject(): List<String>

    @Query("SELECT subjectName FROM category WHERE sectionName LIKE :section")
    suspend fun readSubject(section: String): List<String>

    @Query("SELECT * FROM Category WHERE sectionName LIKE :section")
    fun readAllSubjectOfSection(section:String):LiveData<List<Category>>
}