package com.example.libraryapplication.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
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
import com.example.libraryapplication.databinding.FragmentBookListBinding
import com.example.libraryapplication.enums.Section
import com.example.libraryapplication.enums.Subject
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.activity.MainActivity
import com.example.libraryapplication.ui.adapter.AllBooksAdapter
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookListFragment : Fragment(), AllBooksAdapter.ClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentBookListBinding
    private lateinit var adapter: AllBooksAdapter
    private lateinit var searchView: SearchView
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var args: BookListFragmentArgs
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        args = BookListFragmentArgs.fromBundle(requireArguments())
        user = authenticationViewModel.getUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_book_list, container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setAllBooksRecyclerView()
    }

    private fun initializeViewModels() {
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

    private fun setAllBooksRecyclerView() {
        adapter = AllBooksAdapter()
        recyclerView = binding.allBooksRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setClickListener(this)
        when (args.subject) {
            Subject.ALL.reason -> {
                setAllBooksAdapter()
            }
            Section.SHARED.reason -> {
                setSharedBooksAdapter()
            }
            else -> {
                setSubjectBooksAdapter()
            }
        }
    }

    private fun setSubjectBooksAdapter() {
        (requireActivity() as MainActivity).setActionBarTitle("${args.subject} Books")
        lifecycleScope.launch {
            val books = bookManagementViewModel.getSubjectsBooks(args.subject)
            setAdapter(bookManagementViewModel.getUniqueIsbnBooks(books))
        }
    }

    private fun setSharedBooksAdapter() {
        (requireActivity() as MainActivity).setActionBarTitle("Shared Books")
        val donatedBookList = MutableLiveData<List<Book>>()
        val tempDonatedBookList = arrayListOf<Book>()
        bookManagementViewModel.getSharedBookDetails()
            .observe(viewLifecycleOwner) { donatedBookDetails ->
                lifecycleScope.launch {
                    donatedBookList.value =
                        getDonatedBookList(donatedBookDetails, tempDonatedBookList)
                }
                donatedBookList.observe(viewLifecycleOwner) {
                    setAdapter(it)
                }
            }
    }

    private suspend fun getDonatedBookList(
        donatedBookDetails: List<DonatedBook>,
        tempDonatedBookList: ArrayList<Book>
    ): List<Book> {
        for (donatedBookDetail in donatedBookDetails) {
            val book = bookManagementViewModel.getBook(
                donatedBookDetail.bookId
            )
                tempDonatedBookList.add(book)
        }
        return tempDonatedBookList
    }

    private fun setAllBooksAdapter() {
        bookManagementViewModel.getAllBooks().observe(viewLifecycleOwner) { bookList ->
            (requireActivity() as MainActivity).setActionBarTitle("All Books")
            lifecycleScope.launch {
                setAdapter(bookManagementViewModel.getUniqueIsbnBooks(bookList))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuInflater = activity?.menuInflater
        menuInflater?.inflate(R.menu.search, menu)
        val menuItem = menu.findItem(R.id.search_all_books)
        if (menuItem != null) {
            searchView = menuItem.actionView as SearchView
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setSearch()
        return super.onOptionsItemSelected(item)
    }

    private fun setSearch() {
        when (args.subject) {
            Subject.ALL.reason -> setSearchBooks(Subject.ALL.reason)
            Section.SHARED.reason -> setSearchBooks(Section.SHARED.reason)
            else -> setSearchBooks()
        }
    }

    private fun searchBooks(searchType: String, query: String) {
        when (searchType) {
            Subject.ALL.reason -> searchAllBooks(query)
            Section.SHARED.reason -> searchSharedBooks(query)
            else -> searchSubjectBooks(query, args.subject)
        }
    }

    private fun setSearchBooks(searchType: String="") {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchBooks(searchType, query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchBooks(searchType, query)
                }
                return true
            }

        })
    }

    private fun searchSubjectBooks(query: String, subject: String) {
        val searchQuery = "%$query%"
        bookManagementViewModel.searchSubjectBooks(searchQuery, subject)
            .observe(this) { bookList ->
                setSearchAdapter(bookList)
            }

    }

    private fun setEmptyListVisibility(bookList: List<Book>) {
        if (bookList.isEmpty()) binding.searchNotFound.visibility = View.VISIBLE
        else binding.searchNotFound.visibility = View.GONE
    }

    private fun searchAllBooks(query: String) {
        val searchQuery = "%$query%"
        bookManagementViewModel.searchAllBooks(searchQuery).observe(
            this
        ) { bookList ->
            setSearchAdapter(bookList)
        }
    }

    private fun setSearchAdapter(bookList: List<Book>) {
        lifecycleScope.launch(Main) {
            setEmptyListVisibility(bookList)
            adapter.setData(bookManagementViewModel.getUniqueIsbnBooks(bookList))
        }
    }

    private fun searchSharedBooks(query: String) {
        val searchQuery = "%$query%"
        val bookList = arrayListOf<Book>()
        bookManagementViewModel.getSharedBookDetails()
            .observe(viewLifecycleOwner) { donatedBookDetails ->
                lifecycleScope.launch {
                    for (donatedBookDetail in donatedBookDetails) {
                        bookList.add(bookManagementViewModel.getBook(donatedBookDetail.bookId))
                    }
                    val bookIdList = arrayListOf<Int>()
                    bookList.forEach {
                        bookIdList.add(it.id)
                    }
                    bookManagementViewModel.searchSharedBooks(searchQuery, bookIdList)
                        .observe(viewLifecycleOwner) {
                            setEmptyListVisibility(it)
                            adapter.setData(it)
                        }
                }
            }
    }

    private fun setAdapter(list: List<Book>) {
        if (list.isEmpty()) binding.allBooksEmpty.visibility = View.VISIBLE
        else {
            adapter.setData(list)
            binding.allBooksEmpty.visibility = View.GONE
        }
    }

    override fun onBookClicked(book: Book, itemView: View) {
        findNavController().safeNavigate(BookListFragmentDirections.actionAllBooksToBookInfo2(book))
    }
}

/*  private fun setSearchSharedBooks() {
      searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

          override fun onQueryTextSubmit(query: String?): Boolean {
              if (query != null) {
                  searchSharedBooks(query)
              }
              return true
          }

          override fun onQueryTextChange(query: String?): Boolean {
              if (query != null) {
                  searchSharedBooks(query)
              }
              return true
          }
      })
  }

  private fun setSearchAllBooks() {
      searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
          override fun onQueryTextSubmit(query: String?): Boolean {
              searchView.hideInput()
              if (query != null) {
                  searchAllBooks(query)
              }
              return true
          }

          override fun onQueryTextChange(query: String?): Boolean {
              if (query != null) {
                  searchAllBooks(query)
              }
              return true
          }
      })
  }


    private fun setSearchSubjectBooks() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchSubjectBooks(query, args.subject)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchSubjectBooks(query, args.subject)
                }
                return true
            }

        })
    }
*/
