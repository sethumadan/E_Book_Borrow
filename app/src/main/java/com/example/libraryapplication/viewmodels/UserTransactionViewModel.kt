package com.example.libraryapplication.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapplication.data.entities.*
import com.example.libraryapplication.data.repositary.LibraryRepository
import com.example.libraryapplication.enums.Availability
import com.example.libraryapplication.ui.utils.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class UserTransactionViewModel @Inject constructor(private val libraryRepository: LibraryRepository) :
    ViewModel() {
    private var _balanceDueAmount = 0
    val initialDueAmount = 10
    val initialBookTime = 10
    val maxBorrowLimit = 4
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    fun getUserDueBookDetails(user: User) = libraryRepository.getUserDueBookDetails(user.id)
    private lateinit var currentBorrowDetail: BorrowDetail

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertBorrowedDetail(book: Book, userId: Int) {
        val borrowDate: LocalDate = LocalDate.now()
        val returnDate: LocalDate = borrowDate.plusDays(initialBookTime.toLong())
        val borrowDetail = BorrowDetail(
            book.id,
            userId,
            formatter.format(borrowDate).toString(),
            formatter.format(returnDate).toString(),
            initialDueAmount
        )
        currentBorrowDetail = borrowDetail
        viewModelScope.launch(Dispatchers.IO) { libraryRepository.insertBorrowDetail(borrowDetail) }
    }

    fun getCurrentBorrowedDetail(): BorrowDetail {
        return currentBorrowDetail
    }

    fun updateReturnDetails(borrowDetail: BorrowDetail) {
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.updateBookStatus(
                borrowDetail.bookId,
                Availability.AVAILABLE.reason
            )
        }
        viewModelScope.launch(Dispatchers.IO) { libraryRepository.deleteBorrowedBook(borrowDetail.bookId) }
    }

    fun updateBorrowDetails(book: Book, userId: Int) {
        insertBorrowedDetail(book, userId)
        viewModelScope.launch {
            libraryRepository.updateBookStatus(
                book.id,
                Availability.UNAVAILABLE.reason
            )
        }
    }


     fun updateDefaultUserDetails(book: Book, userId: Int) {
       viewModelScope.launch(Dispatchers.IO) {
           libraryRepository.insertBorrowDetail(
               BorrowDetail(
                   book.id,
                   userId,
                   DateFormatter.dateFormatter(LocalDate.now().minusDays(11)),
                   DateFormatter.dateFormatter(LocalDate.now().minusDays(5)),
                   50
               )
           )
           libraryRepository.updateBookStatus(
               book.id,
               Availability.UNAVAILABLE.reason
           )

           libraryRepository.updateNoOfBorrows(book.isbnId, 1)
       }
    }

    fun getUserBalanceDueAmount(): Int {
        return _balanceDueAmount
    }

    fun getUserDonatedBooksLive(user: User): LiveData<List<DonatedBook>> {
        return libraryRepository.readUserDonatedBooksLive(user.id)
    }

    suspend fun getUserDonatedBooks(user: User):List<DonatedBook> {
        return libraryRepository.readUserDonatedBooks(user.id)
    }

    fun getUserBorrowDetail(user: User): LiveData<List<BorrowDetail>> {
        return libraryRepository.getBorrowedBooks(user.id)

    }

    fun removeDonatedBookDetails(bookId: Int) {
        viewModelScope.launch(Dispatchers.IO) { libraryRepository.removeDonatedBookDetail(bookId) }
    }

    fun insertSharedBookDetail(userId: Int, BookId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            libraryRepository.insertSharedBookDetail(DonatedBook(BookId, userId))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDueAmount(date: String, bookId: Int) {
        val reversedDate=DateFormatter.dayToYear(date)
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datetime = LocalDate.parse(reversedDate, pattern)
        var dueAmount = 0
        val noOfDays = LocalDate.now().dayOfYear - datetime.dayOfYear
        if (noOfDays > 0) {
            dueAmount = noOfDays * 10
            _balanceDueAmount = _balanceDueAmount.plus(dueAmount)
        }
        viewModelScope.launch { libraryRepository.updateDue(bookId, dueAmount) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDueAmount(borrowDetails: List<BorrowDetail>) {
        _balanceDueAmount = 0
        for (borrowedBookDetail in borrowDetails) {
            calculateDueAmount(borrowedBookDetail.returnDate, borrowedBookDetail.bookId)
        }
    }

    fun getBorrowBookCount(user: User): LiveData<Int> {
        Log.d("Login", "User inside fun----------->${user.name}")
        return libraryRepository.getUserBorrowedBookCount(user.id)
    }

    fun insertFavorite(favoriteBook: FavoriteBook) {
        viewModelScope.launch(Dispatchers.IO) { libraryRepository.insertFavorite(favoriteBook) }
    }

    fun deleteFavorite(favoriteBook: FavoriteBook) {
        viewModelScope.launch(Dispatchers.IO) { libraryRepository.deleteFavorite(favoriteBook) }
    }

    fun isFavorite(favoriteBook: FavoriteBook): LiveData<Int> {
        return libraryRepository.isFavorite(favoriteBook)
    }

    fun getFavoriteBooks(userId: Int): LiveData<List<FavoriteBook>> {
        return libraryRepository.getFavoriteBooks(userId)
    }
}

