package com.example.libraryapplication.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.data.entities.DonatedBook
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentSharedBooksBinding
import com.example.libraryapplication.ui.adapter.SharedBooksAdapter
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel
import kotlinx.coroutines.launch

class SharedBooksFragment : Fragment(),SharedBooksAdapter.ClickListener {
    private lateinit var binding: FragmentSharedBooksBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SharedBooksAdapter
    private lateinit var user: User
    private lateinit var bookManagementViewModel: BookManagementViewModel

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
            DataBindingUtil.inflate(inflater, R.layout.fragment_shared_books, container, false)
        setSharedBooksRecyclerView()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setSharedBooksRecyclerView() {
        adapter = SharedBooksAdapter()
        recyclerView = binding.sharedBookRecycler
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setClickListener(this)
        user = authenticationViewModel.getUser()
        setSharedBooksAdapter(adapter)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setSharedBooksAdapter(adapter: SharedBooksAdapter) {
        val books = arrayListOf<Book>()
        val bookList = MutableLiveData<ArrayList<Book>>()
        setDonatedBookList(bookList,books)
        bookList.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private  fun setDonatedBookList(
        bookList: MutableLiveData<java.util.ArrayList<Book>>,
        books: java.util.ArrayList<Book>
    ) {
        userTransactionViewModel.getUserDonatedBooksLive(user)
            .observe(viewLifecycleOwner) { donatedBooks ->
                setEmptyListImage(donatedBooks)
                lifecycleScope.launch {
                    for (donatedBook in donatedBooks) {
                        books.add(bookManagementViewModel.getBook(donatedBook.bookId))
                        bookList.value = books
                    }
                }
            }
    }

    private fun setEmptyListImage(donatedBooks: List<DonatedBook>) {
        if (donatedBooks.isEmpty()) {
            binding.sharedBookNotFound.visibility = View.VISIBLE
            binding.sharedBookNotFoundText.visibility = View.VISIBLE
        }
        else{
            binding.sharedBookNotFound.visibility = View.GONE
            binding.sharedBookNotFoundText.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModels() {
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun confirmDialog(bookId: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Get Back?")
        builder.setMessage("Are you sure you want to get the book from library ?")
        builder.setPositiveButton("Yes"
        ) { _, _ ->
            Toast.makeText(
                context,
                "Your book has been handed back Successfully",
                Toast.LENGTH_LONG
            ).show()
            bookManagementViewModel.removeDonatedBook(bookId)
            userTransactionViewModel.removeDonatedBookDetails(bookId)
            findNavController().navigateUp()
        }
        builder.setNegativeButton("No"
        ) { _, _ -> }
        builder.create().show()
    }
    override fun onClickGetBack(sharedBook: Book) {
        confirmDialog(sharedBook.id)
    }
}