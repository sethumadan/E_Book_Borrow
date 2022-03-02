package com.example.libraryapplication.viewmodels

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.data.entities.Category
import com.example.libraryapplication.data.entities.DonatedBook
import com.example.libraryapplication.data.entities.MostBorrowed
import com.example.libraryapplication.data.repositary.LibraryRepository
import com.example.libraryapplication.viewmodels.response.Report
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookManagementViewModel @Inject constructor(
    private var libraryRepository: LibraryRepository
) : ViewModel() {

    private var filteredSubjectListInSearch = arrayListOf<String>()

    fun isSubjectListEmpty(): Boolean {
        return filteredSubjectListInSearch.isEmpty()
    }

    fun addSubject(subject: String) {
        filteredSubjectListInSearch.add(subject)
    }

    fun emptySubjectList() {
        filteredSubjectListInSearch = arrayListOf()
    }

    fun removeSubject(subject: String) {
        filteredSubjectListInSearch.remove(subject)
    }

    fun getSubjectBooks(): List<String> {
        return filteredSubjectListInSearch
    }

    fun getAllBooks() = libraryRepository.readAllBooks()

    suspend fun getNoOfBorrows(isbnId: Int): Int {
        /*   Log.d("isbn", "isbn  $isbnId")
           Log.d("isbn", "${libraryRepository.getNoOfBorrows(isbnId)}")
           Log.d("isbn", "${libraryRepository.getNoOfBorrows(isbnId)}")*/
        return libraryRepository.getNoOfBorrows(isbnId)
    }

    fun getCategoryDetails(section: String): LiveData<List<Category>> {
        return libraryRepository.readAllSubjectOfSection(section)
    }

    suspend fun getUniqueIsbnBooks(books: List<Book>): List<Book> {
        val bookList = arrayListOf<Book>()
        var isbnId = -1
        books.forEach { book ->
            if (isbnId != book.isbnId) {
                getAvailableCopyOfBook(book.isbnId)
                    ?.let { it1 -> bookList.add(it1) }
                isbnId = book.isbnId
            }
        }
        return bookList
    }

    suspend fun getAvailableCopyOfBook(isbnId: Int): Book? {
        val books = libraryRepository.getAvailableCopiesOfBook(isbnId)
        Log.d("copies", "isbn--->$isbnId        ${libraryRepository.getCopiesOfBook(isbnId)}")
        return if (books.isNullOrEmpty()) {
            return if (libraryRepository.getCopiesOfBook(isbnId).isNotEmpty()) {
                libraryRepository.getCopiesOfBook(isbnId)[0]
            } else {
                null
            }
        } else books[0]
    }

    fun getLiveCopiesOfBook(isbnId: Int): LiveData<List<Book>> {
        return libraryRepository.getLiveCopiesOfBook(isbnId)
    }

    suspend fun getBook(bookId: Int): Book {
        return libraryRepository.getBook(bookId)
    }

    /* fun getBook(bookId: Int): Book {
         return runBlocking {
             libraryRepository.getBook(bookId)
         }
     }*/

    suspend fun getIsbn(bookId: Int): Int {
        return libraryRepository.getIsbn(bookId)
    }

    /*fun getLiveBook(bookId: Int): LiveData<Book> {
        return libraryRepository.getAvailableBook(bookId)
    }*/

    fun getSharedBookDetails(): LiveData<List<DonatedBook>> {
        return libraryRepository.getAllSharedBookDetails()
    }

    fun insertBooks(books: List<Book>) {
        viewModelScope.launch(Dispatchers.IO) { libraryRepository.insertBooks(books) }
    }

    fun insertCategories(categories: List<Category>) {
        viewModelScope.launch(Dispatchers.IO) { libraryRepository.insertCategories(categories) }
    }

    suspend fun getSubjectsBooks(subject: String = "All Books"): List<Book> {
        return libraryRepository.getSubjectBooks(subject)
    }

    suspend fun getNoOfCopies(isbnId: Int): Int {
        return libraryRepository.getNoOfCopies(isbnId)
    }

    fun searchAllBooks(searchQuery: String): LiveData<List<Book>> {
        return libraryRepository.searchAllBook(searchQuery)
    }

    fun searchSubjectBooks(searchQuery: String, subject: String): LiveData<List<Book>> {
        return libraryRepository.searchSubjectBooks(searchQuery, subject)
    }

    fun searchSharedBooks(searchQuery: String, idList: List<Int>): LiveData<List<Book>> {
        Log.d("BookList", "search shared Book ${idList.size}")
        return libraryRepository.searchSharedBooks(idList, searchQuery)
    }

    fun getMostBorrowedBooks(): LiveData<List<MostBorrowed>> {
        return libraryRepository.readMostBorrowedBooks()
    }

    fun insertMostBorrows(mostBorrowList: List<MostBorrowed>) {
        viewModelScope.launch(Dispatchers.IO) { libraryRepository.insertAllMostBorrow(mostBorrowList) }
    }

    fun updateNoOfBorrows(isbnId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.updateNoOfBorrows(
                isbnId,
                getNoOfBorrows(isbnId).plus(1)
            )
        }
    }

    suspend fun insertBook(book: Book): Long {
        return libraryRepository.insertBook(book)
    }

    /*fun insertBook(book: Book): Long {
        return runBlocking { libraryRepository.insertBook(book) }
    }*/

    fun removeDonatedBook(bookId: Int) {
        viewModelScope.launch(Dispatchers.IO) { libraryRepository.removeSharedBook(bookId) }
    }

    fun insertMostBorrow(bookId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.insertMostBorrow(
                MostBorrowed(
                    bookId,
                    0
                )
            )
        }
    }

    fun searchFilteredBooks(
        subjects: List<String>,
        searchQuery: String,
    ): LiveData<List<Book>> {
        return libraryRepository.searchFilteredBooks(subjects, searchQuery)
    }

    fun getFilteredBooks(subjects: List<String>): LiveData<List<Book>> {
        return libraryRepository.getFilteredBooks(subjects)
    }

    fun isValidBook(
        bookName: String,
        subject: String?,
        authorName: String,
        noOfPages: String,
        bookImage: String?
    ): Report.BookCheckResponse {
        Log.d("inputCheck", "bookImage------------>$bookImage")
        return when {
            bookImage == null -> Report.BookCheckResponse.SELECT_IMAGE
            inputCheck(
                bookName,
                subject,
                authorName,
                noOfPages,
            ) -> Report.BookCheckResponse.UN_FILLED_ROWS
            !isValidNoOfPages(noOfPages) -> Report.BookCheckResponse.INVALID_PAGE_NO
            else -> Report.BookCheckResponse.SUCCESS
        }
    }

    private fun isValidNoOfPages(noOfPages: String): Boolean {
        return (noOfPages.isDigitsOnly() && !hasOnlyZeros(noOfPages))
    }

    private fun hasOnlyZeros(noOfPages: String): Boolean {
        noOfPages.forEach {
            if (it != '0') {
                return false
            }
        }
        return true
    }

    private fun inputCheck(
        bookName: String,
        subject: String?,
        authorName: String,
        noOfPages: String,
    ): Boolean {
        Log.d(
            "inputCheck", "bookname------->$bookName" +
                    "\nsubject-------->$subject" +
                    "\nauthorName------->$authorName" +
                    "\nnoOfPages---------->$noOfPages"
        )
        return bookName.isEmpty() || subject == null || authorName.isEmpty() || noOfPages.isEmpty()
    }

    fun setSharedBookIsbn(bookId: Int) {
        viewModelScope.launch {
            libraryRepository.setSharedBookIsbn(bookId, bookId)
        }
    }
}

