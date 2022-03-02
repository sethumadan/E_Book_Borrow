package com.example.libraryapplication.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.databinding.FragmentSearchBinding
import com.example.libraryapplication.enums.Subject
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.activity.MainActivity
import com.example.libraryapplication.ui.adapter.AllBooksAdapter
import com.example.libraryapplication.ui.adapter.SearchAdapter
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


class SearchFragment : Fragment(), AllBooksAdapter.ClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var booksRecyclerView: RecyclerView
    private lateinit var booksAdapter: AllBooksAdapter
    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var filterAdapter: SearchAdapter
    private lateinit var subjects: ArrayList<String>
    private lateinit var states: MutableList<Boolean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        initializeSubjects()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        setFilterRecycler()
        setUpFocusChangeListeners()
        return binding.root
    }

    private fun initializeSubjects() {
        subjects = arrayListOf()
        states = arrayListOf()
        Subject.values().forEach { subject ->
            if (subject != Subject.ALL) {
                subjects.add(subject.reason)
                states.add(false)
            }
        }
    }

    private fun initializeViewModels() {
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchSearchTopLayout.isEndIconVisible = false
        if (bookManagementViewModel.isSubjectListEmpty()) showStartSearchBackgroundVisibility()
        setBooksRecyclerView()
        onClickDropIcon()
        manageKeyboard()
        onClickRecyclerView()
        searchFilteredBooks()
    }

    private fun onClickRecyclerView() {
        binding.bookSearchRecycler.setOnClickListener {
            binding.bookSearchRecycler.hideInput()
        }
    }

    private fun manageKeyboard() {
        binding.searchSearch.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    v.clearFocus()
                    v.hideInput()
                    true
                }
                else -> false
            }
        }
    }

    private fun hideStartSearchBackgroundVisibility() {
        binding.startSearchImage.visibility = View.GONE
        binding.startSearchText.visibility = View.GONE
    }

    private fun showStartSearchBackgroundVisibility() {
        binding.startSearchImage.visibility = View.VISIBLE
        binding.startSearchText.visibility = View.VISIBLE
    }

    private fun searchFilteredBooks() {
        binding.searchSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val selectedSubjects = bookManagementViewModel.getSubjectBooks()
                if (selectedSubjects.isEmpty()) {
                    searchWhenFilterEmpty(s)
                    Log.d("adapter", "searchWhenFilterIsEmpty")
                } else {
                    Log.d("adapter", "NotEmpty")
                    searchWhenFilterNotEmpty(s, selectedSubjects)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun searchWhenFilterNotEmpty(s: CharSequence?, selectedSubjects: List<String>) {
        bookManagementViewModel.searchFilteredBooks(
            selectedSubjects,
            "%${s}%"
        ).observe(viewLifecycleOwner) {
            lifecycleScope.launch(Main) {
                val books = bookManagementViewModel.getUniqueIsbnBooks(it)
                booksAdapter.setData(books)
                setNoItemFoundImage(books, s)
                binding.searchSearchTopLayout.isEndIconVisible = true
            }
        }
    }

    private fun searchWhenFilterEmpty(s: CharSequence?) {
        bookManagementViewModel.searchFilteredBooks(subjects, "%${s}%")
            .observe(viewLifecycleOwner) {
                lifecycleScope.launch(Main) {
                    val books = bookManagementViewModel.getUniqueIsbnBooks(it)
                    setBookAdapter(books, s)
                    setNoItemFoundImage(books, s)
                }
            }
    }

    private fun setNoItemFoundImage(list: List<Book>, s: CharSequence?) {
        if (list.isEmpty() && !s.isNullOrEmpty()) binding.searchNotFoundImage.visibility =
            View.VISIBLE
        else binding.searchNotFoundImage.visibility = View.GONE
    }

    private fun setBookAdapter(books: List<Book>, s: CharSequence?) {
        if (s.isNullOrEmpty()) {
            showStartSearchBackgroundVisibility()
            booksAdapter.setData(emptyList())
        } else {
            hideStartSearchBackgroundVisibility()
            booksAdapter.setData(books)
        }
    }

    private fun setBooksRecyclerView() {
        booksAdapter = AllBooksAdapter()
        booksRecyclerView = binding.bookSearchRecycler
        booksRecyclerView.adapter = booksAdapter
        booksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        booksAdapter.setClickListener(this)
    }

    private fun onClickDropIcon() {
        var isDown = true
        binding.filterLeftIcon.setOnClickListener {
            isDown = setDropIconState(isDown)
            setSearchCardVisibility()
        }
        binding.dropIconFilter.setOnClickListener {
            isDown = setDropIconState(isDown)
            setSearchCardVisibility()
        }
    }

    private fun setDropIconState(isDown: Boolean): Boolean {
        return if (isDown) {
            setDropIconUp()
            false
        } else {
            setDropIconDown()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    private fun setSearchCardVisibility() {
        if (binding.cardSearhSearch.visibility == View.GONE)
            binding.cardSearhSearch.visibility = View.VISIBLE
        else
            binding.cardSearhSearch.visibility = View.GONE
    }

    private fun setDropIconUp() {
        binding.dropIconFilter.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_baseline_done_24
            )
        )
    }

    private fun setDropIconDown() {
        binding.dropIconFilter.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_baseline_arrow_drop_down_24
            )
        )
        binding.searchSearch.text = null
        if (!bookManagementViewModel.isSubjectListEmpty()) {
            hideStartSearchBackgroundVisibility()
            binding.searchNotFoundImage.visibility = View.GONE
            searchFilteredBooks()
        } else showStartSearchBackgroundVisibility()
        setBookList()
    }

    private fun setFilterRecycler() {
        filterAdapter = SearchAdapter{ subject, isChecked ->
            if (isChecked) {
                bookManagementViewModel.addSubject(subject)
            } else {
                bookManagementViewModel.removeSubject(subject)
            }
        }
        filterRecyclerView = binding.searchRecycler
        filterRecyclerView.adapter = filterAdapter
        filterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        filterAdapter.setData(subjects, states)
    }

    private fun setBookList() {
        val selectedSubjects = bookManagementViewModel.getSubjectBooks()
        val books = bookManagementViewModel.getFilteredBooks(selectedSubjects)
        books.observe(viewLifecycleOwner) {
            Log.d("adapter", "setBookList${it.size}")
            booksAdapter.setData(it)

        }
    }

    private fun setUpFocusChangeListeners() {
        binding.searchSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                activity?.onBackPressedDispatcher?.addCallback(
                    viewLifecycleOwner,
                    object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            binding.searchSearch.clearFocus()
                        }
                    })
                binding.searchSearchTopLayout.setEndIconTintList(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary
                        )
                    )
                )
            } else {
                activity?.onBackPressedDispatcher?.addCallback(
                    viewLifecycleOwner,
                    object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            findNavController().navigateUp()
                        }
                    })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bookManagementViewModel.emptySubjectList()
        binding.searchSearch.hideInput()

    }

    override fun onBookClicked(book: Book, itemView: View) {
        itemView.hideInput()
        findNavController().safeNavigate(SearchFragmentDirections.actionSearchFragmentToBookInfo(book))
    }
}
