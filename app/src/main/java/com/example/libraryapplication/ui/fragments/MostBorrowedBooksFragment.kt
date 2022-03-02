package com.example.libraryapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.data.entities.MostBorrowed
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentMostlyBorrowedBinding
import com.example.libraryapplication.enums.Availability
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.adapter.MostBorrowedListAdapter
import com.example.libraryapplication.ui.adapter.model.MostBorrowedAdapterModel
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel

class MostBorrowedBooksFragment : Fragment(), MostBorrowedListAdapter.ClickListener {
    private lateinit var binding: FragmentMostlyBorrowedBinding
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MostBorrowedListAdapter
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_mostly_borrowed, container, false)
        setMostBorrowedBooksRecyclerView()
        user = authenticationViewModel.getUser()
        return binding.root
    }

    private fun setMostBorrowedBooksRecyclerView() {
        adapter = MostBorrowedListAdapter()
        recyclerView = binding.mostBorrowedRecycler
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setClickListener(this)
        val mostBorrowedBookModels = MutableLiveData<List<MostBorrowedAdapterModel>>()
        val tempMostBorrowedAdapterModel = arrayListOf<MostBorrowedAdapterModel>()
        setMostBorrowedBookModels(mostBorrowedBookModels, tempMostBorrowedAdapterModel)
        setMostBorrowedAdapter(mostBorrowedBookModels)

    }

    private fun setMostBorrowedBookModels(
        mostBorrowedBookModels: MutableLiveData<List<MostBorrowedAdapterModel>>,
        tempMostBorrowedAdapterModel: java.util.ArrayList<MostBorrowedAdapterModel>
    ) {
        bookManagementViewModel.getMostBorrowedBooks()
            .observe(viewLifecycleOwner) { mostBorrowedBooks ->
                for (mostBorrowedBook in mostBorrowedBooks) {
                    bookManagementViewModel.getLiveCopiesOfBook(mostBorrowedBook.isbnId)
                        .observe(viewLifecycleOwner) { Books ->
                            setMostBorrowedAdapterModel(
                                Books,
                                mostBorrowedBook,
                                tempMostBorrowedAdapterModel
                            )
                            mostBorrowedBookModels.value = tempMostBorrowedAdapterModel
                        }

                }
            }
    }

    private fun setMostBorrowedAdapter(mostBorrowedBookModels: MutableLiveData<List<MostBorrowedAdapterModel>>) {
        mostBorrowedBookModels.observe(viewLifecycleOwner) {
            if (it.isEmpty()) binding.initiate.visibility = View.VISIBLE
            else {
                binding.initiate.visibility = View.GONE
            }
            adapter.setData(it)
        }
    }

    private fun setMostBorrowedAdapterModel(
        books: List<Book>,
        mostBorrowedBook: MostBorrowed,
        tempMostBorrowedAdapterModel: ArrayList<MostBorrowedAdapterModel>
    ) {
        var isAdded = false
        for (book in books) {
            if (book.availability == Availability.AVAILABLE.reason) {
                tempMostBorrowedAdapterModel.add(
                    MostBorrowedAdapterModel(
                        book, mostBorrowedBook.noOfBorrows
                    )
                )
                isAdded = true
                break
            }
        }
        if (!isAdded) {
            if (books.isNotEmpty())
                tempMostBorrowedAdapterModel.add(
                    MostBorrowedAdapterModel(
                        books[0],
                        mostBorrowedBook.noOfBorrows
                    )
                )
        }
    }

    private fun initializeViewModels() {
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

    override fun onClickView(mostBorrowedBook: MostBorrowedAdapterModel) {
        findNavController().safeNavigate(
            MostBorrowedBooksFragmentDirections.actionMostlyBorrowedFragmentToBookInfo(
                mostBorrowedBook.book
            )
        )
    }

   /* override fun onClickGetBack(mostBorrowedBook: MostBorrowedAdapterModel) {
        findNavController().safeNavigate(
            MostBorrowedBooksFragmentDirections.actionMostlyBorrowedFragmentToBookInfo(
                mostBorrowedBook.book
            )
        )
    }*/
}