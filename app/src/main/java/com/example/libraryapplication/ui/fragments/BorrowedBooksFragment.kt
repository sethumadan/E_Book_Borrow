package com.example.libraryapplication.ui.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.data.entities.BorrowDetail
import com.example.libraryapplication.databinding.FragmentBorrowedBooksBinding
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.activity.MainActivity
import com.example.libraryapplication.ui.adapter.BorrowedBooksAdapter
import com.example.libraryapplication.ui.adapter.model.BorrowedBookModel
import com.example.libraryapplication.ui.fragments.args.FromIcon
import com.example.libraryapplication.ui.utils.DateFormatter
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class BorrowedBooksFragment : Fragment(), BorrowedBooksAdapter.ClickListener {
    private lateinit var binding: FragmentBorrowedBooksBinding
    private lateinit var borrowedBookRecyclerView: RecyclerView
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var args: BorrowedBooksFragmentArgs
    private lateinit var adapter: BorrowedBooksAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_borrowed_books, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setActionBarTitle(args.title)
        setBorrowedBookRecyclerView()
        binding.borrowedBooksEmptyText.text = resources.getString(R.string.no_due_books)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setBorrowedBookRecyclerView() {
        borrowedBookRecyclerView = binding.borrowedBooksRecycler
        adapter = BorrowedBooksAdapter()
        borrowedBookRecyclerView.adapter = adapter
        borrowedBookRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setClickListener(this)
        if (args.title == FromIcon.DUE_BOOKS.reason) {
            setDueBooks()
        } else {
            setBorrowedBooks()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setBorrowedBooks() {
        userTransactionViewModel.getUserBorrowDetail(authenticationViewModel.getUser())
            .observe(viewLifecycleOwner) {
                checkListEmpty(it)
                lifecycleScope.launch {
                    adapter.setBorrowDetail(it, getBorrowedBookModel(it), args.title)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDueBooks() {
        userTransactionViewModel.getUserDueBookDetails(authenticationViewModel.getUser())
            .observe(viewLifecycleOwner) { borrowDetail ->
                checkListEmpty(borrowDetail)
                lifecycleScope.launch {
                    adapter.setBorrowDetail(
                        borrowDetail,
                        getBorrowedBookModel(borrowDetail),
                        args.title
                    )
                }
            }
    }

    private fun checkListEmpty(list: List<BorrowDetail>) {
        if (list.isEmpty()) {
            binding.borrowedBooksEmptyText.visibility = View.VISIBLE
            binding.borrowedBooksEmptyImage.visibility = View.VISIBLE
            if (args.title == FromIcon.DUE_BOOKS.reason) {
                binding.borrowedBooksEmptyText.text = resources.getString(R.string.no_due_books)
            } else binding.borrowedBooksEmptyText.text = resources.getString(R.string.no_books)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getBorrowedBookModel(borrowDetails: List<BorrowDetail>): List<BorrowedBookModel> {
        var bookImage: String
        var bookName: String
        val borrowedBookModelList = arrayListOf<BorrowedBookModel>()
        val books = arrayListOf<Book>()
        for (borrowedBookDetail in borrowDetails) {
            books.add(
                bookManagementViewModel.getBook(borrowedBookDetail.bookId).also { book ->
                    bookImage = book.bookImage
                    bookName = book.name
                })
            borrowedBookModelList.add(
                BorrowedBookModel(
                    bookImage,
                    bookName,
                    calculateExtraDays(borrowedBookDetail.returnDate)
                )
            )
        }
        return borrowedBookModelList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateExtraDays(date: String): Int {
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datetime = LocalDate.parse(DateFormatter.dayToYear(date), pattern)
        val res = LocalDate.now().dayOfYear - datetime.dayOfYear
        return if (res > 0) res
        else 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModels() {
        args = BorrowedBooksFragmentArgs.fromBundle(requireArguments())
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun returnDialog(borrowDetail: BorrowDetail) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Confirm Return?")
        builder.setMessage("Confirm your return by clicking the \"confirm\" else \"cancel\" if not interested!!!")
        builder.setPositiveButton("confirm") { _, _ ->
            Toast.makeText(context, "Book has been returned Successfully", Toast.LENGTH_LONG).show()
            userTransactionViewModel.updateReturnDetails(borrowDetail)
        }
        builder.setNegativeButton("cancel") { _, _ -> }
        builder.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun returnBook(borrowDetail: BorrowDetail, item: BorrowedBookModel) {
        if (borrowDetail.dueAmount > 0) payDueDialog(
            borrowDetail,
            item.bookName,
            item.extraDays,
        )
        else returnDialog(borrowDetail)
    }

    private fun payDueDialog(
        borrowDetail: BorrowDetail,
        bookName: String,
        extraDays: Int,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Due Pending")
        builder.setMessage("Due Amount Pending!!!")
        builder.setPositiveButton("Pay") { _, _ ->
            findNavController().safeNavigate(
                BorrowedBooksFragmentDirections.actionBorrowedBooksFragmentToPayDueFragment(
                    borrowDetail,
                    bookName,
                    extraDays,
                    args.title
                )
            )
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.create().show()
    }

    override fun onClickReturn(borrowDetail: BorrowDetail, item: BorrowedBookModel) {
        returnBook(borrowDetail, item)
    }

    override fun onClickView(borrowDetail: BorrowDetail, item: BorrowedBookModel) {
        findNavController().safeNavigate(
            BorrowedBooksFragmentDirections.actionBorrowedBooksFragmentToReturnFragment(
                borrowDetail,
                item.bookName,
                item.extraDays
            )
        )
    }

    override fun onLongClickView(borrowDetail: BorrowDetail, item: BorrowedBookModel) {
        returnBook(borrowDetail, item)
    }
}
