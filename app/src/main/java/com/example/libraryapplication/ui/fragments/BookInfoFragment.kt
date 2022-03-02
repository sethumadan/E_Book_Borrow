package com.example.libraryapplication.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.libraryapplication.R
import com.example.libraryapplication.data.entities.Book
import com.example.libraryapplication.data.entities.BorrowDetail
import com.example.libraryapplication.data.entities.FavoriteBook
import com.example.libraryapplication.data.entities.User
import com.example.libraryapplication.databinding.FragmentBookInfoBinding
import com.example.libraryapplication.enums.Availability
import com.example.libraryapplication.safenavigation.safeNavigate
import com.example.libraryapplication.ui.utils.LoadImage
import com.example.libraryapplication.viewmodels.AuthenticationViewModel
import com.example.libraryapplication.viewmodels.BookManagementViewModel
import com.example.libraryapplication.viewmodels.UserTransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class BookInfoFragment : Fragment() {
    private lateinit var binding: FragmentBookInfoBinding
    private lateinit var userTransactionViewModel: UserTransactionViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var bookManagementViewModel: BookManagementViewModel
    private lateinit var book: Book
    private lateinit var args: BookInfoFragmentArgs
    private lateinit var user: User
    private var isCalledOnce = true
    private var countUpdatedOnce = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModels()
        args = BookInfoFragmentArgs.fromBundle(
            requireArguments()
        )
        user = authenticationViewModel.getUser()
        book = args.book
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_info, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteButtonStateCheck()
        onClickButtonFavorite()
        setBookInfo()
        onClickInfoBorrow()
        lifecycleScope.launch(Main) {
            checkAndSetBorrowButton()
        }
    }

    private fun setNoOfBorrows() {
        lifecycleScope.launch {
            binding.noOfBorrowsGivenInfo.text = bookManagementViewModel.getNoOfBorrows(args.book.isbnId).toString()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickInfoBorrow() {
        binding.infoBorrow.setOnClickListener {
            userTransactionViewModel.getUserBorrowDetail(user).observe(viewLifecycleOwner) {
                checkAndSetBorrow(it)
            }
        }
    }

    private suspend fun checkAndSetBorrowButton() {
        when {
            isBookYouDonated() -> {
                binding.favoriteCardInfo.visibility=View.GONE
                binding.infoBorrow.text = resources.getString(R.string.info)
            }
            bookManagementViewModel.getAvailableCopyOfBook(book.isbnId)?.availability==Availability.UNAVAILABLE.reason -> {
                binding.infoBorrow.text=resources.getString(R.string.unavailable)
            }
            else -> {
                userTransactionViewModel.getUserBorrowDetail(user).observe(viewLifecycleOwner) {
                    lifecycleScope.launch {
                        if (isBookAlreadyBorrowed(it)) {
                            binding.infoBorrow.text = resources.getString(R.string.info)
                        }
                    }
                }
            }
        }
    }
  private suspend fun  isBookYouDonated():Boolean{
      return userTransactionViewModel.getUserDonatedBooks(user).any { it.bookId==book.id }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndSetBorrow(borrowDetails: List<BorrowDetail>) {
        lifecycleScope.launch(Main) {
            if (isCalledOnce) {
                when {
                    isBookYouDonated()->infoDialog("This book is shared by you :)","Your Book")
                    bookManagementViewModel.getAvailableCopyOfBook(book.isbnId)?.availability==Availability.UNAVAILABLE.reason->{
                        infoDialog("Book Currently Unavailable","Unavailable!!!")
                    }
                    borrowDetails.size >= userTransactionViewModel.maxBorrowLimit -> {
                        infoDialog("You have reached Maximum borrow limit","Limit Reached")
                    }
                    isBookAlreadyBorrowed(borrowDetails) -> {
                        infoDialog("Multiple copies of same book can't be borrowed.","Sorry!!!")
                    }
                    else -> {
                        confirmDialog()
                    }
                }
            }
        }
    }

    private suspend fun isBookAlreadyBorrowed(borrowDetails: List<BorrowDetail>): Boolean {
        for (borrowDetail in borrowDetails) {
            if (book.isbnId == bookManagementViewModel.getIsbn(borrowDetail.bookId)) {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickButtonFavorite() {
        binding.buttonFavorite.setOnClickListener {
            if (binding.buttonFavorite.isChecked) {
                userTransactionViewModel.insertFavorite(FavoriteBook(args.book.isbnId, user.id))
                binding.buttonFavorite.isChecked = false
            } else {
                userTransactionViewModel.deleteFavorite(FavoriteBook(args.book.isbnId, user.id))
                binding.buttonFavorite.isChecked = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun favoriteButtonStateCheck() {
        userTransactionViewModel.isFavorite(FavoriteBook(args.book.isbnId, user.id))
            .observe(viewLifecycleOwner) {
                binding.buttonFavorite.isChecked = it == 1
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModels() {
        userTransactionViewModel =
            ViewModelProvider(requireActivity())[UserTransactionViewModel::class.java]
        authenticationViewModel =
            ViewModelProvider(requireActivity())[AuthenticationViewModel::class.java]
        bookManagementViewModel =
            ViewModelProvider(requireActivity())[BookManagementViewModel::class.java]
    }

    private fun setBookInfo() {
        LoadImage.loadImage(book.bookImage, binding.infoBookImage)
        binding.infoBookNameSelected.text = book.name
        binding.infoAuthorNameSelected.text = book.authorName
        binding.infoNoOfPagesSelected.text = book.noOfPages.toString()
        binding.infoSubjectSelected.text = book.subject
        setNoOfBorrows()
        setAvailableCopies()
    }

    private fun setAvailableCopies() {
        lifecycleScope.launch(Main) {
            binding.noOfCopiesGivenInfo.text =
                bookManagementViewModel.getNoOfCopies(book.isbnId).toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun confirmDialog() {
        val borrowDate = LocalDate.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val returnDate = borrowDate.plusDays(userTransactionViewModel.initialBookTime.toLong())
        val duePerDay = userTransactionViewModel.initialDueAmount
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm borrow?")
        builder.setMessage(
            "Borrow date   :  ${formatter.format(borrowDate)}\n\n" +
                    "Return date   :  ${formatter.format(returnDate)}\n\n" +
                    "For each day after return date Rs $duePerDay will be fined"
        )
        builder.setPositiveButton(
            "Confirm"
        ) { _, _ -> borrowBook() }
        builder.setNegativeButton(
            "Cancel"
        ) { _, _ -> }
        builder.create().show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun borrowBook() {
        isCalledOnce = false
        countUpdatedOnce = true
        bookManagementViewModel.updateNoOfBorrows(book.isbnId)
        userTransactionViewModel.updateBorrowDetails(args.book, user.id)
        lifecycleScope.launchWhenResumed {
            findNavController().safeNavigate(
                BookInfoFragmentDirections.actionBookInfoToBorrowDetailFragment(
                    userTransactionViewModel.getCurrentBorrowedDetail(), book.name
                )
            )
        }
    }

    private fun infoDialog(msg: String,title: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setNeutralButton(
            "OK"
        ) { _, _ -> }
        builder.create().show()
    }
}

/*private fun sendMessage() {
    if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED){
         sendMassageGranted()
        Log.d("permission","yes")
    }
    else{
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SEND_SMS),100)
        Log.d("permission","no----->${arrayOf(Manifest.permission.SEND_SMS)}")
    }
}*/

/*override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if(requestCode==100&& grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
        sendMassageGranted()
    }
}*/
/*private fun sendMassageGranted() {
    val borrowDetail=userTransactionViewModel.getCurrentBorrowedDetail()
    val bookName=book.name
    val phNo=user.phoneNo
    val msg:String="Book Name : ${bookName}\nBorrowed Date : ${borrowDetail.borrowDate}\nReturn Date : ${borrowDetail.returnDate}"
    val smsManager=SmsManager.getDefault()
    smsManager.sendTextMessage(phNo,null,msg,null,null)
}*/

