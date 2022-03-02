package com.example.libraryapplication.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentShareBookBinding
import com.example.libraryapplication.enums.Availability
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.utils.BitmapConverter
import com.example.libraryapplication.ui.utils.hideInput
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel
import com.example.libraryapplication.viewmodels.response.Report
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


class ShareBookFragment : Fragment() {
    private lateinit var binding: FragmentShareBookBinding
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private var bookImage: String? = null
    private lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        user = authenticationViewModel.getUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share_book, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageKeyBoard()
        setUpFocusChangeListeners()
        onClickShareBookImage()
        setSubjectAdapter()
        onClickShareBookButton()
    }

    private fun setSubjectAdapter() {
        val subjects = resources.getStringArray(R.array.Subjects)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.subject_drop_down, subjects)
        binding.shareBookSubjectName.setAdapter(arrayAdapter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickShareBookButton() {
        binding.button.setOnClickListener {
            val result = bookManagementViewModel.isValidBook(
                binding.shareBookBookName.text.toString(),
                binding.shareBookSubjectName.text.toString(),
                binding.shareBookAuthorName.text.toString(),
                binding.shareBookNoOfPages.text.toString(),
                bookImage
            )
            when (result) {
                Report.BookCheckResponse.SUCCESS -> {
                    setSuccessResponse()
                }
                Report.BookCheckResponse.UN_FILLED_ROWS -> {
                    checkUnfilledBox()
                }
                Report.BookCheckResponse.INVALID_PAGE_NO->{
                    binding.shareBookNoOfPagesLayout.error = Report.BookCheckResponse.INVALID_PAGE_NO.reason
                }
                else -> {
                    Toast.makeText(requireContext(), result.reason, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkUnfilledBox() {
        when {
            binding.shareBookBookName.text.isNullOrEmpty() -> binding.shareBookBookNameLayout.error =
                Report.BookCheckResponse.BOOK_NAME_EMPTY.reason
            else -> binding.shareBookBookNameLayout.error = null
        }

        when {
            binding.shareBookSubjectName.text.isNullOrEmpty() -> binding.shareBookSubjectNameTopLayout.error =
                Report.BookCheckResponse.SUBJECT_EMPTY.reason
            else -> binding.shareBookSubjectNameTopLayout.error = null
        }

        when {
            binding.shareBookAuthorName.text.isNullOrEmpty() -> binding.shareBookAuthorNameLayout.error =
                Report.BookCheckResponse.AUTHOR_EMPTY.reason
            else -> binding.shareBookAuthorNameLayout.error = null
        }

        when {
            binding.shareBookNoOfPages.text.isNullOrEmpty() -> binding.shareBookNoOfPagesLayout.error =
                Report.BookCheckResponse.NO_OF_PAGES_EMPTY.reason
            else -> binding.shareBookNoOfPagesLayout.error = null
        }
    }

    private fun setUpFocusChangeListeners() {
        binding.shareBookBookName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.shareBookBookNameLayout.error = null
        }
        binding.shareBookSubjectName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.shareBookBookNameLayout.error = null
        }
        binding.shareBookAuthorName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.shareBookAuthorNameLayout.error = null
        }
        binding.shareBookNoOfPages.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.shareBookNoOfPagesLayout.error = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setSuccessResponse() {
        val book = Book(
            0,
            binding.shareBookBookName.text.toString(),
            binding.shareBookAuthorName.text.toString(),
            binding.shareBookSubjectName.text.toString(),
            binding.shareBookNoOfPages.text.toString().toInt(),
            Availability.AVAILABLE.reason,
            bookImage!!,
            0
        )
        lifecycleScope.launch(Main) {
            val bookId = updateBookDetails(book)
            findNavController().safeNavigate(
                ShareBookFragmentDirections.actionShareBookFragmentToShareBookDetailFragment(
                    bookId
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateBookDetails(book:Book):Int {
        val bookId = bookManagementViewModel.insertBook(book).toInt()
        bookManagementViewModel.setSharedBookIsbn(bookId)
        userTransactionViewModel.insertSharedBookDetail(user.id, bookId)
        bookManagementViewModel.insertMostBorrow(bookId)
        return bookId
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun onClickShareBookImage() {
        binding.shareBookImage.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            )
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModels() {
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageURI: Uri = data?.data ?: return@registerForActivityResult
             //   Bitmap bitmap = MediaStore.Images.Media.getBitmap(.getContentResolver(), filePath1);
                val bit=MediaStore.Images.Media.getBitmap(requireContext().contentResolver,selectedImageURI)
              /*  val source: ImageDecoder.Source = ImageDecoder.createSource(
                    requireContext().contentResolver,
                    selectedImageURI
                )
                val bitmap = ImageDecoder.decodeBitmap(source)*/
                bookImage = BitmapConverter.encodeImage(bit)
                binding.shareBookImage.setImageBitmap(BitmapConverter.convert(bookImage!!))
            }
        }


    private fun manageKeyBoard() {
        binding.shareBookNoOfPages.setOnEditorActionListener { v, actionId, _ ->
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

}
