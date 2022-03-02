package com.example.libraryapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.data.entities.Category
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentCategoryBinding
import com.example.libraryapplication.enums.Section
import com.example.libraryapplication.enums.Subject
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.adapter.CategoryAdapter
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : Fragment(), CategoryAdapter.ClickListener {
    private lateinit var academicsRecyclerView: RecyclerView
    private lateinit var nonAcademicsRecyclerView: RecyclerView
    private lateinit var donatedRecyclerView: RecyclerView
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var academicsAdapter: CategoryAdapter
    private lateinit var nonAcademicsAdapter: CategoryAdapter
    private lateinit var donatedBookAdapter: CategoryAdapter
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        user = authenticationViewModel.getUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAcademicsRecyclerView()
        setNonAcademicRecyclerView()
        setDonatedRecyclerView()
        onClickAcademicsArrow()
        onClickNonAcademicsArrow()
        onClickSharedArrow()
        onClickNonAcademicsViewText()
        onClickAcademicsViewText()
        onClickSharedViewText()

    }

    private fun onClickSharedViewText() {
        binding.sharedViewText.setOnClickListener {
            findNavController().safeNavigate(
                CategoryFragmentDirections.actionCategoryToAllBooks(Section.SHARED.reason)
            )
        }
    }

    private fun onClickAcademicsViewText() {
        binding.academicsViewText.setOnClickListener {
            findNavController().safeNavigate(
                CategoryFragmentDirections.actionSearchToSubjectFragment(Section.ACADEMICS.reason)
            )
        }
    }

    private fun onClickNonAcademicsViewText() {
        binding.nonAcademicsViewText.setOnClickListener {
            findNavController().safeNavigate(
                CategoryFragmentDirections.actionSearchToSubjectFragment(Section.NON_ACADEMICS.reason)
            )
        }
    }

    private fun initializeViewModels() {
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

    private fun onClickNonAcademicsArrow() {
        binding.nonAcademicsArrow.setOnClickListener {
            findNavController().safeNavigate(
                CategoryFragmentDirections.actionSearchToSubjectFragment(Section.NON_ACADEMICS.reason)
            )
        }
    }

    private fun onClickAcademicsArrow() {
        binding.academecisArrow.setOnClickListener {
            findNavController().safeNavigate(
                CategoryFragmentDirections.actionSearchToSubjectFragment(Section.ACADEMICS.reason)
            )
        }
    }

    private fun onClickSharedArrow() {
        binding.sharedBooksArrow.setOnClickListener {
            findNavController().safeNavigate(
                CategoryFragmentDirections.actionCategoryToAllBooks(Section.SHARED.reason)
            )
        }
    }

    private fun getCategoryImageList(categoryDetails: List<Category>): List<String> {
        val imageList = arrayListOf<String>()
        for (categoryDetail in categoryDetails) {
            imageList.add(categoryDetail.subjectImage)
        }
        return imageList
    }

    private fun getSharedBooksImage(sharedBooks: List<Book>): List<String> {
        val imageList = arrayListOf<String>()
        for (sharedBook in sharedBooks) {
            imageList.add(sharedBook.bookImage)
        }
        return imageList
    }

    private fun setAcademicsRecyclerView() {
        academicsAdapter = CategoryAdapter()
        academicsRecyclerView = binding.academicsRecyclerView
        academicsRecyclerView.adapter = academicsAdapter
        academicsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        academicsAdapter.setClickListener(this)
        setAcademicsAdapter()
    }

    private fun setAcademicsAdapter() {
        bookManagementViewModel.getCategoryDetails(Section.ACADEMICS.reason)
            .observe(viewLifecycleOwner) { categoryDetails ->
                academicsAdapter.setPreviewImageList(getCategoryImageList(categoryDetails))
                academicsAdapter.setCategoryData(categoryDetails)
            }
        bookManagementViewModel.getAllBooks().observe(
            viewLifecycleOwner
        ) { books -> academicsAdapter.setBookListData(books) }
    }

    private fun setNonAcademicRecyclerView() {
        nonAcademicsAdapter = CategoryAdapter()
        nonAcademicsRecyclerView = binding.nonAcademicsRecyclerView
        nonAcademicsRecyclerView.adapter = nonAcademicsAdapter
        nonAcademicsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        nonAcademicsAdapter.setClickListener(this)
        setNonAcademicsAdapter()
    }

    private fun setNonAcademicsAdapter() {
        bookManagementViewModel.getCategoryDetails(Section.NON_ACADEMICS.reason)
            .observe(viewLifecycleOwner) { categoryDetails ->
                nonAcademicsAdapter.setPreviewImageList(getCategoryImageList(categoryDetails))
                nonAcademicsAdapter.setCategoryData(categoryDetails)
            }
        bookManagementViewModel.getAllBooks().observe(viewLifecycleOwner) { books ->
            nonAcademicsAdapter.setBookListData(books)
        }
    }

    private fun setDonatedRecyclerView() {
        donatedBookAdapter = CategoryAdapter()
        donatedRecyclerView = binding.sharedbooksRecyclerView
        donatedRecyclerView.adapter = donatedBookAdapter
        donatedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        donatedBookAdapter.setClickListener(this)
        setDonatedBookAdapter()
    }

    private fun setEmptyImage(list: List<Book>) {
        if (list.isEmpty()) {
            binding.categoryEmptyCard.visibility = View.VISIBLE
            binding.sharedBooksArrow.visibility = View.GONE
            binding.sharedViewText.visibility=View.GONE
        } else {
            binding.categoryEmptyCard.visibility = View.GONE
            binding.sharedBooksArrow.visibility = View.VISIBLE
            binding.sharedViewText.visibility=View.VISIBLE
        }
    }

    private fun setDonatedBookAdapter() {
        val donatedBookList = MutableLiveData<List<Book>>()
        val tempDonatedBookList = arrayListOf<Book>()
        bookManagementViewModel.getSharedBookDetails()
            .observe(viewLifecycleOwner) { donatedBookDetails ->
                lifecycleScope.launch(Main) {
                    for (donatedBookDetail in donatedBookDetails) {
                        bookManagementViewModel.getBook(donatedBookDetail.bookId).let {
                            tempDonatedBookList.add(it)
                            donatedBookList.value=tempDonatedBookList
                        }
                }
                    setEmptyImage(tempDonatedBookList)
            }
            }
        donatedBookList.observe(viewLifecycleOwner) {
            donatedBookAdapter.setDonatedBooks(it)
            donatedBookAdapter.setPreviewImageList(getSharedBooksImage(it))
        }
    }

    private fun onClickSectionImage(directions: NavDirections) {
        findNavController().safeNavigate(
            directions
        )
    }

    private fun getSubject(subjectImage: String, categoryList: List<Category>): String? {
        for (category in categoryList) {
            if (category.subjectImage == subjectImage) {
                return category.subjectName
            }
        }
        return null
    }

    private fun filterDonatedBook(bookImage: String, sharedBookList: List<Book>): Book? {
        for (book in sharedBookList) {
            if (book.bookImage == bookImage) {
                return book
            }
        }
        return null
    }

    override fun onClickView(
        image: String,
        sharedBookList: List<Book>,
        categoryBookList: List<Category>
    ) {
        if (getSubject(image, categoryBookList) != null) {
            onClickSectionImage(
                CategoryFragmentDirections.actionCategoryToAllBooks(
                    getSubject(image, categoryBookList) ?: Subject.ALL.reason
                )
            )
        } else {
            onClickSectionImage(
                CategoryFragmentDirections.actionCategoryToBookInfo(
                    filterDonatedBook(image, sharedBookList)!!
                )
            )
        }
    }
}
