package com.example.libraryapplication.data.repositary

import androidx.lifecycle.LiveData
import com.example.libraryapplication.data.dao.*
import com.example.libraryapplication.data.entities.*

class LibraryRepository(
    private val userDao: UserDao,
    private val borrowDetailDao: BorrowDetailDao,
    private val bookDao: BookDao,
    private val categoryDao: CategoryDao,
    private val donatedBookDao: DonatedBookDao,
    private val mostBorrowedDao: MostBorrowedDao,
    private val favoriteBookDao: FavoriteBookDao,
    private val authenticationDetailDao: AuthenticationDetailDao
) {
    suspend fun insertUsers(users: List<User>) {
        userDao.insertUsers(users)
    }

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }


   suspend fun getUser(userId: Int): User {
        return userDao.getUser(userId)
    }

    suspend fun getAllUser(): List<User> {
        return userDao.readAllUser()
    }

    suspend fun insertBorrowDetail(borrowDetail: BorrowDetail) {
        borrowDetailDao.insertBorrowDetail(borrowDetail)
    }

    suspend fun updateDue(bookId: Int, dueAmount: Int) {
        borrowDetailDao.updateDue(bookId, dueAmount)
    }

    suspend fun deleteBorrowedBook(bookId: Int) {
        borrowDetailDao.deleteBorrowedBook(bookId)
    }

    fun getUserDueBookDetails(userId: Int): LiveData<List<BorrowDetail>> {
        return borrowDetailDao.readUserDueBooks(userId)
    }

    fun getBorrowedBooks(userId: Int): LiveData<List<BorrowDetail>> {
        return borrowDetailDao.getBorrowedBooks(userId)
    }

    fun readAllBooks(): LiveData<List<Book>> {
        return bookDao.readAll()
    }

    fun getAvailableBook(bookId: Int): LiveData<Book> {
        return bookDao.readLiveBook(bookId)
    }

    /*fun searchAllBooks(searchQuery: String): LiveData<List<Book>> {
        return bookDao.searchAllBooks(searchQuery)
    }*/

    fun searchAllBook(searchQuery: String): LiveData<List<Book>> {
        val toSearch: String = searchQuery
        val pattern = "%" + toSearch.replace(" ", "%") + "%"
        return bookDao.searchAllBooks(pattern)
    }

    fun searchSubjectBooks(searchQuery: String, subject: String): LiveData<List<Book>> {
        val toSearch: String = searchQuery
        val pattern = "%" + toSearch.replace(" ", "%") + "%"
        return bookDao.searchSubjectBooks(pattern, subject)
    }

    fun readAllSubjectOfSection(section: String): LiveData<List<Category>> {
        return categoryDao.readAllSubjectOfSection(section)
    }

    fun getAllSharedBookDetails(): LiveData<List<DonatedBook>> {
        return donatedBookDao.readAll()
    }

    /*fun getSubjectBooks(subject: String): LiveData<List<Book>> {
        return bookDao.readSubjectBooks(subject)
    }*/

    suspend fun getSubjectBooks(subject: String): List<Book> {
        return bookDao.readSubjectBooks(subject)
    }

    fun searchSharedBooks(
        idList: List<Int>,
        searchQuery: String
    ): LiveData<List<Book>> {
        val toSearch: String = searchQuery
        val pattern = "%" + toSearch.replace(" ", "%") + "%"
        return bookDao.searchSharedBooks(idList, pattern)
    }

    suspend fun updateBookStatus(bookId: Int, availability: String) {
        bookDao.updateBookAvailability(bookId, availability)
    }

    fun getUserBorrowedBookCount(userId: Int): LiveData<Int> {
        return borrowDetailDao.getUserBorrowedBookCount(userId)
    }

    suspend fun getBook(bookId: Int): Book {
        return bookDao.readBook(bookId)
    }

    fun readUserDonatedBooksLive(userId: Int): LiveData<List<DonatedBook>> {
        return donatedBookDao.readBookFromUserLive(userId)
    }

    suspend fun removeDonatedBookDetail(bookId: Int) {
        donatedBookDao.removeBook(bookId)
    }

    suspend fun removeSharedBook(bookId: Int) {
        bookDao.removeSharedBook(bookId)
    }

    suspend fun insertBooks(books: List<Book>) {
        bookDao.insert(books)
    }

    suspend fun insertCategories(categories: List<Category>) {
        categoryDao.insert(categories)
    }

    suspend fun insertDonatedBooks(donatedBooks: List<DonatedBook>) {
        donatedBookDao.insertDonatedBooks(donatedBooks)
    }

    fun readMostBorrowedBooks(): LiveData<List<MostBorrowed>> {
        return mostBorrowedDao.readMostBorrowedBooks()
    }

    suspend fun getNoOfBorrows(isbnId: Int): Int {
        return mostBorrowedDao.getNoOfBorrows(isbnId)
    }

    suspend fun updateNoOfBorrows(isbnId: Int, count: Int) {
        mostBorrowedDao.updateNoOfBorrows(isbnId, count)
    }

    suspend fun insertAllMostBorrow(mostBorrows: List<MostBorrowed>) {
        mostBorrowedDao.insertAll(mostBorrows)
    }

    suspend fun insertMostBorrow(mostBorrow: MostBorrowed) {
        mostBorrowedDao.insert(mostBorrow)
    }

    suspend fun insertFavorite(favoriteBook: FavoriteBook) {
        favoriteBookDao.insertFavorite(favoriteBook)
    }

    suspend fun deleteFavorite(favoriteBook: FavoriteBook) {
        favoriteBookDao.deleteFavorite(favoriteBook)
    }

    fun isFavorite(favoriteBook: FavoriteBook): LiveData<Int> {
        return favoriteBookDao.isFavorite(favoriteBook.userId, favoriteBook.isbnId)
    }

    fun getFavoriteBooks(userId: Int): LiveData<List<FavoriteBook>> {
        return favoriteBookDao.getFavoriteBooks(userId)
    }

    suspend fun insertSharedBookDetail(donatedBook: DonatedBook) {
        donatedBookDao.insertSharedBookDetail(donatedBook)
    }

    suspend fun insertBook(book: Book): Long {
        return bookDao.insertBook(book)
    }

    suspend fun updateUserDetails(userName: String,
                                  doorNo: String,
                                  landMark: String,
                                  district: String,
                                  streetName: String,
                                  pinCode: String,
                                  userId: Int) {
        userDao.updateUserDetails(userName, doorNo, landMark, district, streetName, pinCode, userId)
    }

    fun getUserDetail(userId: Int): LiveData<User> {
        return userDao.getUserDetail(userId)
    }

    suspend fun updateUserPassword(password: String, userId: Int) {
        authenticationDetailDao.updateUserPassword(password, userId)
    }

    suspend fun insertAuthenticationDetail(authenticationDetail: AuthenticationDetail) {
        authenticationDetailDao.insert(authenticationDetail)
    }

    suspend fun getPassword(userId:Int):String{
       return authenticationDetailDao.getPassword(userId)
    }

   suspend fun updateUserMobileNo(mobileNumber: String,userId: Int) {
        userDao.updateUserMobileNo(mobileNumber,userId)
    }

    fun getFilteredBooks(subjects:List<String>):LiveData<List<Book>>{
        return bookDao.getFilteredBooks(subjects)
    }

    fun searchFilteredBooks(
        subjects: List<String>,
        searchQuery: String,
    ): LiveData<List<Book>>{
        val toSearch: String = searchQuery
        val pattern = "%" + toSearch.replace(" ", "%") + "%"
        return  bookDao.searchFilteredBooks(subjects,pattern)
    }

    suspend fun getNoOfCopies(isbnId: Int): Int {
        return bookDao.getNoOfCopies(isbnId)
    }

    suspend fun getAvailableCopiesOfBook(isbnId: Int): List<Book> {
       return bookDao.getAvailableCopiesOfBook(isbnId)
    }

     fun getLiveCopiesOfBook(isbnId: Int):LiveData<List<Book>> {
        return bookDao.getLiveCopiesOfBook(isbnId)
    }

    suspend fun getIsbn(bookId: Int): Int {
        return bookDao.getIsbn(bookId)
    }

   suspend fun setSharedBookIsbn(bookId: Int, isbn: Int) {
        bookDao.setSharedBookIsbn(bookId, isbn)
    }

   suspend fun getCopiesOfBook(isbnId: Int): List<Book> {
        return bookDao.getCopiesOfBook(isbnId)
    }

   suspend fun readUserDonatedBooks(userId: Int): List<DonatedBook> {
        return donatedBookDao.readBookFromUser(userId)
    }
}